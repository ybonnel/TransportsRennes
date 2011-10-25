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
package fr.ybo.transportsrennes.application;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;

import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.transportsrennes.services.UpdateTimeService;
import fr.ybo.transportsrennes.database.TransportsRennesDatabase;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.AlarmReceiver;
import fr.ybo.transportsrennes.util.CalculItineraires;
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.GeocodeUtil;

/**
 * Classe de l'application permettant de stocker les attributs globaux à
 * l'application.
 */
public class TransportsRennesApplication extends Application {

	private static GeocodeUtil geocodeUtil;

	public static GeocodeUtil getGeocodeUtil() {
		return geocodeUtil;
	}

	private static TransportsRennesDatabase databaseHelper;

	public static TransportsRennesDatabase getDataBaseHelper() {
		return databaseHelper;
	}

	@Override
	public void onCreate() {
		if (CapptainAgentUtils.isInDedicatedCapptainProcess(this))
			return;
		super.onCreate();

		databaseHelper = new TransportsRennesDatabase(this);
		geocodeUtil = new GeocodeUtil(this);
		startService(new Intent(UpdateTimeService.ACTION_UPDATE));
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("fr.ybo.transportsrennes", ".services.UpdateTimeService"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

		// Récupération des alertes
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					for (Alert alert : Keolis.getInstance().getAlerts()) {
						lignesWithAlerts.addAll(alert.lines);
					}
				} catch (ErreurReseau ignore) {
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
		}.execute((Void)null);

		setRecurringAlarm(this);
	}

	private static Set<String> lignesWithAlerts = new HashSet<String>();

	public static boolean hasAlert(String ligneNomCourt) {
		return lignesWithAlerts.contains(ligneNomCourt);
	}

	private static LatLngBounds bounds;

	public static LatLngBounds getBounds() {
		return bounds;
	}

	private static final long INTERVAL_ALARM = AlarmManager.INTERVAL_HALF_DAY;

	private void setRecurringAlarm(Context context) {
		Intent alarm = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringCheck = PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, INTERVAL_ALARM, recurringCheck);
	}

}
