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

package fr.ybo.transportsrennes.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.RemoteViews;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.TransportsWidget;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetUpdateUtil {

	private WidgetUpdateUtil() {
		super();
	}

	public static void updateAppWidget(final Context context, final RemoteViews views, final ArrayList<ArretFavori> favoris) {

		switch (favoris.size()) {
			case 1:
				updateAppWidget1Arret(context, views, favoris.get(0));
				remplirRemoteViews1Arret(context, views, favoris);
				break;
			case 2:
				updateAppWidget2Arret(context, views, favoris.get(0), favoris.get(1));
				remplirRemoteViews2Arret(context, views, favoris);
				break;
			case 3:
				updateAppWidget3Arret(context, views, favoris.get(0), favoris.get(1), favoris.get(2));
				remplirRemoteViews3Arret(context, views, favoris);
				break;
		}
	}


	private static void updateAppWidget1Arret(final Context context, final RemoteViews views, final ArretFavori favori) {
		views.setTextViewText(R.id.nomArret_1arret, favori.nomArret);
		views.setTextViewText(R.id.direction_1arret, "-> " + favori.direction);
		views.setImageViewResource(R.id.iconeLigne_1arret, IconeLigne.getIconeResource(favori.nomCourt));
		final Intent intent = new Intent(context, TransportsWidget.class);
		intent.setAction("YboClick_" + favori.arretId + "_" + favori.ligneId);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);
		views.setViewVisibility(R.id.layout_1arret, View.VISIBLE);
		views.setViewVisibility(R.id.layout_2arret, View.INVISIBLE);
		views.setViewVisibility(R.id.layout_3arret, View.INVISIBLE);
	}

	private static void updateAppWidget2Arret(final Context context, final RemoteViews views, final ArretFavori favori1, final ArretFavori favori2) {
		views.setTextViewText(R.id.nomArret1_2arret, favori1.nomArret);
		views.setTextViewText(R.id.direction1_2arret, "-> " + favori1.direction);
		views.setImageViewResource(R.id.iconeLigne1_2arret, IconeLigne.getIconeResource(favori1.nomCourt));
		views.setTextViewText(R.id.nomArret2_2arret, favori2.nomArret);
		views.setTextViewText(R.id.direction2_2arret, "-> " + favori2.direction);
		views.setImageViewResource(R.id.iconeLigne2_2arret, IconeLigne.getIconeResource(favori2.nomCourt));
		final Intent intent1 = new Intent(context, TransportsWidget.class);
		intent1.setAction("YboClick_" + favori1.arretId + "_" + favori1.ligneId);
		final PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.layout_2arret_1, pendingIntent1);
		final Intent intent2 = new Intent(context, TransportsWidget.class);
		intent2.setAction("YboClick_" + favori2.arretId + "_" + favori2.ligneId);
		final PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.layout_2arret_2, pendingIntent2);
		views.setViewVisibility(R.id.layout_1arret, View.INVISIBLE);
		views.setViewVisibility(R.id.layout_2arret, View.VISIBLE);
		views.setViewVisibility(R.id.layout_3arret, View.INVISIBLE);
	}

	private static void updateAppWidget3Arret(final Context context, final RemoteViews views, final ArretFavori favori1, final ArretFavori favori2, final ArretFavori favori3) {
		views.setTextViewText(R.id.nomArret1_3arret, favori1.nomArret);
		views.setTextViewText(R.id.direction1_3arret, "-> " + favori1.direction);
		views.setImageViewResource(R.id.iconeLigne1_3arret, IconeLigne.getIconeResource(favori1.nomCourt));
		views.setTextViewText(R.id.nomArret2_3arret, favori2.nomArret);
		views.setTextViewText(R.id.direction2_3arret, "-> " + favori2.direction);
		views.setImageViewResource(R.id.iconeLigne2_3arret, IconeLigne.getIconeResource(favori2.nomCourt));
		views.setTextViewText(R.id.nomArret3_3arret, favori3.nomArret);
		views.setTextViewText(R.id.direction3_3arret, "-> " + favori3.direction);
		views.setImageViewResource(R.id.iconeLigne3_3arret, IconeLigne.getIconeResource(favori3.nomCourt));
		final Intent intent1 = new Intent(context, TransportsWidget.class);
		intent1.setAction("YboClick_" + favori1.arretId + "_" + favori1.ligneId);
		final PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.layout_3arret_1, pendingIntent1);
		final Intent intent2 = new Intent(context, TransportsWidget.class);
		intent2.setAction("YboClick_" + favori2.arretId + "_" + favori2.ligneId);
		final PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.layout_3arret_2, pendingIntent2);
		final Intent intent3 = new Intent(context, TransportsWidget.class);
		intent3.setAction("YboClick_" + favori3.arretId + "_" + favori3.ligneId);
		final PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.layout_3arret_3, pendingIntent3);
		views.setViewVisibility(R.id.layout_1arret, View.INVISIBLE);
		views.setViewVisibility(R.id.layout_2arret, View.INVISIBLE);
		views.setViewVisibility(R.id.layout_3arret, View.VISIBLE);
	}

	public static Map<Integer, Integer> requete(final ArretFavori favori, final int limit, final Calendar calendar, final int now) {
		final Calendar calendarLaVeille = Calendar.getInstance();
		calendarLaVeille.roll(Calendar.DATE, false);
		final StringBuilder requete = new StringBuilder();
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
		final int uneJournee = 24 * 60;
		// Réquète.
		final List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(Integer.toString(uneJournee));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now + uneJournee));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now));
		final Map<Integer, Integer> mapProchainsDepart = new HashMap<Integer, Integer>();
		try {
			int count = 1;
			final Cursor currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
			while (currentCursor.moveToNext()) {
				mapProchainsDepart.put(count, currentCursor.getInt(0));
				count++;
			}
			currentCursor.close();
		} catch (SQLiteException ignored) {
		}
		return mapProchainsDepart;
	}

	private static void remplirRemoteViews1Arret(final Context context, final RemoteViews remoteViews, final List<ArretFavori> favoris) {

		final Calendar calendar = Calendar.getInstance();
		final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		final Map<Integer, Integer> mapProchainsDepart = requete(favoris.get(0), 4, calendar, now);
		remoteViews.setTextViewText(R.id.tempsRestant1_1arret, mapProchainsDepart.get(1) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart.get(1), now));
		remoteViews.setTextViewText(R.id.tempsRestant2_1arret, mapProchainsDepart.get(2) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart.get(2), now));
		remoteViews.setTextViewText(R.id.tempsRestant3_1arret, mapProchainsDepart.get(3) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart.get(3), now));
		remoteViews.setTextViewText(R.id.tempsRestant4_1arret, mapProchainsDepart.get(4) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart.get(4), now));

	}

	private static void remplirRemoteViews2Arret(final Context context, final RemoteViews remoteViews, final List<ArretFavori> favoris) {
		final Calendar calendar = Calendar.getInstance();
		final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		final Map<Integer, Integer> mapProchainsDepart1 = requete(favoris.get(0), 2, calendar, now);
		remoteViews.setTextViewText(R.id.tempsRestant11_2arret, mapProchainsDepart1.get(1) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart1.get(1), now));
		remoteViews.setTextViewText(R.id.tempsRestant12_2arret, mapProchainsDepart1.get(2) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart1.get(2), now));
		final Map<Integer, Integer> mapProchainsDepart2 = requete(favoris.get(1), 2, calendar, now);
		remoteViews.setTextViewText(R.id.tempsRestant21_2arret, mapProchainsDepart2.get(1) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart2.get(1), now));
		remoteViews.setTextViewText(R.id.tempsRestant22_2arret, mapProchainsDepart2.get(2) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart2.get(2), now));
	}

	private static void remplirRemoteViews3Arret(final Context context, final RemoteViews remoteViews, final List<ArretFavori> favoris) {
		final Calendar calendar = Calendar.getInstance();
		final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		final Map<Integer, Integer> mapProchainsDepart1 = requete(favoris.get(0), 1, calendar, now);
		remoteViews.setTextViewText(R.id.tempsRestant1_3arret, mapProchainsDepart1.get(1) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart1.get(1), now));

		final Map<Integer, Integer> mapProchainsDepart2 = requete(favoris.get(1), 1, calendar, now);
		remoteViews.setTextViewText(R.id.tempsRestant2_3arret, mapProchainsDepart2.get(1) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart2.get(1), now));

		final Map<Integer, Integer> mapProchainsDepart3 = requete(favoris.get(2), 1, calendar, now);
		remoteViews.setTextViewText(R.id.tempsRestant3_3arret, mapProchainsDepart3.get(1) == null ? "" :
				context.getString(R.string.dans) + " " + formatterCalendar(context, mapProchainsDepart3.get(1), now));
	}

	public static String formatterCalendar(final Context context, final int prochainDepart, final int now) {
		final StringBuilder stringBuilder = new StringBuilder();
		final int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append(context.getString(R.string.tropTard));
		} else {
			final int heures = tempsEnMinutes / 60;
			final int minutes = tempsEnMinutes - heures * 60;
			boolean tempsAjoute = false;
			if (heures > 0) {
				stringBuilder.append(heures);
				stringBuilder.append(' ');
				stringBuilder.append(context.getString(R.string.miniHeures));
				stringBuilder.append(' ');
				tempsAjoute = true;
			}
			if (minutes > 0) {
				if (heures <= 0) {
					stringBuilder.append(minutes);
					stringBuilder.append(' ');
					stringBuilder.append(context.getString(R.string.miniMinutes));
				} else {
					if (minutes < 10) {
						stringBuilder.append("0");
					}
					stringBuilder.append(minutes);
				}
				tempsAjoute = true;
			}
			if (!tempsAjoute) {
				stringBuilder.append("0 ");
				stringBuilder.append(context.getString(R.string.miniMinutes));
			}
		}
		return stringBuilder.toString();
	}

	private static String clauseWhereForTodayCalendrier(final Calendar calendar) {
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
