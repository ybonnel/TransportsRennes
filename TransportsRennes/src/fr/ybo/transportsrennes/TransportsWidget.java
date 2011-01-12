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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.widget.RemoteViews;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.JoursFeries;

import java.lang.reflect.Field;
import java.util.*;

public class TransportsWidget extends AppWidgetProvider {
	private final static Class<?> classDrawable = R.drawable.class;

	private Timer timer = null;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		if (timer != null) {
			timer.cancel();
		}
		SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", 0);
		ArretFavori favoriSelect = new ArretFavori();
		Map<Integer, ArretFavori> mapArretFavoris = new HashMap<Integer, ArretFavori>();
		for (int appWidgetId : appWidgetIds) {
			favoriSelect.arretId = sharedPreferences.getString("ArretId_" + appWidgetId, null);
			favoriSelect.ligneId = sharedPreferences.getString("LigneId_" + appWidgetId, null);
			mapArretFavoris.put(appWidgetId, TransportsRennesApplication.getDataBaseHelper().selectSingle(favoriSelect));
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			views.setTextViewText(R.id.nomArret, mapArretFavoris.get(appWidgetId).nomArret);
			views.setTextViewText(R.id.directionArret, mapArretFavoris.get(appWidgetId).direction);
			try {
				Field fieldIcon = classDrawable.getDeclaredField("i" + mapArretFavoris.get(appWidgetId).nomCourt.toLowerCase());
				int ressourceImg = fieldIcon.getInt(null);
				views.setImageViewResource(R.id.iconeLigne, ressourceImg);
			} catch (Exception ignore) {
			}
			appWidgetManager.updateAppWidget(appWidgetIds, views);
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager, mapArretFavoris), 1, 1000);
	}

	private class MyTime extends TimerTask {
		;
		AppWidgetManager appWidgetManager;
		Context context;

		private Map<Integer, ArretFavori> mapArretFavoris;

		public MyTime(Context context, AppWidgetManager appWidgetManager, Map<Integer, ArretFavori> mapArretFavoris) {
			this.context = context;
			this.appWidgetManager = appWidgetManager;
			this.mapArretFavoris = mapArretFavoris;
		}

		@Override
		public void run() {

			Calendar calendar = Calendar.getInstance();
			Calendar calendarLaVeille = Calendar.getInstance();
			calendarLaVeille.roll(Calendar.DATE, false);
			int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			StringBuilder requete = new StringBuilder();
			requete.append("select (Horaire.heureDepart - :uneJournee) as _id ");
			requete.append("from Calendrier,  Horaire_@ligneId@");
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
			requete.append("@ligneId@");
			requete.append(" as Horaire, Trajet ");
			requete.append("where ");
			requete.append(clauseWhereForTodayCalendrier(calendar));
			requete.append(" and Trajet.id = Horaire.trajetId");
			requete.append(" and Trajet.calendrierId = Calendrier.id");
			requete.append(" and Trajet.ligneId = :routeId2");
			requete.append(" and Horaire.arretId = :arretId2");
			requete.append(" and Horaire.heureDepart >= :maintenant");
			requete.append(" and Horaire.terminus = 0");
			requete.append(" order by _id limit 2;");
			int uneJournee = 24 * 60;

			for (Map.Entry<Integer, ArretFavori> entry : mapArretFavoris.entrySet()) {
				// Réquète.


				List<String> selectionArgs = new ArrayList<String>();
				selectionArgs.add(Integer.toString(uneJournee));
				selectionArgs.add(entry.getValue().ligneId);
				selectionArgs.add(entry.getValue().arretId);
				selectionArgs.add(Integer.toString(now + (uneJournee)));
				selectionArgs.add(entry.getValue().ligneId);
				selectionArgs.add(entry.getValue().arretId);
				selectionArgs.add(Integer.toString(now));
				Integer prochainDepart1 = null;
				Integer prochainDepart2 = null;
				try {
					Cursor currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
					while (currentCursor.moveToNext()) {
						if (prochainDepart1 != null) {
							prochainDepart2 = currentCursor.getInt(0);
						} else {
							prochainDepart1 = currentCursor.getInt(0);
						}
					}
					currentCursor.close();
				} catch (SQLiteException ignored) {
				}
				RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
				if (prochainDepart1 != null) {
					remoteViews.setTextViewText(R.id.tempsRestant1, formatterCalendar(prochainDepart1, now));
				} else {
					remoteViews.setTextViewText(R.id.tempsRestant1, "");
				}
				if (prochainDepart2 != null) {
					remoteViews.setTextViewText(R.id.tempsRestant2, formatterCalendar(prochainDepart2, now));
				} else {
					remoteViews.setTextViewText(R.id.tempsRestant2, "");
				}
				appWidgetManager.updateAppWidget(entry.getKey(), remoteViews);
			}
		}


		private String formatterCalendar(int prochainDepart, int now) {
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

		private String clauseWhereForTodayCalendrier(Calendar calendar) {
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
		}
	}
}

