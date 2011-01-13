/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.widget.RemoteViews;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.JoursFeries;
import fr.ybo.transportsrennes.util.LogYbo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TransportsWidget extends AppWidgetProvider {
	private final static LogYbo LOG_YBO = new LogYbo(TransportsWidget.class);

	private final static Class<?> classDrawable = R.drawable.class;

	private final static Map<Integer, Timer> mapTimersByWidgetId = new HashMap<Integer, Timer>();

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
		LOG_YBO.debug("onDeleted");
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
		super.onEnabled(context);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void onDisabled(Context context) {
		LOG_YBO.debug("onDisable");
		super.onDisabled(context);    //To change body of overridden methods use File | Settings | File Templates.
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		if (mapTimersByWidgetId.containsKey(appWidgetId)) {
			return;
		}
		LOG_YBO.debug("UpdateAppWidget : " + appWidgetId);
		ArretFavori favoriSelect = TransportsWidgetConfigure.loadSettings(context, appWidgetId);
		if (favoriSelect == null) {
			return;
		}
		ArretFavori favori = TransportsRennesApplication.getDataBaseHelper().selectSingle(favoriSelect);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		views.setTextViewText(R.id.nomArret, favori.nomArret);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			views.setImageViewResource(R.id.iconeLigne, ressourceImg);
		} catch (Exception ignore) {
		}
		final Intent intent = new Intent(context, TransportsWidget.class);
		intent.setAction("YboClick");
		intent.putExtra("favori", favori);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		Timer timer = new Timer();
		synchronized (mapTimersByWidgetId) {
			mapTimersByWidgetId.put(appWidgetId, timer);
		}
		timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager, favori, appWidgetId), 2000, 20000);
	}

	private static class MyTime extends TimerTask {
		AppWidgetManager appWidgetManager;
		Context context;

		private ArretFavori favori;
		private int appWidgetId;

		public MyTime(Context context, AppWidgetManager appWidgetManager, ArretFavori favori, int appWidgetId) {
			this.context = context;
			this.appWidgetManager = appWidgetManager;
			this.favori = favori;
			this.appWidgetId = appWidgetId;
		}

		@Override
		public void run() {

			Calendar calendar = Calendar.getInstance();
			Calendar calendarLaVeille = Calendar.getInstance();
			calendarLaVeille.roll(Calendar.DATE, false);
			int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			StringBuilder requete = new StringBuilder();
			requete.append("select (Horaire.heureDepart - :uneJournee) as _id ");
			requete.append("from Calendrier,  Horaire_");
			requete.append(favori.ligneId);
			requete.append(" as Horaire, Trajet ");
			requete.append("where ");
			requete.append(clauseWhereForTodayCalendrier(calendarLaVeille));
			requete.append(" and Trajet.id = Horaire.trajetId");
			requete.append(" and Trajet.calendrierId = Calendrier.id");
			requete.append(" and Trajet.ligneId = :routeId1");
			requete.append(" and Horaire.arretId = :arretId1");
			requete.append(" and Horaire.heureDepart >= :maintenantHier ");
			requete.append(" and Horaire.terminus = 0 ");
			requete.append("UNION ");
			requete.append("select Horaire.heureDepart as _id ");
			requete.append("from Calendrier,  Horaire_");
			requete.append(favori.ligneId);
			requete.append(" as Horaire, Trajet ");
			requete.append("where ");
			requete.append(clauseWhereForTodayCalendrier(calendar));
			requete.append(" and Trajet.id = Horaire.trajetId");
			requete.append(" and Trajet.calendrierId = Calendrier.id");
			requete.append(" and Trajet.ligneId = :routeId2");
			requete.append(" and Horaire.arretId = :arretId2");
			requete.append(" and Horaire.heureDepart >= :maintenant");
			requete.append(" and Horaire.terminus = 0");
			requete.append(" order by _id limit 4;");
			int uneJournee = 24 * 60;
			// Réquète.
			List<String> selectionArgs = new ArrayList<String>();
			selectionArgs.add(Integer.toString(uneJournee));
			selectionArgs.add(favori.ligneId);
			selectionArgs.add(favori.arretId);
			selectionArgs.add(Integer.toString(now + (uneJournee)));
			selectionArgs.add(favori.ligneId);
			selectionArgs.add(favori.arretId);
			selectionArgs.add(Integer.toString(now));
			Map<Integer, Integer> mapProchainsDepart = new HashMap<Integer, Integer>();
			try {
				int count = 1;
				Cursor currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
				while (currentCursor.moveToNext()) {
					mapProchainsDepart.put(count, currentCursor.getInt(0));
					count++;
				}
				currentCursor.close();
			} catch (SQLiteException ignored) {
			}
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			remoteViews.setTextViewText(R.id.tempsRestant1,
					mapProchainsDepart.get(1) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(1), now));
			remoteViews.setTextViewText(R.id.tempsRestant2,
					mapProchainsDepart.get(2) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(2), now));
			remoteViews.setTextViewText(R.id.tempsRestant3,
					mapProchainsDepart.get(3) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(3), now));
			remoteViews.setTextViewText(R.id.tempsRestant4,
					mapProchainsDepart.get(4) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(4), now));
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}


	private static String formatterCalendar(int prochainDepart, int now) {
		StringBuilder stringBuilder = new StringBuilder();
		int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append("Trop tard!");
		} else {
			int heures = tempsEnMinutes / 60;
			int minutes = tempsEnMinutes - heures * 60;
			boolean tempsAjoute = false;
			if (heures > 0) {
				stringBuilder.append(heures);
				stringBuilder.append(" h ");
				tempsAjoute = true;
			}
			if (minutes > 0) {
				if (heures <= 0) {
					stringBuilder.append(minutes);
					stringBuilder.append(" min");
				} else {
					if (minutes < 10) {
						stringBuilder.append("0");
					}
					stringBuilder.append(minutes);
				}
				tempsAjoute = true;
			}
			if (!tempsAjoute) {
				stringBuilder.append("0 min");
			}
		}
		return stringBuilder.toString();
	}

	private static String clauseWhereForTodayCalendrier(Calendar calendar) {
		if (JoursFeries.isJourFerie(calendar.getTime())) {
			return "Dimanche = 1";
		}
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				return "Lundi = 1";
			case Calendar.TUESDAY:
				return "Mardi = 1";
			case Calendar.WEDNESDAY:
				return "Mercredi = 1";
			case Calendar.THURSDAY:
				return "Jeudi = 1";
			case Calendar.FRIDAY:
				return "Vendredi = 1";
			case Calendar.SATURDAY:
				return "Samedi = 1";
			case Calendar.SUNDAY:
				return "Dimanche = 1";
			default:
				return null;
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
		} else if ("YboClick".equals(action)) {
			ArretFavori favori = (ArretFavori) intent.getExtras().getSerializable("favori");
			Intent startIntent = new Intent(context, DetailArret.class);
			startIntent.putExtra("favori", favori);
			startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIntent);
		}
		super.onReceive(context, intent);
	}
}

