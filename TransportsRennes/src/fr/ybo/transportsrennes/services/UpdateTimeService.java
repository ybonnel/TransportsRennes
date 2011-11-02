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

package fr.ybo.transportsrennes.services;

import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.widgets.*;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.modele.Arret;
import fr.ybo.transportsrennes.database.modele.Ligne;
import fr.ybo.transportsrennes.database.modele.Notification;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.Calendar;

public class UpdateTimeService extends Service {

    private static LogYbo LOG_YBO = new LogYbo(UpdateTimeService.class);

    /**
     * Used by the AppWidgetProvider to notify the Service that the views need
     * to be updated and redrawn.
     */
    public static final String ACTION_UPDATE = "fr.ybo.transportsrennes.action.UPDATE";

    private final static IntentFilter sIntentFilter;

    static {
        sIntentFilter = new IntentFilter();
        sIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        sIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        sIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        sIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LOG_YBO.debug("onCreate");
        registerReceiver(mTimeChangedReceiver, sIntentFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeChangedReceiver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent != null && ACTION_UPDATE.equals(intent.getAction())) {
            update();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification notifSelect = new Notification();

    /**
     * Updates and redraws the Widget.
     */
    private void update() {
        LOG_YBO.debug("update");
        for (int widgetId : TransportsWidget11Configure.getWidgetIds(getApplicationContext())) {
            TransportsWidget11.updateAppWidget(getApplicationContext(),
                    AppWidgetManager.getInstance(getApplicationContext()), widgetId);
        }
        for (int widgetId : TransportsWidget21Configure.getWidgetIds(getApplicationContext())) {
            TransportsWidget21.updateAppWidget(getApplicationContext(),
                    AppWidgetManager.getInstance(getApplicationContext()), widgetId);
        }
        for (int widgetId : TransportsWidgetConfigure.getWidgetIds(getApplicationContext())) {
            TransportsWidget.updateAppWidget(getApplicationContext(),
                    AppWidgetManager.getInstance(getApplicationContext()), widgetId);
        }
        // GestionNotif.
        Calendar calendar = Calendar.getInstance();
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        notifSelect.setHeure(now);
        for (Notification notification : TransportsRennesApplication.getDataBaseHelper().select(notifSelect)) {
            createNotification(notification);
            TransportsRennesApplication.getDataBaseHelper().delete(notification);
        }

    }


    private void createNotification(Notification notification) {
        Ligne ligne = Ligne.getLigne(notification.getLigneId());
        Arret arret = Arret.getArret(notification.getArretId());
        int icon = IconeLigne.getIconeResource(ligne.nomCourt);
        String texte = getResources().getString(R.string.notifText, ligne.nomCourt, arret.nom, notification.getTempsAttente());
        android.app.Notification notif = new android.app.Notification(icon, texte, System.currentTimeMillis());
        notif.setLatestEventInfo(this, texte, texte, null);
        notif.defaults |= android.app.Notification.DEFAULT_ALL;
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notification.getHeure(), notif);
    }

    /**
     * Automatically registered when the Service is created, and unregistered
     * when the Service is destroyed.
     */
    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {

        private boolean screenOn = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                screenOn = true;
            }
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                screenOn = false;
            }
            if (screenOn) {
                update();
            }
        }
    };

}
