/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportscommun.donnees.manager.gtfs;

import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import fr.ybo.database.DataBaseException;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.moteurcsv.exception.MoteurCsvException;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.donnees.Constantes;
import fr.ybo.transportscommun.donnees.manager.LigneInexistanteException;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.ArretRoute;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.LoadingInfo;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportscommun.util.NoSpaceLeftException;

public final class UpdateDataBase {

	private static final LogYbo LOG_YBO = new LogYbo(UpdateDataBase.class);

	private static boolean majDatabaseEncours = false;

	public static boolean isMajDatabaseEncours() {
		return majDatabaseEncours;
	}

	public static void setMajDatabaseEncours(boolean majDatabaseEncours) {
		UpdateDataBase.majDatabaseEncours = majDatabaseEncours;
	}

	public static void updateIfNecessaryDatabase(int lastUpdate, Resources resources, LoadingInfo loadingInfo)
			throws GestionFilesException, MoteurCsvException, DataBaseException, NoSpaceLeftException {
		LOG_YBO.debug("Mise à jour des données Keolis...");
		DernierMiseAJour dernierMiseAJour = AbstractTransportsApplication.getDataBaseHelper().selectSingle(
				new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(resources, lastUpdate);
		if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null
				|| dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			majDatabaseEncours = true;
			try {
				LOG_YBO.debug("Mise à jour disponible, lancement de la mise à jour");
				LOG_YBO.debug("Suppression des lignes chargées");
				loadingInfo.setNbEtape(9);
				for (Ligne ligne : AbstractTransportsApplication.getDataBaseHelper().select(new Ligne())) {
					if (ligne.isChargee()) {
						try {
							AbstractTransportsApplication.getDataBaseHelper().getWritableDatabase()
									.execSQL("DROP TABLE Horaire_" + ligne.id);
						} catch (SQLiteException ignored) {
						}
					}
				}
				loadingInfo.etapeSuivante();
				LOG_YBO.debug("Suppression de toutes les tables sauf les tables de favoris.");
				for (Class<?> clazz : Constantes.CLASSES_DB_TO_DELETE_ON_UPDATE) {
					AbstractTransportsApplication.getDataBaseHelper().deleteAll(clazz);
				}
				loadingInfo.etapeSuivante();
				LOG_YBO.debug("Mise à jour des donnees");
				GestionZipKeolis.getAndParseZipKeolis(new MoteurCsv(Constantes.LIST_CLASSES_GTFS), resources,
						loadingInfo);
				LOG_YBO.debug("Mise à jour des arrêts favoris suite à la mise à jour.");
				Ligne ligneSelect = new Ligne();
				Arret arretSelect = new Arret();
				ArretRoute arretRouteSelect = new ArretRoute();
				Direction directionSelect = new Direction();
				List<ArretFavori> favoris = AbstractTransportsApplication.getDataBaseHelper().select(new ArretFavori());
				AbstractTransportsApplication.getDataBaseHelper().deleteAll(ArretFavori.class);
				for (ArretFavori favori : favoris) {
					ligneSelect.id = favori.ligneId;
					Ligne ligne = AbstractTransportsApplication.getDataBaseHelper().selectSingle(ligneSelect);
					arretSelect.id = favori.arretId;
					Arret arret = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arretSelect);
					arretRouteSelect.ligneId = favori.ligneId;
					arretRouteSelect.arretId = favori.arretId;
					arretRouteSelect.macroDirection = favori.macroDirection;
					ArretRoute arretRoute = AbstractTransportsApplication.getDataBaseHelper().selectSingle(
							arretRouteSelect);
					if (ligne == null || arret == null || arretRoute == null) {
						LOG_YBO.debug("Le favori avec arretId = " + favori.arretId + ", ligneId = " + favori.ligneId
								+ " n'a plus de correspondances dans la base -> suppression");
						AbstractTransportsApplication.getDataBaseHelper().delete(favori);
					} else {
						directionSelect.id = arretRoute.directionId;
						favori.direction = AbstractTransportsApplication.getDataBaseHelper().selectSingle(
								directionSelect).direction;
						favori.nomArret = arret.nom;
						favori.nomCourt = ligne.nomCourt;
						favori.nomLong = ligne.nomLong;
						AbstractTransportsApplication.getDataBaseHelper().insert(favori);
					}
				}
				DernierMiseAJour miseAJour = new DernierMiseAJour();
				miseAJour.derniereMiseAJour = dateDernierFichierKeolis;
				AbstractTransportsApplication.getDataBaseHelper().insert(miseAJour);
				loadingInfo.etapeSuivante();
			} finally {
				majDatabaseEncours = false;
			}
		}
		AbstractTransportsApplication.getDataBaseHelper().endTransaction();
		AbstractTransportsApplication.getDataBaseHelper().close();
	}

	public static void chargeDetailLigne(Class<?> rawClass, Ligne ligne, Resources resources)
			throws LigneInexistanteException, NoSpaceLeftException {
		LOG_YBO.debug("Chargement en base de la ligne : " + ligne.nomCourt);
		try {
			AbstractTransportsApplication.getDataBaseHelper().beginTransaction();
			ligne.chargerHeuresArrets(rawClass, AbstractTransportsApplication.getDataBaseHelper(), resources);
			ligne.chargee = Boolean.TRUE;
			AbstractTransportsApplication.getDataBaseHelper().update(ligne);
		} finally {
			AbstractTransportsApplication.getDataBaseHelper().endTransaction();
		}
		LOG_YBO.debug("Chargement en base de la ligne terminée");
	}

	private UpdateDataBase() {
	}

}
