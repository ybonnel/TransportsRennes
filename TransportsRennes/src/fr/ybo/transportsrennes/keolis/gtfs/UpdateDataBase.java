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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.keolis.gtfs;

import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.LigneInexistanteException;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Direction;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.LogYbo;

public final class UpdateDataBase {

	private static final LogYbo LOG_YBO = new LogYbo(UpdateDataBase.class);

	public static void updateIfNecessaryDatabase(Resources resources) {
		LOG_YBO.debug("Mise à jour des données Keolis...");
		DernierMiseAJour dernierMiseAJour = TransportsRennesApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(resources);
		if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null ||
				dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			LOG_YBO.debug("Mise à jour disponible, lancement de la mise à jour");
			LOG_YBO.debug("Suppression des lignes chargées");
			for (Ligne ligne : TransportsRennesApplication.getDataBaseHelper().select(new Ligne())) {
				if (ligne.isChargee()) {
					try {
						TransportsRennesApplication.getDataBaseHelper().getWritableDatabase().execSQL("DROP TABLE Horaire_" + ligne.id);
					} catch (SQLiteException ignored) {}
				}
			}
			LOG_YBO.debug("Suppression de toutes les tables sauf les tables de favoris.");
			for (Class<?> clazz : ConstantesKeolis.CLASSES_DB_TO_DELETE_ON_UPDATE) {
				TransportsRennesApplication.getDataBaseHelper().deleteAll(clazz);
			}
			LOG_YBO.debug("Mise à jour des donnees");
			GestionZipKeolis.getAndParseZipKeolis(new MoteurCsv(ConstantesKeolis.LIST_CLASSES_GTFS), resources);
			LOG_YBO.debug("Mise à jour des arrêts favoris suite à la mise à jour.");
			Ligne ligneSelect = new Ligne();
			Arret arretSelect = new Arret();
			ArretRoute arretRouteSelect = new ArretRoute();
			Direction directionSelect = new Direction();
			List<ArretFavori> favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori());
			TransportsRennesApplication.getDataBaseHelper().deleteAll(ArretFavori.class);
			for (ArretFavori favori : favoris) {
				ligneSelect.id = favori.ligneId;
				Ligne ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligneSelect);
				arretSelect.id = favori.arretId;
				Arret arret = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretSelect);
				arretRouteSelect.ligneId = favori.ligneId;
				arretRouteSelect.arretId = favori.arretId;
				arretRouteSelect.macroDirection = favori.macroDirection;
				ArretRoute arretRoute = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretRouteSelect);
				if (ligne == null || arret == null || arretRoute == null) {
					LOG_YBO.debug("Le favori avec arretId = " + favori.arretId + ", ligneId = " + favori.ligneId +
							" n'a plus de correspondances dans la base -> suppression");
					TransportsRennesApplication.getDataBaseHelper().delete(favori);
				} else {
					directionSelect.id = arretRoute.directionId;
					favori.direction = TransportsRennesApplication.getDataBaseHelper().selectSingle(directionSelect).direction;
					favori.nomArret = arret.nom;
					favori.nomCourt = ligne.nomCourt;
					favori.nomLong = ligne.nomLong;
					TransportsRennesApplication.getDataBaseHelper().insert(favori);
				}
			}
			DernierMiseAJour miseAJour = new DernierMiseAJour();
			miseAJour.derniereMiseAJour = dateDernierFichierKeolis;
			TransportsRennesApplication.getDataBaseHelper().insert(miseAJour);
		}
		TransportsRennesApplication.getDataBaseHelper().endTransaction();
		TransportsRennesApplication.getDataBaseHelper().close();
	}

	public static void chargeDetailLigne(Ligne ligne, Resources resources) throws LigneInexistanteException {
		LOG_YBO.debug("Chargement en base de la ligne : " + ligne.nomCourt);
		try {
			TransportsRennesApplication.getDataBaseHelper().beginTransaction();
			ligne.chargerHeuresArrets(TransportsRennesApplication.getDataBaseHelper(), resources);
			ligne.chargee = Boolean.TRUE;
			TransportsRennesApplication.getDataBaseHelper().update(ligne);
		} finally {
			TransportsRennesApplication.getDataBaseHelper().endTransaction();
		}
		LOG_YBO.debug("Chargement en base de la ligne terminée");
	}

	private UpdateDataBase() {
	}

}
