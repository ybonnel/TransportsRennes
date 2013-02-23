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

import java.util.Calendar;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.widget.RemoteViews;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget;

public class Widget11UpdateUtil {

    private static final LogYbo LOG_YBO = new LogYbo(Widget11UpdateUtil.class);

    private Widget11UpdateUtil() {
    }

    public static void updateAppWidget(Context context, RemoteViews views, ArretFavori favori, Calendar calendar) {
        views.setTextViewText(R.id.nomArret, favori.nomArret);
        views.setTextViewText(R.id.directionArret, "-> " + favori.direction);
        views.setImageViewResource(R.id.iconeLigne, IconeLigne.getIconeResource(favori.nomCourt));
        Intent intent = new Intent(context, TransportsWidget.class);
        intent.setAction("YboClick_" + favori.arretId + '_' + favori.ligneId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);

        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, -3);
        try {
            List<DetailArretConteneur> prochainsDeparts = Horaire.getProchainHorairesAsList(favori.ligneId, favori.arretId,
                    2, calendar, favori.macroDirection);
            LOG_YBO.debug("Prochains departs : " + prochainsDeparts);
            if (prochainsDeparts.size() > 0) {
                int heureProchain = prochainsDeparts.get(0).getHoraire();
                if (heureProchain >= 24 * 60) {
                    heureProchain -= 24 * 60;
                }

                LOG_YBO.debug("heureProchain : " + heureProchain);
                LOG_YBO.debug("now : " + now);
                if ((now - heureProchain) >= 0 && (now - heureProchain) < 30) {
                    views.setTextColor(R.id.tempsRestant, context.getResources().getColor(R.color.red));
                } else {
                    views.setTextColor(R.id.tempsRestant, context.getResources().getColor(R.color.blanc));
                }
                views.setTextViewText(R.id.tempsRestant, formatterCalendar(prochainsDeparts.get(0).getHoraire()));

            } else {
                views.setTextViewText(R.id.tempsRestant, "");
            }
            views.setTextViewText(
                    R.id.tempsRestantFutur,
                    prochainsDeparts.size() < 2 ? "" : formatterCalendar(prochainsDeparts.get(1).getHoraire()));
        } catch (SQLiteException ignore) {

        }
    }

    private static String formatterCalendar(int prochainDepart) {
        StringBuilder stringBuilder = new StringBuilder();

        int heures = prochainDepart / 60;
        int minutes = prochainDepart - heures * 60;
        if (heures >= 24) {
            heures -= 24;
        }
        if (heures < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(heures);
        stringBuilder.append(':');
        if (minutes < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(minutes);

        return stringBuilder.toString();
    }
}
