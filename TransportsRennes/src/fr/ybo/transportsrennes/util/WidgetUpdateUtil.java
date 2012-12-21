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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.RemoteViews;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.util.Formatteur;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget;

public class WidgetUpdateUtil {

    private WidgetUpdateUtil() {
    }

    public static void updateAppWidget(Context context, RemoteViews views, ArrayList<ArretFavori> favoris,
                                       Calendar calendar) {

        switch (favoris.size()) {
            case 1:
                updateAppWidget1Arret(context, views, favoris.get(0));
                remplirRemoteViews1Arret(context, views, favoris, calendar);
                break;
            case 2:
                updateAppWidget2Arret(context, views, favoris.get(0), favoris.get(1));
                remplirRemoteViews2Arret(context, views, favoris, calendar);
                break;
            case 3:
                updateAppWidget3Arret(context, views, favoris.get(0), favoris.get(1), favoris.get(2));
                remplirRemoteViews3Arret(context, views, favoris, calendar);
                break;
        }
    }

    private static void updateAppWidget1Arret(Context context, RemoteViews views, ArretFavori favori) {
        views.setTextViewText(R.id.nomArret_1arret, favori.nomArret);
        views.setTextViewText(R.id.direction_1arret, "-> " + favori.direction);
        views.setImageViewResource(R.id.iconeLigne_1arret, IconeLigne.getIconeResource(favori.nomCourt));
        Intent intent = new Intent(context, TransportsWidget.class);
        intent.setAction("YboClick_" + favori.arretId + '_' + favori.ligneId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);
        views.setViewVisibility(R.id.layout_1arret, View.VISIBLE);
        views.setViewVisibility(R.id.layout_2arret, View.INVISIBLE);
        views.setViewVisibility(R.id.layout_3arret, View.INVISIBLE);
    }

