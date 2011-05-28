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

package fr.ybo.transportsbordeaux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportsbordeaux.modele.Alert;
import fr.ybo.transportsbordeaux.modele.Arret;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.ArretRoute;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.modele.Direction;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.modele.VeloFavori;
import fr.ybo.transportsbordeaux.util.Version;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
@ReportsCrashes(formKey = "dE5mNl9RV3NOc25XdnI1RWpNQnZGYlE6MQ", mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.erreurNonPrevue)
public class TransportsBordeauxApplication extends Application {

	private static TransportsBordeauxDatabase databaseHelper;

	public static TransportsBordeauxDatabase getDataBaseHelper() {
		return databaseHelper;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
		databaseHelper = new TransportsBordeauxDatabase(this, Arrays.asList(Arret.class, ArretFavori.class, ArretRoute.class,
				DernierMiseAJour.class, Direction.class, Ligne.class, VeloFavori.class));

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
				return null;
			}
		}.execute();

		checkVersion.execute();
	}

	private static Set<String> lignesWithAlerts = new HashSet<String>();

	public static boolean hasAlert(String ligneNomLong) {
		return lignesWithAlerts.contains(ligneNomLong);
	}
	


	private AsyncTask<Void, Void, String> checkVersion = new AsyncTask<Void, Void, String>() {

		@Override
		protected String doInBackground(Void... params) {
			return Version.getVersionMarket();
		}

		protected void onPostExecute(String result) {
			if (result != null && !result.equals(Version.getVersionCourante(TransportsBordeauxApplication.this))) {
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

		Uri uri = Uri.parse("market://details?id=fr.ybo.transportsbordeaux");
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
