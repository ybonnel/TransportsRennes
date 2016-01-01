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
package fr.ybo.transportsbordeaux.activity.widgets;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.bus.DetailArret;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.services.UpdateTimeService;
import fr.ybo.transportsbordeaux.util.Widget11UpdateUtil;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.util.LogYbo;

public class TransportsWidget11 extends AppWidgetProvider {

    private static final LogYbo LOG_YBO = new LogYbo(TransportsWidget11.class);

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        LOG_YBO.debug("onUpdate");
        context.startService(new Intent(context.getApplicationContext(), UpdateTimeService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(final Context context, final int[] appWidgetIds) {
        for (final int appWidgetId : appWidgetIds) {
            TransportsWidget11Configure.deleteSettings(context, appWidgetId);
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
        TransportsWidget11Configure.deleteAllSettings(context);
        super.onDisabled(context);
    }

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        LOG_YBO.debug("UpdateAppWidget : " + appWidgetId);
        final ArretFavori favoriSelect = TransportsWidget11Configure.loadSettings(context, appWidgetId);
        if (favoriSelect == null) {
            LOG_YBO.debug("Pas de favoris trouvés dans la conf.");
            return;
        }
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_arrets11);
        if (TransportsBordeauxApplication.getDataBaseHelper() == null) {
            return;
        }
        final ArretFavori favoriBdd = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(favoriSelect);
        if (favoriBdd == null) {
            LOG_YBO.debug("FavoriBdd null");
            return;
        }
        if (favoriBdd.nomArret.length() > 10) {
            favoriBdd.nomArret = favoriBdd.nomArret.substring(0, 8) + "...";
        }
        if (favoriBdd.direction.length() > 12) {
            favoriBdd.direction = favoriBdd.direction.substring(0, 10) + "...";
        }
        Widget11UpdateUtil.updateAppWidget(context, views, favoriBdd, Calendar.getInstance());
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // v1.5 fix that doesn't call onDelete Action
        final String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
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
                favori = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(favori);
                if (favori != null) {
                    final Intent startIntent = new Intent(context, DetailArret.class).putExtra("favori", favori).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    context.startActivity(startIntent);
                }
            }
        }
        super.onReceive(context, intent);
    }
}
