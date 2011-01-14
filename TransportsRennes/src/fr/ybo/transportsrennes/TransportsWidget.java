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
import android.view.View;
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

	static void updateAppWidget1Arret(Context context, RemoteViews views, ArretFavori favori) {
		views.setTextViewText(R.id.nomArret_1arret, favori.nomArret);
		views.setTextViewText(R.id.direction_1arret, "-> " + favori.direction);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			views.setImageViewResource(R.id.iconeLigne_1arret, ressourceImg);
		} catch (Exception ignore) {
		}
		Intent intent = new Intent(context, TransportsWidget.class);
		intent.setAction("YboClick_" + favori.arretId + "_" + favori.ligneId);
		LOG_YBO.debug("Action dans l'intent : " + intent.getAction());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);
		views.setViewVisibility(R.id.layout_1arret, View.VISIBLE);
		views.setViewVisibility(R.id.layout_2arret, View.INVISIBLE);
	}

	static void updateAppWidget2Arret(Context context, RemoteViews views, ArretFavori favori1, ArretFavori favori2) {
		views.setTextViewText(R.id.nomArret1_2arret, favori1.nomArret);
		views.setTextViewText(R.id.direction1_2arret, "-> " + favori1.direction);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori1.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			views.setImageViewResource(R.id.iconeLigne1_2arret, ressourceImg);
		} catch (Exception ignore) {
		}
		views.setTextViewText(R.id.nomArret2_2arret, favori2.nomArret);
		views.setTextViewText(R.id.direction2_2arret, "-> " + favori2.direction);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori2.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			views.setImageViewResource(R.id.iconeLigne2_2arret, ressourceImg);
		} catch (Exception ignore) {
		}
		final Intent intent1 = new Intent(context, TransportsWidget.class);
		intent1.setAction("YboClick_" + favori1.arretId + "_" + favori1.ligneId);
		LOG_YBO.debug("Action dans l'intent : " + intent1.getAction());
		PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.layout_2arret_1, pendingIntent1);
		Intent intent2 = new Intent(context, TransportsWidget.class);
		intent2.setAction("YboClick_" + favori2.arretId + "_" + favori2.ligneId);
		LOG_YBO.debug("Action dans l'intent : " + intent2.getAction());
		PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.layout_2arret_2, pendingIntent2);
		views.setViewVisibility(R.id.layout_1arret, View.INVISIBLE);
		views.setViewVisibility(R.id.layout_2arret, View.VISIBLE);
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		if (mapTimersByWidgetId.containsKey(appWidgetId)) {
			return;
		}
		LOG_YBO.debug("UpdateAppWidget : " + appWidgetId);
		List<ArretFavori> favorisSelects = TransportsWidgetConfigure.loadSettings(context, appWidgetId);
		if (favorisSelects.isEmpty()) {
			LOG_YBO.debug("Pas de favoris trouvés dans la conf.");
			return;
		}
		List<ArretFavori> favorisBdd = new ArrayList<ArretFavori>(favorisSelects.size());
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
		switch (favorisSelects.size()) {
			case 1:
				updateAppWidget1Arret(context, views, favorisBdd.get(0));
				break;
			case 2:
				updateAppWidget2Arret(context, views, favorisBdd.get(0),
						favorisBdd.get(1));
				break;
		}


		appWidgetManager.updateAppWidget(appWidgetId, views);
		Timer timer = new Timer();
		synchronized (mapTimersByWidgetId) {
			mapTimersByWidgetId.put(appWidgetId, timer);
		}
		timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager, favorisBdd, appWidgetId), 20000, 20000);
	}

	private static class MyTime extends TimerTask {
		AppWidgetManager appWidgetManager;
		Context context;

		private List<ArretFavori> favoris;
		private int appWidgetId;

		public MyTime(Context context, AppWidgetManager appWidgetManager, List<ArretFavori> favoris, int appWidgetId) {
			this.context = context;
			this.appWidgetManager = appWidgetManager;
			this.favoris = favoris;
			this.appWidgetId = appWidgetId;
		}

		private static Map<Integer, Integer> requete(ArretFavori favori, int limit, Calendar calendar, int now) {
			Calendar calendarLaVeille = Calendar.getInstance();
			calendarLaVeille.roll(Calendar.DATE, false);
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
			requete.append(" order by _id limit ");
			requete.append(limit);
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
			return mapProchainsDepart;
		}

		public void remplirRemoteViews1Arret(RemoteViews remoteViews) {
			remplirRemoteViews1Arret(remoteViews, favoris);
		}

		public static void remplirRemoteViews1Arret(RemoteViews remoteViews, List<ArretFavori> favoris) {

			Calendar calendar = Calendar.getInstance();
			int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			Map<Integer, Integer> mapProchainsDepart = requete(favoris.get(0), 4, calendar, now);
			remoteViews.setTextViewText(R.id.tempsRestant1_1arret,
					mapProchainsDepart.get(1) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(1), now));
			remoteViews.setTextViewText(R.id.tempsRestant2_1arret,
					mapProchainsDepart.get(2) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(2), now));
			remoteViews.setTextViewText(R.id.tempsRestant3_1arret,
					mapProchainsDepart.get(3) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(3), now));
			remoteViews.setTextViewText(R.id.tempsRestant4_1arret,
					mapProchainsDepart.get(4) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart.get(4), now));

		}

		public void remplirRemoteViews2Arret(RemoteViews remoteViews) {
			remplirRemoteViews2Arret(remoteViews, favoris);
		}

		public static void remplirRemoteViews2Arret(RemoteViews remoteViews, List<ArretFavori> favoris) {
			Calendar calendar = Calendar.getInstance();
			int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			Map<Integer, Integer> mapProchainsDepart1 = requete(favoris.get(0), 2, calendar, now);
			remoteViews.setTextViewText(R.id.tempsRestant11_2arret,
					mapProchainsDepart1.get(1) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart1.get(1), now));
			remoteViews.setTextViewText(R.id.tempsRestant12_2arret,
					mapProchainsDepart1.get(2) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart1.get(2), now));
			Map<Integer, Integer> mapProchainsDepart2 = requete(favoris.get(1), 2, calendar, now);
			remoteViews.setTextViewText(R.id.tempsRestant21_2arret,
					mapProchainsDepart2.get(1) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart2.get(1), now));
			remoteViews.setTextViewText(R.id.tempsRestant22_2arret,
					mapProchainsDepart2.get(2) == null ? "" : "dans " + formatterCalendar(mapProchainsDepart2.get(2), now));
		}

		@Override
		public void run() {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_arrets);
			switch (favoris.size()) {
				case 1:
					remplirRemoteViews1Arret(remoteViews);
					break;
				case 2:
					remplirRemoteViews2Arret(remoteViews);
					break;
			}
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
		LOG_YBO.debug("Action recue : " + intent.getAction());
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
				Intent startIntent = new Intent(context, DetailArret.class);
					startIntent.putExtra("favori", TransportsRennesApplication.getDataBaseHelper().selectSingle(favori));
					startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(startIntent);
			}
		}
		super.onReceive(context, intent);
	}
}

