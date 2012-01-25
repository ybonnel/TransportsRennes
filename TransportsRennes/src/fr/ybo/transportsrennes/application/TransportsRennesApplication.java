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
package fr.ybo.transportsrennes.application;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.activity.AccueilActivity;
import fr.ybo.transportscommun.activity.commun.ActivityHelper;
import fr.ybo.transportscommun.activity.commun.Refreshable;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.TransportsRennes;
import fr.ybo.transportsrennes.activity.bus.TabFavoris;
import fr.ybo.transportsrennes.activity.preferences.PreferencesRennes;
import fr.ybo.transportsrennes.activity.velos.ListStationsFavoris;
import fr.ybo.transportsrennes.database.TransportsRennesDatabase;
import fr.ybo.transportsrennes.database.modele.AlertBdd;
import fr.ybo.transportsrennes.database.modele.Bounds;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.services.UpdateTimeService;
import fr.ybo.transportsrennes.util.AlarmReceiver;
import fr.ybo.transportsrennes.util.CalculItineraires;
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.Theme;

/**
 * Classe de l'application permettant de stocker les attributs globaux à
 * l'application.
 */
public class TransportsRennesApplication extends AbstractTransportsApplication {

	private static TransportsRennesDatabase databaseHelper;

	public static TransportsRennesDatabase getDataBaseHelper() {
		return databaseHelper;
	}

	public static Theme getTheme(Context context) {
		return Theme.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(
				"TransportsRennes_choixTheme", Theme.BLANC.name()));
	}

	public static int getTextColor(Context context) {
		return getTheme(context).getTextColor();
	}

	public void majTheme(Context context) {
		context.setTheme(getTheme(context).getTheme());
	}

	public String getApplicationName() {
		return "TransportsRennes";
	}

	public void constructDatabase() {
		databaseHelper = new TransportsRennesDatabase(this);
	}

	public void postCreate() {

		startService(new Intent(UpdateTimeService.ACTION_UPDATE));
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("fr.ybo.transportsrennes", ".services.UpdateTimeService"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

		final String dateCourante = new SimpleDateFormat("ddMMyyyy").format(new Date());
		Bounds boundsBdd = getDataBaseHelper().selectSingle(new Bounds());
		if (boundsBdd != null) {
			if (!dateCourante.equals(boundsBdd.getDate())) {
				getDataBaseHelper().delete(boundsBdd);
			}
		}

		AlertBdd alertBdd = getDataBaseHelper().selectSingle(new AlertBdd());
		if (alertBdd != null) {
			if (!dateCourante.equals(alertBdd.getDate())) {
				getDataBaseHelper().delete(alertBdd);
			}
		}

		// Récupération des alertes
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					AlertBdd alertBdd = getDataBaseHelper().selectSingle(new AlertBdd());
					if (alertBdd == null) {
						alertBdd = new AlertBdd();
						alertBdd.setDate(dateCourante);
						Set<String> lignes = new HashSet<String>();
						for (Alert alert : Keolis.getInstance().getAlerts()) {
							lignes.addAll(alert.lines);
						}
						StringBuilder stringBuilder = new StringBuilder();
						for (String ligne : lignes) {
							stringBuilder.append(ligne);
							stringBuilder.append(',');
						}
						alertBdd.setLignes(stringBuilder.toString());
						getDataBaseHelper().insert(alertBdd);
					}

					Collections.addAll(lignesWithAlerts, alertBdd.getLignes().split(","));
				} catch (ErreurReseau ignore) {
				}
				try {
					Bounds boundsBdd = getDataBaseHelper().selectSingle(new Bounds());
					if (boundsBdd == null) {
						GraphMetadata metadata = CalculItineraires.getInstance().getMetadata();
						if (metadata != null) {
							boundsBdd = new Bounds();
							boundsBdd.setDate(dateCourante);
							boundsBdd.setMinLatitude(metadata.getMinLatitude());
							boundsBdd.setMaxLatitude(metadata.getMaxLatitude());
							boundsBdd.setMinLongitude(metadata.getMinLongitude());
							boundsBdd.setMaxLongitude(metadata.getMaxLongitude());
							getDataBaseHelper().insert(boundsBdd);
						}
					}
					if (boundsBdd != null) {
						bounds = new LatLngBounds(new LatLng(new BigDecimal(boundsBdd.getMinLatitude()),
								new BigDecimal(boundsBdd.getMinLongitude())), new LatLng(new BigDecimal(
								boundsBdd.getMaxLatitude()), new BigDecimal(boundsBdd.getMaxLongitude())));
					}
				} catch (OpenTripPlannerException ignore) {
				}
				return null;
			}
		}.execute((Void) null);

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

	public Class<? extends AccueilActivity> getAccueilActivity() {
		return TransportsRennes.class;
	}

	public boolean isThemeNoir() {
		return getTheme(this) == Theme.NOIR;
	}

	public boolean onOptionsItemSelected(MenuItem item, Activity activity, ActivityHelper helper) {

		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				helper.goHome();
				return true;
			case R.id.menu_bus_favoris:
				activity.startActivity(new Intent(activity, TabFavoris.class));
				return true;
			case R.id.menu_velo_favoris:
				activity.startActivity(new Intent(activity, ListStationsFavoris.class));
				return true;
			case R.id.menu_prefs:
				activity.startActivity(new Intent(activity, PreferencesRennes.class));
				return true;
			case R.id.menu_refresh:
				if (activity instanceof Refreshable) {
					((Refreshable) activity).refresh();
				}
				return true;
			default:
				return false;
		}

	}

	public int getTextColor() {
		return getTextColor(this);
	}

	public int getActionBarBackground() {
		return getTheme(this).getActionBarBackground();
	}

}
