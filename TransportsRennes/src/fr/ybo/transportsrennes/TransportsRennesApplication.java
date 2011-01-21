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

package fr.ybo.transportsrennes;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.util.Constantes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
public class TransportsRennesApplication extends Application {

	private static DataBaseHelper databaseHelper;

	public static DataBaseHelper getDataBaseHelper() {
		return databaseHelper;
	}

	private static String dateDerniereVerifUpdate = null;

	private static String dateCourante = null;

	private static SharedPreferences sharedPreferences = null;

	private static DernierMiseAJour dernierMiseAJour = new DernierMiseAJour();

	public static boolean verifUpdateNecessaire() {
		if (dateDerniereVerifUpdate == null) {
			dateDerniereVerifUpdate = sharedPreferences.getString("dateDerniereVerifUpdate", null);
		}
		return (dateDerniereVerifUpdate == null) || !dateCourante.equals(dateDerniereVerifUpdate) || databaseHelper.selectSingle(dernierMiseAJour) == null;
	}

	public static void verifUpdateDone() {
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString("dateDerniereVerifUpdate", dateCourante);
		dateDerniereVerifUpdate = dateCourante;
		edit.commit();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dateCourante = new SimpleDateFormat("yyyyMMdd").format(new Date());
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		databaseHelper = new DataBaseHelper(this, ConstantesKeolis.LIST_CLASSES_DATABASE);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		TransportsWidget.verifKiller(getApplicationContext(), appWidgetManager);
		GoogleAnalyticsTracker traker = GoogleAnalyticsTracker.getInstance();
		traker.start(Constantes.UA_ACCOUNT, this);
		/*traker.setCustomVar(1, "androidVersion", android.os.Build.FINGERPRINT, 1);
		traker.setCustomVar(2, "androidModel", android.os.Build.MODEL, 1);
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			traker.setCustomVar(3, "appVersion", info.versionName, 1);
		} catch (PackageManager.NameNotFoundException ignore) {
		}*/
		myTraker = new MyTraker(traker);
		handler = new Handler();
	}

	private static Handler handler;

	private static MyTraker myTraker;


	public static class MyTraker {

		public MyTraker(GoogleAnalyticsTracker traker) {
			this.traker = traker;
		}

		private GoogleAnalyticsTracker traker;

		 public void trackPageView(final String url) {
			 handler.post(new Runnable() {
				 public void run() {
					 traker.trackPageView(url);
					 traker.dispatch();
				 }
			 });
		 }
	}

	public static MyTraker getTraker() {
		return myTraker;
	}
}
