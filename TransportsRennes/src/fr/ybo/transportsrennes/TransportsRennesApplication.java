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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.transportsrennes.database.TransportsRennesDatabase;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.CalculItineraires;
import fr.ybo.transportsrennes.util.Constantes;
import fr.ybo.transportsrennes.util.Version;

/**
 * Classe de l'application permettant de stocker les attributs globaux à
 * l'application.
 */
public class TransportsRennesApplication extends Application {

	private static TransportsRennesDatabase databaseHelper;

	public static TransportsRennesDatabase getDataBaseHelper() {
		return databaseHelper;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		databaseHelper = new TransportsRennesDatabase(this);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		TransportsWidget.verifKiller(getApplicationContext(), appWidgetManager);
		TransportsWidget11.verifKiller(getApplicationContext(), appWidgetManager);
		TransportsWidget21.verifKiller(getApplicationContext(), appWidgetManager);
		GoogleAnalyticsTracker traker = GoogleAnalyticsTracker.getInstance();
		traker.start(Constantes.UA_ACCOUNT, this);
		handler = new Handler();
		myTraker = new TransportsRennesApplication.MyTraker(traker);
		myTraker.trackPageView("/TransportsRennesApplication/Model/" + Build.MODEL);
		myTraker.trackPageView("/TransportsRennesApplication/Version/" + Version.getVersionCourante(this));

		// Récupération des alertes
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					for (Alert alert : Keolis.getInstance().getAlerts()) {
						lignesWithAlerts.addAll(alert.lines);
					}
					GraphMetadata metadata = CalculItineraires.getInstance().getMetadata();
					bounds = new LatLngBounds(new LatLng(new BigDecimal(metadata.getMinLatitude()), new BigDecimal(
							metadata.getMinLongitude())), new LatLng(new BigDecimal(metadata.getMinLatitude()),
							new BigDecimal(metadata.getMinLongitude())));
				} catch (Exception ignored) {

				}
				return null;
			}
		}.execute();

		checkVersion.execute();
	}

	private static Handler handler;

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

	private static LatLngBounds bounds;

	public static LatLngBounds getBounds() {
		return bounds;
	}

	private AsyncTask<Void, Void, String> checkVersion = new AsyncTask<Void, Void, String>() {

		@Override
		protected String doInBackground(Void... params) {
			return Version.getVersionMarket();
		}

		protected void onPostExecute(String result) {
			if (result != null && !result.equals(Version.getVersionCourante(TransportsRennesApplication.this))) {
				createNotification(result);
			}
		};
	};

	private final int NOTIFICATION_VERSION_ID = 1;

	private void createNotification(String nouvelleVersion) {
		int icon = R.drawable.icon;
		CharSequence tickerText = getString(R.string.nouvelleVersion);
		long when = System.currentTimeMillis();
		Context context = getApplicationContext();
		CharSequence contentTitle = getString(R.string.nouvelleVersion);
		CharSequence contentText = getString(R.string.versionDisponible, nouvelleVersion);

		Uri uri = Uri.parse("market://details?id=fr.ybo.transportsrennes");
		Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		// the next two lines initialize the Notification, using the
		// configurations above
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_VERSION_ID, notification);
	}

}
