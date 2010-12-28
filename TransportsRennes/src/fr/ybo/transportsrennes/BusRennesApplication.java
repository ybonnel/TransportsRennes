package fr.ybo.transportsrennes;

import android.app.Application;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
public class BusRennesApplication extends Application {

	private static DataBaseHelper databaseHelper;

	public static DataBaseHelper getDataBaseHelper() {
		return databaseHelper;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		databaseHelper = new DataBaseHelper(getApplicationContext(), ConstantesKeolis.LIST_CLASSES_DATABASE);
	}

}