    private static void updateAppWidget2Arret(Context context, RemoteViews views, ArretFavori favori1,
                                              ArretFavori favori2) {
        views.setTextViewText(R.id.nomArret1_2arret, favori1.nomArret);
        views.setTextViewText(R.id.direction1_2arret, "-> " + favori1.direction);
        views.setImageViewResource(R.id.iconeLigne1_2arret, IconeLigne.getIconeResource(favori1.nomCourt));
        views.setTextViewText(R.id.nomArret2_2arret, favori2.nomArret);
        views.setTextViewText(R.id.direction2_2arret, "-> " + favori2.direction);
        views.setImageViewResource(R.id.iconeLigne2_2arret, IconeLigne.getIconeResource(favori2.nomCourt));
        Intent intent1 = new Intent(context, TransportsWidget.class);
        intent1.setAction("YboClick_" + favori1.arretId + '_' + favori1.ligneId);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_2arret_1, pendingIntent1);
        Intent intent2 = new Intent(context, TransportsWidget.class);
        intent2.setAction("YboClick_" + favori2.arretId + '_' + favori2.ligneId);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_2arret_2, pendingIntent2);
        views.setViewVisibility(R.id.layout_1arret, View.INVISIBLE);
        views.setViewVisibility(R.id.layout_2arret, View.VISIBLE);
        views.setViewVisibility(R.id.layout_3arret, View.INVISIBLE);
    }

    private static void updateAppWidget3Arret(Context context, RemoteViews views, ArretFavori favori1,
                                              ArretFavori favori2, ArretFavori favori3) {
        views.setTextViewText(R.id.nomArret1_3arret, favori1.nomArret);
        views.setTextViewText(R.id.direction1_3arret, "-> " + favori1.direction);
        views.setImageViewResource(R.id.iconeLigne1_3arret, IconeLigne.getIconeResource(favori1.nomCourt));
        views.setTextViewText(R.id.nomArret2_3arret, favori2.nomArret);
        views.setTextViewText(R.id.direction2_3arret, "-> " + favori2.direction);
        views.setImageViewResource(R.id.iconeLigne2_3arret, IconeLigne.getIconeResource(favori2.nomCourt));
        views.setTextViewText(R.id.nomArret3_3arret, favori3.nomArret);
        views.setTextViewText(R.id.direction3_3arret, "-> " + favori3.direction);
        views.setImageViewResource(R.id.iconeLigne3_3arret, IconeLigne.getIconeResource(favori3.nomCourt));
        Intent intent1 = new Intent(context, TransportsWidget.class);
        intent1.setAction("YboClick_" + favori1.arretId + '_' + favori1.ligneId);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_3arret_1, pendingIntent1);
        Intent intent2 = new Intent(context, TransportsWidget.class);
        intent2.setAction("YboClick_" + favori2.arretId + '_' + favori2.ligneId);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_3arret_2, pendingIntent2);
        Intent intent3 = new Intent(context, TransportsWidget.class);
        intent3.setAction("YboClick_" + favori3.arretId + '_' + favori3.ligneId);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, intent3,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_3arret_3, pendingIntent3);
        views.setViewVisibility(R.id.layout_1arret, View.INVISIBLE);
        views.setViewVisibility(R.id.layout_2arret, View.INVISIBLE);
        views.setViewVisibility(R.id.layout_3arret, View.VISIBLE);
    }

    private static void remplirRemoteViews1Arret(Context context, RemoteViews remoteViews, List<ArretFavori> favoris,
                                                 Calendar calendar) {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        try {
            List<DetailArretConteneur> prochainsDeparts = Horaire.getProchainHorairesAsList(favoris.get(0).ligneId,
                    favoris.get(0).arretId, 4, calendar, favoris.get(0).macroDirection);
            remoteViews.setTextViewText(
                    R.id.tempsRestant1_1arret,
                    prochainsDeparts.size() < 1 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts.get(0).getHoraire(), now));
            remoteViews.setTextViewText(
                    R.id.tempsRestant2_1arret,
                    prochainsDeparts.size() < 2 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts.get(1).getHoraire(), now));
            remoteViews.setTextViewText(
                    R.id.tempsRestant3_1arret,
                    prochainsDeparts.size() < 3 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts.get(2).getHoraire(), now));
            remoteViews.setTextViewText(
                    R.id.tempsRestant4_1arret,
                    prochainsDeparts.size() < 4 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts.get(3).getHoraire(), now));
        } catch (SQLiteException ignore) {

        }

    }

    private static void remplirRemoteViews2Arret(Context context, RemoteViews remoteViews, List<ArretFavori> favoris,
                                                 Calendar calendar) {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        try {
            List<DetailArretConteneur> prochainsDeparts1 = Horaire.getProchainHorairesAsList(favoris.get(0).ligneId,
                    favoris.get(0).arretId, 2, calendar, favoris.get(0).macroDirection);
            remoteViews.setTextViewText(
                    R.id.tempsRestant11_2arret,
                    prochainsDeparts1.size() < 1 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts1.get(0).getHoraire(), now));
            remoteViews.setTextViewText(
                    R.id.tempsRestant12_2arret,
                    prochainsDeparts1.size() < 2 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts1.get(1).getHoraire(), now));
        } catch (SQLiteException ignore) {

        }
        try {
            List<DetailArretConteneur> prochainsDeparts2 = Horaire.getProchainHorairesAsList(favoris.get(1).ligneId,
                    favoris.get(1).arretId, 2, calendar, favoris.get(1).macroDirection);
            remoteViews.setTextViewText(
                    R.id.tempsRestant21_2arret,
                    prochainsDeparts2.size() < 1 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts2.get(0).getHoraire(), now));
            remoteViews.setTextViewText(
                    R.id.tempsRestant22_2arret,
                    prochainsDeparts2.size() < 2 ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts2.get(1).getHoraire(), now));
        } catch (SQLiteException ignore) {

        }
    }

    private static void remplirRemoteViews3Arret(Context context, RemoteViews remoteViews, List<ArretFavori> favoris,
                                                 Calendar calendar) {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        try {
            List<DetailArretConteneur> prochainsDeparts1 = Horaire.getProchainHorairesAsList(favoris.get(0).ligneId,
                    favoris.get(0).arretId, 1, calendar, favoris.get(0).macroDirection);
            remoteViews.setTextViewText(
                    R.id.tempsRestant1_3arret,
                    prochainsDeparts1.isEmpty() ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts1.get(0).getHoraire(), now));
        } catch (SQLiteException ignore) {

        }

        try {
            List<DetailArretConteneur> prochainsDeparts2 = Horaire.getProchainHorairesAsList(favoris.get(1).ligneId,
                    favoris.get(1).arretId, 1, calendar, favoris.get(1).macroDirection);
            remoteViews.setTextViewText(
                    R.id.tempsRestant2_3arret,
                    prochainsDeparts2.isEmpty() ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts2.get(0).getHoraire(), now));
        } catch (SQLiteException ignore) {

        }
        try {
            List<DetailArretConteneur> prochainsDeparts3 = Horaire.getProchainHorairesAsList(favoris.get(2).ligneId,
                    favoris.get(2).arretId, 1, calendar, favoris.get(2).macroDirection);
            remoteViews.setTextViewText(
                    R.id.tempsRestant3_3arret,
                    prochainsDeparts3.isEmpty() ? "" : context.getString(R.string.dans) + ' '
							+ Formatteur.formatterCalendar(context, prochainsDeparts3.get(0).getHoraire(), now));
        } catch (SQLiteException ignore) {

        }
    }
}
