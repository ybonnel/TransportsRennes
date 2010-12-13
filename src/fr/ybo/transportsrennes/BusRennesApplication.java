package fr.ybo.transportsrennes;

import android.app.Application;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;

public class BusRennesApplication extends Application {

	private static DataBaseHelper databaseHelper;

	public static DataBaseHelper getDataBaseHelper() {
		return databaseHelper;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			databaseHelper = new DataBaseHelper(getApplicationContext(), ConstantesKeolis.LIST_CLASSES_DATABASE);
		} catch (final DataBaseException e) {
			e.printStackTrace();
		}
	}

}
