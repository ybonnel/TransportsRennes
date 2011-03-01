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

package fr.ybo.transportsrennes;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.Constantes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
public class TransportsRennesApplication extends Application {

	@SuppressWarnings({"StaticNonFinalField", "StaticNonFinalField"})
	private static DataBaseHelper databaseHelper;

	public static DataBaseHelper getDataBaseHelper() {
		return databaseHelper;
	}

	@SuppressWarnings({"StaticNonFinalField"})
	private static String dateDerniereVerifUpdate;

	@SuppressWarnings({"StaticNonFinalField"})
	private static String dateCourante;

	@SuppressWarnings({"StaticNonFinalField"})
	private static SharedPreferences sharedPreferences;

	private static final DernierMiseAJour MISE_A_JOUR = new DernierMiseAJour();

	public static synchronized boolean isUpdateNecessaire() {
		if (dateDerniereVerifUpdate == null) {
			dateDerniereVerifUpdate = sharedPreferences.getString("dateDerniereVerifUpdate150", null);
		}
		return dateDerniereVerifUpdate == null || !dateCourante.equals(dateDerniereVerifUpdate) || databaseHelper.selectSingle(MISE_A_JOUR) == null;
	}

	public static void verifUpdateDone() {
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString("dateDerniereVerifUpdate150", dateCourante);
		dateDerniereVerifUpdate = dateCourante;
		edit.commit();
	}

	@SuppressWarnings({"AssignmentToStaticFieldFromInstanceMethod"})
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
		handler = new Handler();
		myTraker = new TransportsRennesApplication.MyTraker(traker);
		myTraker.trackPageView("/TransportsRennesApplication/Model/" + Build.MODEL);
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			myTraker.trackPageView("/TransportsRennesApplication/Version/" + info.versionName);
		} catch (PackageManager.NameNotFoundException ignore) {
		}

		// Récupération des alertes
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					for (Alert alert : Keolis.getInstance().getAlerts()) {
						lignesWithAlerts.addAll(alert.lines);
					}
				} catch (Exception ignored) {

				}
				return null;
			}
		}.execute();
	}

	@SuppressWarnings({"StaticNonFinalField"})
	private static Handler handler;

	@SuppressWarnings({"StaticNonFinalField"})
	private static TransportsRennesApplication.MyTraker myTraker;


	public static class MyTraker {

		public MyTraker(GoogleAnalyticsTracker traker) {
			this.traker = traker;
		}

		private final GoogleAnalyticsTracker traker;

		public void trackPageView(final String url) {
			handler.post(new Runnable() {
				public void run() {
					traker.trackPageView(url);
					traker.dispatch();
				}
			});
		}
	}

	public static TransportsRennesApplication.MyTraker getTraker() {
		return myTraker;
	}

	private static Set<String> lignesWithAlerts = new HashSet<String>();

	public static boolean hasAlert(String ligneNomCourt) {
		return lignesWithAlerts.contains(ligneNomCourt);
	}
}
