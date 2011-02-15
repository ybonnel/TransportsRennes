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

	private static final LogYbo LOG_YBO = new LogYbo(TransportsWidget.class);

	private static final Map<Integer, Timer> MAP_TIMERS_BY_WIDGET_ID = new HashMap<Integer, Timer>(5);

	public static void verifKiller(final Context context, final AppWidgetManager appWidgetManager) {
		if (MAP_TIMERS_BY_WIDGET_ID.size() == 0) {
			for (final int widgetId : TransportsWidgetConfigure.getWidgetIds(context)) {
				LOG_YBO.debug("Le widget " + widgetId + " a du être killer, on le relance");
				updateAppWidget(context, appWidgetManager, widgetId);
			}
		}
	}

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		LOG_YBO.debug("onUpdate");
		for (final int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(final Context context, final int[] appWidgetIds) {
		synchronized (MAP_TIMERS_BY_WIDGET_ID) {
			for (final int appWidgetId : appWidgetIds) {
				// Arrêt du timer.
				if (MAP_TIMERS_BY_WIDGET_ID.containsKey(appWidgetId)) {
					MAP_TIMERS_BY_WIDGET_ID.get(appWidgetId).cancel();
					MAP_TIMERS_BY_WIDGET_ID.remove(appWidgetId);
				}
				TransportsWidgetConfigure.deleteSettings(context, appWidgetId);
			}
		}
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(final Context context) {
		LOG_YBO.debug("onEnable");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(final Context context) {
		LOG_YBO.debug("onDisable");
		TransportsWidgetConfigure.deleteAllSettings(context);
		super.onDisabled(context);
	}

	static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
		LOG_YBO.debug("UpdateAppWidget : " + appWidgetId);
		if (MAP_TIMERS_BY_WIDGET_ID.containsKey(appWidgetId)) {
			return;
		}
		final List<ArretFavori> favorisSelects = TransportsWidgetConfigure.loadSettings(context, appWidgetId);
		if (favorisSelects.isEmpty()) {
			LOG_YBO.debug("Pas de favoris trouvés dans la conf.");
			return;
		}
		final ArrayList<ArretFavori> favorisBdd = new ArrayList<ArretFavori>(favorisSelects.size());
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_arrets);
		for (final ArretFavori favoriSelect : favorisSelects) {
			ArretFavori favoriBdd = TransportsRennesApplication.getDataBaseHelper().selectSingle(favoriSelect);
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
		final Timer timer = new Timer();
		synchronized (MAP_TIMERS_BY_WIDGET_ID) {
			MAP_TIMERS_BY_WIDGET_ID.put(appWidgetId, timer);
		}
		timer.scheduleAtFixedRate(new TransportsWidget.MyTime(context, appWidgetManager, favorisBdd, appWidgetId), 2000, 2000);
	}

	private static class MyTime extends TimerTask {
		final AppWidgetManager appWidgetManager;
		final Context context;

		private final ArrayList<ArretFavori> favoris;
		private final int appWidgetId;

		MyTime(final Context context, final AppWidgetManager appWidgetManager, final ArrayList<ArretFavori> favoris, final int appWidgetId) {
			super();
			this.context = context;
			this.appWidgetManager = appWidgetManager;
			this.favoris = favoris;
			this.appWidgetId = appWidgetId;
		}

		@Override
		public void run() {
			LOG_YBO.debug("MyTimer.run");
			final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_arrets);
			WidgetUpdateUtil.updateAppWidget(context, remoteViews, favoris);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}


	@Override
	public void onReceive(final Context context, final Intent intent) {
		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
				super.onReceive(context, intent);
			} else {
				onDeleted(context, new int[]{appWidgetId});
			}
		} else if (action.startsWith("YboClick")) {
			final String[] champs = action.split("_");
			if (champs.length == 3) {
				ArretFavori favori = new ArretFavori();
				favori.arretId = champs[1];
				favori.ligneId = champs[2];
				favori = TransportsRennesApplication.getDataBaseHelper().selectSingle(favori);
				if (favori != null) {
					final Intent startIntent = new Intent(context, DetailArret.class);
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

