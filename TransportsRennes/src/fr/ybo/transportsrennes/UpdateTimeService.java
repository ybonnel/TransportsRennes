package fr.ybo.transportsrennes;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import fr.ybo.transportsrennes.util.LogYbo;

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
