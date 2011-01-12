/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes.keolis.gtfs;

import android.content.ContentValues;
import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.*;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.Date;
import java.util.List;

public final class UpdateDataBase {

	private static final LogYbo LOG_YBO = new LogYbo(UpdateDataBase.class);

	public static void updateIfNecessaryDatabase() {
		LOG_YBO.debug("Mise à jour des données Keolis...");
		DernierMiseAJour dernierMiseAJour = TransportsRennesApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate();
		if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null ||
				dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			LOG_YBO.debug("Mise à jour disponible, lancement de la mise à jour");
			LOG_YBO.debug("Suppression des lignes chargées");
			for (Ligne ligne : TransportsRennesApplication.getDataBaseHelper().select(new Ligne())) {
				 if (ligne.chargee != null
						 && ligne.chargee) {
					  TransportsRennesApplication.getDataBaseHelper().getWritableDatabase().execSQL("DROP TABLE Horaire_" + ligne.id);
				 }
			}
			LOG_YBO.debug("Suppression de toutes les tables sauf les tables de favoris.");
			for (final Class<?> clazz : ConstantesKeolis.LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE) {
				TransportsRennesApplication.getDataBaseHelper().deleteAll(clazz);
			}
			LOG_YBO.debug("Mise à jour des donnees");
			GestionZipKeolis.getAndParseZipKeolis(new MoteurCsv(ConstantesKeolis.LIST_CLASSES_GTFS));
			LOG_YBO.debug("Mise à jour des arrêts favoris suite à la mise à jour.");
			Ligne ligneSelect = new Ligne();
			Ligne ligne;
			Arret arretSelect = new Arret();
			Arret arret;
			ArretRoute arretRouteSelect = new ArretRoute();
			ArretRoute arretRoute;
			Direction directionSelect = new Direction();
			List<ArretFavori> favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori());
			TransportsRennesApplication.getDataBaseHelper().deleteAll(ArretFavori.class);
			for (ArretFavori favori : favoris) {
				ligneSelect.id = favori.ligneId;
				ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligneSelect);
				arretSelect.id = favori.arretId;
				arret = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretSelect);
				arretRouteSelect.ligneId = favori.ligneId;
				arretRouteSelect.arretId = favori.arretId;
				arretRoute = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretRouteSelect);
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
			dernierMiseAJour = new DernierMiseAJour();
			dernierMiseAJour.derniereMiseAJour = dateDernierFichierKeolis;
			TransportsRennesApplication.getDataBaseHelper().insert(dernierMiseAJour);
		}
		TransportsRennesApplication.getDataBaseHelper().endTransaction();
		TransportsRennesApplication.getDataBaseHelper().close();
	}

	public static void chargeDetailLigne(Ligne ligne) {
		LOG_YBO.debug("Chargement en base de la ligne : " + ligne.nomCourt);
		try {
			TransportsRennesApplication.getDataBaseHelper().beginTransaction();
			ligne.chargerHeuresArrets(TransportsRennesApplication.getDataBaseHelper());
			ligne.chargee = Boolean.TRUE;
			final ContentValues values = new ContentValues();
			values.put("chargee", 1);
			final String[] whereArgs = new String[1];
			whereArgs[0] = ligne.id;
			TransportsRennesApplication.getDataBaseHelper().getWritableDatabase().update("Ligne", values, "id = :id", whereArgs);
		} finally {
			TransportsRennesApplication.getDataBaseHelper().endTransaction();
		}
		LOG_YBO.debug("Chargement en base de la ligne terminée");
	}

	private UpdateDataBase() {
	}

}
