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
		if (dernierMiseAJour == null || dernierMiseAJour.getDerniereMiseAJour() == null ||
				dateDernierFichierKeolis.after(dernierMiseAJour.getDerniereMiseAJour())) {
			LOG_YBO.debug("Mise à jour disponible, lancement de la mise à jour");
			LOG_YBO.debug("Suppression des routes chargées");
			for (Route route : TransportsRennesApplication.getDataBaseHelper().select(new Route())) {
				 if (route.getChargee() != null
						 && route.getChargee().booleanValue()) {
					  TransportsRennesApplication.getDataBaseHelper().getWritableDatabase().execSQL("DROP TABLE HeuresArrets" + route.getIdWithoutSpecCar());
				 }
			}
			LOG_YBO.debug("Suppression de toutes les tables sauf les tables de favoris.");
			for (final Class<?> clazz : ConstantesKeolis.LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE) {
				TransportsRennesApplication.getDataBaseHelper().deleteAll(clazz);
			}
			LOG_YBO.debug("Mise à jour des donnees");
			GestionZipKeolis.getAndParseZipKeolis(new MoteurCsv(ConstantesKeolis.LIST_CLASSES_GTFS));
			LOG_YBO.debug("Mise à jour des arrêts favoris suite à la mise à jour.");
			Route routeSelect = new Route();
			Route route;
			Arret arretSelect = new Arret();
			Arret arret;
			ArretRoute arretRouteSelect = new ArretRoute();
			ArretRoute arretRoute;
			List<ArretFavori> favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori());
			TransportsRennesApplication.getDataBaseHelper().deleteAll(ArretFavori.class);
			for (ArretFavori favori : favoris) {
				routeSelect.setId(favori.getRouteId());
				route = TransportsRennesApplication.getDataBaseHelper().selectSingle(routeSelect);
				arretSelect.setId(favori.getStopId());
				arret = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretSelect);
				arretRouteSelect.setRouteId(favori.getRouteId());
				arretRouteSelect.setArretId(favori.getStopId());
				arretRoute = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretRouteSelect);
				if (route == null || arret == null || arretRoute == null) {
					LOG_YBO.debug("Le favori avec stopId = " + favori.getStopId() + ", routeId = " + favori.getRouteId() +
							" n'a plus de correspondances dans la base -> suppression");
					TransportsRennesApplication.getDataBaseHelper().delete(favori);
				} else {
					favori.setDirection(arretRoute.getDirection());
					favori.setNomArret(arret.getNom());
					favori.setRouteNomCourt(route.getNomCourt());
					favori.setRouteNomLong(route.getNomLong());
					TransportsRennesApplication.getDataBaseHelper().insert(favori);
				}
			}
			dernierMiseAJour = new DernierMiseAJour();
			dernierMiseAJour.setDerniereMiseAJour(dateDernierFichierKeolis);
			TransportsRennesApplication.getDataBaseHelper().insert(dernierMiseAJour);
		}
		TransportsRennesApplication.getDataBaseHelper().endTransaction();
		TransportsRennesApplication.getDataBaseHelper().close();
	}

	public static void chargeDetailRoute(Route route) {
		LOG_YBO.debug("Chargement en base de la route : " + route.getNomCourt());
		try {
			TransportsRennesApplication.getDataBaseHelper().beginTransaction();
			route.chargerHeuresArrets(TransportsRennesApplication.getDataBaseHelper());
			route.setChargee(Boolean.TRUE);
			final ContentValues values = new ContentValues();
			values.put("chargee", 1);
			final String[] whereArgs = new String[1];
			whereArgs[0] = route.getId();
			TransportsRennesApplication.getDataBaseHelper().getWritableDatabase().update("Route", values, "id = :id", whereArgs);
		} finally {
			TransportsRennesApplication.getDataBaseHelper().endTransaction();
		}
		LOG_YBO.debug("Chargement en base de la route terminée");
	}

	private UpdateDataBase() {
	}

}
