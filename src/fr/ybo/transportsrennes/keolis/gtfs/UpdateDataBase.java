package fr.ybo.transportsrennes.keolis.gtfs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import fr.ybo.transportsrennes.BusRennesApplication;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.Date;

public final class UpdateDataBase {

	private static final LogYbo LOG_YBO = new LogYbo(UpdateDataBase.class);

	public static void updateIfNecessaryDatabase(DataBaseHelper dataBaseHelper, Context context,
	                                             ProgressDialog progressDialog, Activity currentActivity) {
		LOG_YBO.debug("Mise à jour des données Keolis...");
		DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate();
		if (dernierMiseAJour == null || dernierMiseAJour.getDerniereMiseAJour() == null || dateDernierFichierKeolis
				.after(dernierMiseAJour.getDerniereMiseAJour())) {
			LOG_YBO.debug("Mise à jour disponible, lancement de la mise à jour");
			LOG_YBO.debug("Suppression de toutes les tables");
			for (final Class<?> clazz : ConstantesKeolis.LIST_CLASSES_DATABASE) {
				dataBaseHelper.deleteAll(clazz);
			}
			LOG_YBO.debug("Mise à jour des donnees");
			GestionZipKeolis.getAndParseZipKeolis(new MoteurCsv(ConstantesKeolis.LIST_CLASSES_GTFS), dataBaseHelper, progressDialog, currentActivity);
			dernierMiseAJour = new DernierMiseAJour();
			dernierMiseAJour.setDerniereMiseAJour(dateDernierFichierKeolis);
			dataBaseHelper.insert(dernierMiseAJour);
		}
		dataBaseHelper.endTransaction();
		dataBaseHelper.close();
	}

	public static void chargeDetailRoute(Route route, Context context) {
		LOG_YBO.debug("Chargement en base de la route : " + route.getNomCourt());
		BusRennesApplication.getDataBaseHelper().beginTransaction();
		route.chargerHeuresArrets(context, BusRennesApplication.getDataBaseHelper());
		route.setChargee(Boolean.TRUE);
		final ContentValues values = new ContentValues();
		values.put("chargee", 1);
		final String[] whereArgs = new String[1];
		whereArgs[0] = route.getId();
		BusRennesApplication.getDataBaseHelper().getWritableDatabase().update("Route", values, "id = :id", whereArgs);
		BusRennesApplication.getDataBaseHelper().endTransaction();
		LOG_YBO.debug("Chargement en base de la route terminée");
	}

	private UpdateDataBase() {
	}

}
