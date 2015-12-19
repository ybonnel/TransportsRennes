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

package fr.ybo.transportsbordeaux.services;

import java.util.Calendar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.bus.DetailArret;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget11;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget21;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportscommun.donnees.manager.gtfs.UpdateDataBase;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportscommun.util.LogYbo;

public class UpdateTimeService extends Service {

	private static final LogYbo LOG_YBO = new LogYbo(UpdateTimeService.class);

	/**
	 * Used by the AppWidgetProvider to notify the Service that the views need
	 * to be updated and redrawn.
	 */
	public static final String ACTION_UPDATE = "fr.ybo.transportsbordeaux.action.UPDATE";

	private static final IntentFilter sIntentFilter;

	static {
		sIntentFilter = new IntentFilter();
		sIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		sIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		sIntentFilter.addAction(Intent.ACTION_TIME_TICK);
		sIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		sIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LOG_YBO.debug("onCreate");
        try {
            final PackageManager pm = getPackageManager();
            if (pm != null) {
                pm.setComponentEnabledSetting(new ComponentName(this, UpdateTimeService.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
        } catch (final Exception ignore) {}
        registerReceiver(mTimeChangedReceiver, sIntentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mTimeChangedReceiver);
	}

	@Override
	public void onStart(final Intent intent, final int startId) {
		super.onStart(intent, startId);
		if (intent != null && ACTION_UPDATE.equals(intent.getAction())) {
			update();
		}
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	/**
	 * Updates and redraws the Widget.
	 */
	private void update() {
		try {
			LOG_YBO.debug("update");
			for (final int widgetId : TransportsWidget11Configure.getWidgetIds(getApplicationContext())) {
				TransportsWidget11.updateAppWidget(getApplicationContext(),
						AppWidgetManager.getInstance(getApplicationContext()), widgetId);
			}
			for (final int widgetId : TransportsWidget21Configure.getWidgetIds(getApplicationContext())) {
				TransportsWidget21.updateAppWidget(getApplicationContext(),
						AppWidgetManager.getInstance(getApplicationContext()), widgetId);
			}
		} catch (final Exception ignore) {
		}
	}

	private final Notification notifSelect = new Notification();

	private void updateNotifs() {
		try {
			// GestionNotif.
			final Calendar calendar = Calendar.getInstance();
			final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			notifSelect.setHeure(now);
			LOG_YBO.debug("Recherche des notif pour " + now);
			for (final Notification notification : TransportsBordeauxApplication.getDataBaseHelper().select(notifSelect)) {
				createNotification(notification);
			}
			notifSelect.setHeure(now - 1);
			for (final Notification notification : TransportsBordeauxApplication.getDataBaseHelper().select(notifSelect)) {
				createNotification(notification);
			}
			notifSelect.setHeure(now - 2);
			for (final Notification notification : TransportsBordeauxApplication.getDataBaseHelper().select(notifSelect)) {
				createNotification(notification);
			}
		} catch (final Exception ignore) {
		}
	}

	private void createNotification(final Notification notification) {
		LOG_YBO.debug("Création d'une notif pour la ligne " + notification.getLigneId());
		final Ligne ligne = Ligne.getLigne(notification.getLigneId());
		final Arret arret = Arret.getArret(notification.getArretId());
		final int icon = IconeLigne.getIconeResource(ligne.nomCourt);
		final String texte = getResources().getString(R.string.notifText, ligne.nomCourt, arret.nom,
				notification.getTempsAttente());
		final String shortText = getResources().getString(R.string.notifShortText, ligne.nomCourt, arret.nom);
		final String descriptionText = getResources()
				.getString(R.string.notifDescriptionText, notification.getTempsAttente());
		final Intent notificationIntent = new Intent(this, DetailArret.class).putExtra("ligne", ligne).putExtra("idArret", notification.getArretId()).putExtra("nomArret", arret.nom).putExtra("direction", notification.getDirection());

		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		final android.app.Notification notif = new android.app.Notification(icon, texte, System.currentTimeMillis());
		notif.setLatestEventInfo(this, shortText, descriptionText, contentIntent);
		notif.defaults |= android.app.Notification.DEFAULT_ALL;
		notif.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
		final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notification.getHeure(), notif);
		TransportsBordeauxApplication.getDataBaseHelper().delete(notification);
	}

	/**
	 * Automatically registered when the Service is created, and unregistered
	 * when the Service is destroyed.
	 */
	private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {

		private boolean screenOn = true;

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();

			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				screenOn = true;
			}
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				screenOn = false;
			}
			if (UpdateDataBase.isMajDatabasePasEncours()) {
				if (screenOn) {
					update();
				}
				updateNotifs();
			}
		}
	};

}
