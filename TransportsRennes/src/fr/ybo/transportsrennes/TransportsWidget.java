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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.LogYbo;
import fr.ybo.transportsrennes.util.WidgetUpdateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TransportsWidget extends AppWidgetProvider {

	private final static LogYbo LOG_YBO = new LogYbo(TransportsWidget.class);

	private final static Map<Integer, Timer> mapTimersByWidgetId = new HashMap<Integer, Timer>();

	public static void verifKiller(Context context, AppWidgetManager appWidgetManager) {
		if (mapTimersByWidgetId.size() == 0) {
			for (int widgetId : TransportsWidgetConfigure.getWidgetIds(context)) {
				LOG_YBO.debug("Le widget " + widgetId + " a du être killer, on le relance");
				updateAppWidget(context, appWidgetManager, widgetId);
			}
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		LOG_YBO.debug("onUpdate");
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		synchronized (mapTimersByWidgetId) {
			for (int appWidgetId : appWidgetIds) {
				// Arrêt du timer.
				if (mapTimersByWidgetId.containsKey(appWidgetId)) {
					mapTimersByWidgetId.get(appWidgetId).cancel();
					mapTimersByWidgetId.remove(appWidgetId);
				}
				TransportsWidgetConfigure.deleteSettings(context, appWidgetId);
			}
		}
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		LOG_YBO.debug("onEnable");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		LOG_YBO.debug("onDisable");
		TransportsWidgetConfigure.deleteAllSettings(context);
		super.onDisabled(context);
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		LOG_YBO.debug("UpdateAppWidget : " + appWidgetId);
		if (mapTimersByWidgetId.containsKey(appWidgetId)) {
			return;
		}
		List<ArretFavori> favorisSelects = TransportsWidgetConfigure.loadSettings(context, appWidgetId);
		if (favorisSelects.isEmpty()) {
			LOG_YBO.debug("Pas de favoris trouvés dans la conf.");
			return;
		}
		ArrayList<ArretFavori> favorisBdd = new ArrayList<ArretFavori>(favorisSelects.size());
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_arrets);
		ArretFavori favoriBdd;
		for (ArretFavori favoriSelect : favorisSelects) {
			favoriBdd = TransportsRennesApplication.getDataBaseHelper().selectSingle(favoriSelect);
			if (favoriBdd == null) {
				LOG_YBO.debug("FavoriBdd null");
				return;
			}
			if (favoriBdd.nomArret.length() > 13) {
				favoriBdd.nomArret = favoriBdd.nomArret.substring(0, 12) + "...";
			}
			if (favoriBdd.direction.length() > 18) {
				favoriBdd.direction = favoriBdd.direction.substring(0, 16) + "...";
			}
			favorisBdd.add(favoriBdd);
		}
		WidgetUpdateUtil.updateAppWidget(context, views, favorisBdd);

		appWidgetManager.updateAppWidget(appWidgetId, views);
		Timer timer = new Timer();
		synchronized (mapTimersByWidgetId) {
			mapTimersByWidgetId.put(appWidgetId, timer);
		}
		timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager, favorisBdd, appWidgetId), 2000, 2000);
	}

	private static class MyTime extends TimerTask {
		AppWidgetManager appWidgetManager;
		Context context;

		private ArrayList<ArretFavori> favoris;
		private int appWidgetId;

		public MyTime(Context context, AppWidgetManager appWidgetManager, ArrayList<ArretFavori> favoris, int appWidgetId) {
			this.context = context;
			this.appWidgetManager = appWidgetManager;
			this.favoris = favoris;
			this.appWidgetId = appWidgetId;
		}

		@Override
		public void run() {
			LOG_YBO.debug("MyTimer.run");
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_arrets);
			WidgetUpdateUtil.updateAppWidget(context, remoteViews, favoris);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[]{appWidgetId});
			} else {
				super.onReceive(context, intent);
			}
		} else if (action.startsWith("YboClick")) {
			String[] champs = action.split("_");
			if (champs.length == 3) {
				ArretFavori favori = new ArretFavori();
				favori.arretId = champs[1];
				favori.ligneId = champs[2];
				favori = TransportsRennesApplication.getDataBaseHelper().selectSingle(favori);
				if (favori != null) {
					Intent startIntent = new Intent(context, DetailArret.class);
					startIntent.putExtra("favori", favori);
					startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

					context.startActivity(startIntent);
				}
			}
		}
		super.onReceive(context, intent);
	}
}

