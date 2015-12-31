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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.Toast;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.adapters.widget.FavoriAdapterForWidget1;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;

public class TransportsWidget11Configure extends ListActivity {

    private int appWidgetId;
    private List<ArretFavori> favoris;
    private FavoriAdapterForWidget1 adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent launchIntent = getIntent();
        final Bundle extras = launchIntent.getExtras();
        appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        // If they gave us an intent without the widget id, just bail.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        final Intent cancelResultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, cancelResultValue);

        setContentView(R.layout.configurewidget11);

        if (TransportsBordeauxApplication.getDataBaseHelper() == null
                || TransportsBordeauxApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour()) == null) {
            Toast.makeText(this, R.string.erreur_widgetBeforeLaunch, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        favoris = TransportsBordeauxApplication.getDataBaseHelper().select(new ArretFavori());
        if (favoris.isEmpty()) {
            Toast.makeText(this, R.string.erreur_widgetWithNoFavori, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        construireListe();
    }

    private void construireListe() {
        adapter = new FavoriAdapterForWidget1(getApplicationContext(), favoris);
        setListAdapter(adapter);
        final ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

                final Checkable checkBox = (Checkable) view.findViewById(R.id.checkbox);
                if (checkBox.isChecked()) {
                    adapter.setFavoriSelectionne(null);
                    checkBox.setChecked(false);
                } else {
                    if (adapter.getFavoriSelectionne() == null) {
                        adapter.setFavoriSelectionne(position);
                        checkBox.setChecked(true);
                    } else {

                        Toast.makeText(TransportsWidget11Configure.this, R.string.justOneFavori, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        lv.setTextFilterEnabled(true);
        registerForContextMenu(lv);
        findViewById(R.id.terminerChoix).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final FavoriAdapterForWidget1 favoriAdapter = (FavoriAdapterForWidget1) getListAdapter();
                final ArretFavori favoriSelectionne = favoriAdapter.getFavoriSelectionne();
                if (favoriSelectionne == null) {
                    Toast.makeText(TransportsWidget11Configure.this, R.string.erreur_auMoinsUnFavori, Toast.LENGTH_SHORT).show();
                } else {
                    saveSettings(TransportsWidget11Configure.this, appWidgetId, favoriSelectionne);
                    final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(TransportsWidget11Configure.this);
                    TransportsWidget11.updateAppWidget(TransportsWidget11Configure.this, appWidgetManager, appWidgetId);
                    final Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        });
    }

    private static void saveSettings(final Context context, final int appWidgetId, final ArretFavori favori) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("11ArretId_" + appWidgetId, favori.arretId).putString("11LigneId_" + appWidgetId, favori.ligneId).commit();
    }

    public static boolean isNotUsed(final Context context, final ArretFavori favori) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Map<Integer, ArretFavori> favorisWidget = new HashMap<Integer, ArretFavori>();
        for (final String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("11ArretId_")) {
                final int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favorisWidget.containsKey(widgetId)) {
                    favorisWidget.put(widgetId, new ArretFavori());
                }
                favorisWidget.get(widgetId).arretId = sharedPreferences.getString(key, null);
            }
            if (key.startsWith("11LigneId_")) {
                final int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favorisWidget.containsKey(widgetId)) {
                    favorisWidget.put(widgetId, new ArretFavori());
                }
                favorisWidget.get(widgetId).ligneId = sharedPreferences.getString(key, null);
            }
        }
        for (final ArretFavori favoriWidget : favorisWidget.values()) {
            if (favori.arretId.equals(favoriWidget.arretId) && favori.ligneId.equals(favoriWidget.ligneId)) {
                return false;
            }
        }
        return true;
    }

    public static Iterable<Integer> getWidgetIds(final Context context) {
        final Collection<Integer> widgetIds = new ArrayList<Integer>(4);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (final String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("11ArretId_")) {
                widgetIds.add(Integer.parseInt(key.split("_")[1]));
            }
        }
        return widgetIds;
    }

    static ArretFavori loadSettings(final Context context, final int appWidgetId) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final ArretFavori favori = new ArretFavori();
        favori.arretId = sharedPreferences.getString("11ArretId_" + appWidgetId, null);
        favori.ligneId = sharedPreferences.getString("11LigneId_" + appWidgetId, null);
        if (favori.arretId == null || favori.ligneId == null) {
            return null;
        }
        return favori;
    }

    static void deleteSettings(final Context context, final int appWidgetId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove("11ArretId_" + appWidgetId).remove("11LigneId_" + appWidgetId).commit();
    }

    static void deleteAllSettings(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Map<String, ?> allPrefs = sharedPreferences.getAll();
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        for (final String key : allPrefs.keySet()) {
            if (key.startsWith("11ArretId") || key.startsWith("11LigneId")) {
                edit.remove(key);
            }
        }
        edit.commit();

    }
}
