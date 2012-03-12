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
package fr.ybo.transportsrennes.activity.widgets;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.bus.DetailArret;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.services.UpdateTimeService;
import fr.ybo.transportsrennes.util.WidgetLowResUpdateUtil;

public class TransportsWidgetLowRes extends AppWidgetProvider {

    private static final LogYbo LOG_YBO = new LogYbo(TransportsWidgetLowRes.class);

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LOG_YBO.debug("onUpdate");
        context.startService(new Intent(UpdateTimeService.ACTION_UPDATE));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
			TransportsWidgetLowResConfigure.deleteSettings(context, appWidgetId);
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
		TransportsWidgetLowResConfigure.deleteAllSettings(context);
        super.onDisabled(context);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        LOG_YBO.debug("UpdateAppWidget : " + appWidgetId);
		ArretFavori favoriSelect = TransportsWidgetLowResConfigure.loadSettings(context, appWidgetId);
        if (favoriSelect == null) {
            LOG_YBO.debug("Pas de favoris trouvés dans la conf.");
            return;
        }
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_arrets_low_res);
        if (TransportsRennesApplication.getDataBaseHelper() == null) {
			LOG_YBO.debug("DatabaseHelper null");
            return;
        }
        ArretFavori favoriBdd = TransportsRennesApplication.getDataBaseHelper().selectSingle(favoriSelect);
        if (favoriBdd == null) {
            LOG_YBO.debug("FavoriBdd null");
            return;
        }
		if (favoriBdd.nomArret.length() > 13) {
			favoriBdd.nomArret = favoriBdd.nomArret.substring(0, 11) + "...";
        }
		if (favoriBdd.direction.length() > 16) {
			favoriBdd.direction = favoriBdd.direction.substring(0, 14) + "...";
        }
		WidgetLowResUpdateUtil.updateAppWidget(context, views, favoriBdd, Calendar.getInstance());
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // v1.5 fix that doesn't call onDelete Action
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                super.onReceive(context, intent);
            } else {
                onDeleted(context, new int[]{appWidgetId});
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
