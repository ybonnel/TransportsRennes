package fr.ybo.transportsrennes.keolis.gtfs;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import fr.ybo.transportsrennes.BusRennesApplication;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.files.ErreurGestionFiles;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.util.LogYbo;

public final class UpdateDataBase {

    private static final LogYbo LOG_YBO = new LogYbo(UpdateDataBase.class);

    public static void updateIfNecessaryDatabase(final DataBaseHelper dataBaseHelper, final Context context,
                                                 final ProgressDialog progressDialog, final Activity currentActivity) throws DataBaseException, ErreurMoteurCsv,
            ErreurGestionFiles {
        LOG_YBO.debug("Mise à jour des données Keolis...");
        DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
        if (dernierMiseAJour != null && !GestionZipKeolis.fichierRoutesPresents(context, dataBaseHelper.select(new Route()))) {
            LOG_YBO.debug("Une mise à jour a déjà été effectuée, mais les fichiers de routes sont absents...");
            dernierMiseAJour = null;
        }
        final Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate();
        if (dateDernierFichierKeolis != null
                && (dernierMiseAJour == null || dernierMiseAJour.getDerniereMiseAJour() == null || dateDernierFichierKeolis
                .after(dernierMiseAJour.getDerniereMiseAJour()))) {
            LOG_YBO.debug("Mise à jour disponible, lancement de la mise à jour");
            LOG_YBO.debug("Suppression de toutes les tables");
            for (final Class<?> clazz : ConstantesKeolis.LIST_CLASSES_DATABASE) {
                dataBaseHelper.deleteAll(clazz);
            }
            LOG_YBO.debug("Mise à jour des donnees");
            GestionZipKeolis.getAndParseZipKeolis(dateDernierFichierKeolis, context, new MoteurCsv(
                    ConstantesKeolis.LIST_CLASSES_GTFS), dataBaseHelper, progressDialog, currentActivity);
            dernierMiseAJour = new DernierMiseAJour();
            dernierMiseAJour.setDerniereMiseAJour(dateDernierFichierKeolis);
            dataBaseHelper.insert(dernierMiseAJour);
        }
        dataBaseHelper.endTransaction();
        dataBaseHelper.close();
    }

    public static void chargeDetailRoute(Route route, Context context) throws ErreurMoteurCsv, IOException, DataBaseException {
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
