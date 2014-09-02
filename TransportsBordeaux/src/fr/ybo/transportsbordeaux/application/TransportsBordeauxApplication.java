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
package fr.ybo.transportsbordeaux.application;

import java.math.BigDecimal;
import java.util.Arrays;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.TransportsBordeaux;
import fr.ybo.transportsbordeaux.activity.bus.DetailArret;
import fr.ybo.transportsbordeaux.activity.bus.TabFavoris;
import fr.ybo.transportsbordeaux.activity.preferences.PreferencesBordeaux;
import fr.ybo.transportsbordeaux.activity.velos.ListStationsFavoris;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportsbordeaux.database.modele.Alert;
import fr.ybo.transportsbordeaux.services.UpdateTimeService;
import fr.ybo.transportsbordeaux.util.AlarmReceiver;
import fr.ybo.transportsbordeaux.util.CalculItineraires;
import fr.ybo.transportsbordeaux.util.ContextWithDatabasePath;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.DonnesSpecifiques;
import fr.ybo.transportscommun.activity.AccueilActivity;
import fr.ybo.transportscommun.activity.commun.ActivityHelper;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Refreshable;
import fr.ybo.transportscommun.donnees.manager.gtfs.CoupleResourceFichier;

/**
 * Classe de l'application permettant de stocker les attributs globaux à
 * l'application.
 */
public class TransportsBordeauxApplication extends AbstractTransportsApplication {

	private static boolean baseNeuve = false;

	public static boolean isBaseNeuve() {
		return baseNeuve;
	}

	public static void setBaseNeuve(boolean baseNeuve) {
		TransportsBordeauxApplication.baseNeuve = baseNeuve;
	}

	public static void constructDatabase(Context pContext) {
		Context context = pContext;
		boolean databaseOnSDCard = PreferenceManager.getDefaultSharedPreferences(pContext).getBoolean(
				"TransportsBordeaux_sdCard", false);

		if (databaseOnSDCard) {
			ContextWithDatabasePath contextWithDatabasePath = new ContextWithDatabasePath(pContext);
			try {
				contextWithDatabasePath.getDatabasePath(TransportsBordeauxDatabase.DATABASE_NAME);
				context = contextWithDatabasePath;
			} catch (Exception exception) {
				Toast.makeText(pContext, pContext.getString(R.string.erreurDBOnSdCard), Toast.LENGTH_LONG).show();
				try {
					ActivityManager am = (ActivityManager) pContext.getSystemService(ACTIVITY_SERVICE);
					am.restartPackage(pContext.getPackageName());
				} catch (Exception ignore) {

				}
				return;
			}
		}
		databaseHelper = new TransportsBordeauxDatabase(context);
	}

	@Override
	public void constructDatabase() {
		constructDatabase(this);
	}

	@Override
	protected void initDonneesSpecifiques() {
		donnesSpecifiques = new DonnesSpecifiques() {

			@Override
			public int getCompactLogo() {
				return R.drawable.compact_icon;
			}

			@Override
			public String getApplicationName() {
				return "TransportsBordeaux";
			}

			@Override
			public Class<?> getDrawableClass() {
				return R.drawable.class;
			}

			@Override
			public int getIconeLigne() {
				return R.drawable.icone_bus;
			}

			@Override
			public Class<? extends BaseListActivity> getDetailArretClass() {
				return DetailArret.class;
			}
		};
	}

	@Override
	public void postCreate() {
		RESOURCES_PRINCIPALE = Arrays.asList(new CoupleResourceFichier(R.raw.arrets, "arrets.txt"),
				new CoupleResourceFichier(R.raw.arrets_routes, "arrets_routes.txt"), new CoupleResourceFichier(
						R.raw.calendriers, "calendriers.txt"), new CoupleResourceFichier(R.raw.calendriers_exceptions,
						"calendriers_exceptions.txt"), new CoupleResourceFichier(R.raw.directions, "directions.txt"),
				new CoupleResourceFichier(R.raw.lignes, "lignes.txt"), new CoupleResourceFichier(R.raw.trajets,
						"trajets.txt"));

		startService(new Intent(UpdateTimeService.ACTION_UPDATE));
        try {
            PackageManager pm = getPackageManager();
            if (pm != null) {
                pm.setComponentEnabledSetting(new ComponentName(this, UpdateTimeService.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
        } catch (Exception ignore) {}

        // Récupération des alertes
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					GraphMetadata metadata = CalculItineraires.getInstance().getMetadata();
					if (metadata != null) {
						setBounds(new LatLngBounds(new LatLng(new BigDecimal(metadata.getMinLatitude()),
								new BigDecimal(metadata.getMinLongitude())), new LatLng(new BigDecimal(
								metadata.getMaxLatitude()), new BigDecimal(metadata.getMaxLongitude()))));
					}
				} catch (OpenTripPlannerException ignore) {
				}
				return null;
			}
		}.execute((Void) null);

		setRecurringAlarm(this);
	}

	private static final long INTERVAL_ALARM = AlarmManager.INTERVAL_HALF_DAY;

	private void setRecurringAlarm(Context context) {
		Intent alarm = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringCheck = PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, INTERVAL_ALARM, recurringCheck);
	}

	@Override
	public Class<? extends AccueilActivity> getAccueilActivity() {
		return TransportsBordeaux.class;
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
				activity.startActivity(new Intent(activity, PreferencesBordeaux.class));
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

}
