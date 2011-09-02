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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsbordeaux;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportsbordeaux.modele.Alert;
import fr.ybo.transportsbordeaux.modele.Arret;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.ArretRoute;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.modele.Direction;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.modele.VeloFavori;
import fr.ybo.transportsbordeaux.util.AlarmReceiver;
import fr.ybo.transportsbordeaux.util.CalculItineraires;
import fr.ybo.transportsbordeaux.util.GeocodeUtil;
import fr.ybo.transportsbordeaux.util.Version;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
@ReportsCrashes(formKey = "dE5mNl9RV3NOc25XdnI1RWpNQnZGYlE6MQ", mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.erreurNonPrevue)
public class TransportsBordeauxApplication extends Application {

	private static final String UA_ACCOUNT = "UA-20831542-3";

	private static TransportsBordeauxDatabase databaseHelper;

	public static TransportsBordeauxDatabase getDataBaseHelper() {
		return databaseHelper;
	}

	private static GeocodeUtil geocodeUtil;

	public static GeocodeUtil getGeocodeUtil() {
		return geocodeUtil;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
		databaseHelper = new TransportsBordeauxDatabase(this, Arrays.asList(Arret.class, ArretFavori.class, ArretRoute.class,
				DernierMiseAJour.class, Direction.class, Ligne.class, VeloFavori.class));

		GoogleAnalyticsTracker traker = GoogleAnalyticsTracker.getInstance();
		traker.start(UA_ACCOUNT, this);
		geocodeUtil = new GeocodeUtil(this);
		handler = new Handler();
		myTraker = new TransportsBordeauxApplication.MyTraker(traker);
		myTraker.trackPageView("/TransportsBordeauxApplication/Version/" + Version.getVersionCourante(this));

		// Récupération des alertes
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					for (Alert alert : Alert.getAlertes()) {
						lignesWithAlerts.add(alert.ligne);
					}
				} catch (Exception ignored) {

				}
				try {
					GraphMetadata metadata = CalculItineraires.getInstance().getMetadata();
					if (metadata != null) {
						bounds = new LatLngBounds(new LatLng(new BigDecimal(metadata.getMinLatitude()), new BigDecimal(
								metadata.getMinLongitude())), new LatLng(new BigDecimal(metadata.getMaxLatitude()),
								new BigDecimal(metadata.getMaxLongitude())));
					}
				} catch (OpenTripPlannerException ignore) {
				}
				return null;
			}
		}.execute();

		setRecurringAlarm(this);
	}

	private static Handler handler;

	private static MyTraker myTraker;

	public static class MyTraker {

		public MyTraker(GoogleAnalyticsTracker traker) {
			this.traker = traker;
		}

		private final GoogleAnalyticsTracker traker;

		public void trackPageView(final String url) {
			handler.post(new Runnable() {
				public void run() {
					try {
						traker.trackPageView(url);
						traker.dispatch();
					} catch (Exception ignore) {

					}
				}
			});
		}
	}

	public static MyTraker getTraker() {
		return myTraker;
	}

	private static LatLngBounds bounds;

	public static LatLngBounds getBounds() {
		return bounds;
	}

	private static Set<String> lignesWithAlerts = new HashSet<String>();

	public static boolean hasAlert(String ligneNomLong) {
		return lignesWithAlerts.contains(ligneNomLong);
	}

	private static final long INTERVAL_ALARM = AlarmManager.INTERVAL_HALF_DAY;

	private void setRecurringAlarm(Context context) {
		Intent alarm = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringCheck = PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, INTERVAL_ALARM, recurringCheck);
	}

}
