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

package fr.ybo.transportsbordeaux.donnees;

import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsbordeaux.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.modele.Arret;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.ArretRoute;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.modele.Direction;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.util.ConstantesTbc;
import fr.ybo.transportsbordeaux.util.LogYbo;

public final class UpdateDataBase {

	private static final LogYbo LOG_YBO = new LogYbo(UpdateDataBase.class);

	public static void updateIfNecessaryDatabase(Resources resources) {
		LOG_YBO.debug("Mise à jour des données Keolis...");
		DernierMiseAJour dernierMiseAJour = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(
				new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipTbc.getLastUpdate(resources);
		if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null ||
				dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			LOG_YBO.debug("Mise à jour disponible, lancement de la mise à jour");
			LOG_YBO.debug("Suppression de toutes les tables sauf les tables de favoris.");
			for (Class<?> clazz : ConstantesTbc.CLASSES_DB_TO_DELETE_ON_UPDATE) {
				TransportsBordeauxApplication.getDataBaseHelper().deleteAll(clazz);
			}
			LOG_YBO.debug("Mise à jour des donnees");
			GestionZipTbc.getAndParseZipKeolis(new MoteurCsv(ConstantesTbc.LIST_CLASSES_GTFS), resources);
			LOG_YBO.debug("Mise à jour des arrêts favoris suite à la mise à jour.");
			Ligne ligneSelect = new Ligne();
			Arret arretSelect = new Arret();
			ArretRoute arretRouteSelect = new ArretRoute();
			Direction directionSelect = new Direction();
			List<ArretFavori> favoris = TransportsBordeauxApplication.getDataBaseHelper().select(new ArretFavori());
			TransportsBordeauxApplication.getDataBaseHelper().deleteAll(ArretFavori.class);
			for (ArretFavori favori : favoris) {
				ligneSelect.id = favori.ligneId;
				Ligne ligne = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(ligneSelect);
				arretSelect.id = favori.arretId;
				Arret arret = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(arretSelect);
				arretRouteSelect.ligneId = favori.ligneId;
				arretRouteSelect.arretId = favori.arretId;
				arretRouteSelect.macroDirection = favori.macroDirection;
				ArretRoute arretRoute = TransportsBordeauxApplication.getDataBaseHelper()
						.selectSingle(arretRouteSelect);
				if (ligne == null || arret == null || arretRoute == null) {
					LOG_YBO.debug("Le favori avec arretId = " + favori.arretId + ", ligneId = " + favori.ligneId +
							" n'a plus de correspondances dans la base -> suppression");
					TransportsBordeauxApplication.getDataBaseHelper().delete(favori);
				} else {
					directionSelect.id = arretRoute.directionId;
					favori.direction = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(directionSelect).direction;
					favori.nomArret = arret.nom;
					favori.nomCourt = ligne.nomCourt;
					favori.nomLong = ligne.nomLong;
					TransportsBordeauxApplication.getDataBaseHelper().insert(favori);
				}
			}
			DernierMiseAJour miseAJour = new DernierMiseAJour();
			miseAJour.derniereMiseAJour = dateDernierFichierKeolis;
			TransportsBordeauxApplication.getDataBaseHelper().insert(miseAJour);
		}
		TransportsBordeauxApplication.getDataBaseHelper().endTransaction();
		TransportsBordeauxApplication.getDataBaseHelper().close();
	}

	private UpdateDataBase() {
	}

}
