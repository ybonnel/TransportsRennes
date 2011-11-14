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

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;
import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportsbordeaux.database.modele.Alert;
import fr.ybo.transportsbordeaux.services.UpdateTimeService;
import fr.ybo.transportsbordeaux.util.AlarmReceiver;
import fr.ybo.transportsbordeaux.util.CalculItineraires;
import fr.ybo.transportsbordeaux.util.ContextWithDatabasePath;
import fr.ybo.transportsbordeaux.util.GeocodeUtil;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
public class TransportsBordeauxApplication extends Application {

    private static TransportsBordeauxDatabase databaseHelper;

    private static boolean baseNeuve = false;

    public static boolean isBaseNeuve() {
        return baseNeuve;
    }

    public static void setBaseNeuve(boolean baseNeuve) {
        TransportsBordeauxApplication.baseNeuve = baseNeuve;
    }

    public static TransportsBordeauxDatabase getDataBaseHelper() {
        return databaseHelper;
    }

    private static GeocodeUtil geocodeUtil;

    public static GeocodeUtil getGeocodeUtil() {
        return geocodeUtil;
    }

    public static void constuctDatabase(Context pContext) {
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

    private boolean isInPrincipalProcess() {
        PackageInfo packageinfo;
        try {
            packageinfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SERVICES);
        } catch (android.content.pm.PackageManager.NameNotFoundException ex) {
            return false;
        }
        String processName = packageinfo.applicationInfo.processName;

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == android.os.Process.myPid()) {
                return runningAppProcessInfo.processName.equals(processName);
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        if (CapptainAgentUtils.isInDedicatedCapptainProcess(this))
            return;
        super.onCreate();

        constuctDatabase(this);

        if (!isInPrincipalProcess()) {
            return;
        }

        startService(new Intent(UpdateTimeService.ACTION_UPDATE));
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("fr.ybo.transportsbordeaux", ".services.UpdateTimeService"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        geocodeUtil = new GeocodeUtil(this);

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
        }.execute((Void) null);

        setRecurringAlarm(this);
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

    private static final boolean activeUpdates = true;

    private void setRecurringAlarm(Context context) {
        if (activeUpdates) {
            Intent alarm = new Intent(context, AlarmReceiver.class);
            PendingIntent recurringCheck = PendingIntent.getBroadcast(context, 0, alarm,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, INTERVAL_ALARM, recurringCheck);
        }
    }

}
