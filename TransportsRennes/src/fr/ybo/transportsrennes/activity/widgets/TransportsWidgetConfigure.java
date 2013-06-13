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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.widget.FavoriAdapterForWidget;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class TransportsWidgetConfigure extends ListActivity {

    private int appWidgetId;
    private List<ArretFavori> favoris;
	private FavoriAdapterForWidget adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		AbstractTransportsApplication.majTheme(this);
        super.onCreate(savedInstanceState);
        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        // If they gave us an intent without the widget id, just bail.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        Intent cancelResultValue = new Intent();
        cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, cancelResultValue);

        setContentView(R.layout.configurewidget);

        if (TransportsRennesApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour()) == null) {
            Toast.makeText(this, getString(R.string.erreur_widgetBeforeLaunch), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori());
        if (favoris.isEmpty()) {
            Toast.makeText(this, getString(R.string.erreur_widgetWithNoFavori), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        construireListe();
    }

    private void construireListe() {
    	adapter = new FavoriAdapterForWidget(getApplicationContext(), favoris);
        setListAdapter(adapter);
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                if (!checkBox.isChecked()) {
                	if (adapter.getFavorisSelectionnes().size() < 3) {
                		adapter.addFavoriSelectionne(position);
                		checkBox.setChecked(true);
                	} else {
                		Toast.makeText(TransportsWidgetConfigure.this, getString(R.string.tooMuchFavoris), Toast.LENGTH_SHORT).show();
                	}
                } else {
                    adapter.removeFavoriSelectionne(position);
                    checkBox.setChecked(false);
                }
            }
        });
        lv.setTextFilterEnabled(true);
        registerForContextMenu(lv);
        findViewById(R.id.terminerChoix).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FavoriAdapterForWidget favoriAdapter = (FavoriAdapterForWidget) getListAdapter();
                List<ArretFavori> favorisSelectionnes = favoriAdapter.getFavorisSelectionnes();
                if (favorisSelectionnes.isEmpty()) {
                    Toast.makeText(TransportsWidgetConfigure.this, getString(R.string.erreur_auMoinsUnFavori), Toast.LENGTH_SHORT).show();
                } else {
                    saveSettings(TransportsWidgetConfigure.this, appWidgetId, favorisSelectionnes);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(TransportsWidgetConfigure.this);
                    TransportsWidget.updateAppWidget(TransportsWidgetConfigure.this, appWidgetManager, appWidgetId);
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        });
    }

    private static void saveSettings(Context context, int appWidgetId, Iterable<ArretFavori> favoris) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        int count = 1;
        for (ArretFavori favori : favoris) {
            edit.putString("ArretId" + count + '_' + appWidgetId, favori.arretId);
            edit.putString("LigneId" + count + '_' + appWidgetId, favori.ligneId);
            count++;
        }
        edit.commit();
    }

    public static boolean isNotUsed(Context context, ArretFavori favori) {
        Map<Integer, ArretFavori> favori1 = new HashMap<Integer, ArretFavori>();
        Map<Integer, ArretFavori> favori2 = new HashMap<Integer, ArretFavori>();
        Map<Integer, ArretFavori> favori3 = new HashMap<Integer, ArretFavori>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("ArretId1_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favori1.containsKey(widgetId)) {
                    favori1.put(widgetId, new ArretFavori());
                }
                favori1.get(widgetId).arretId = sharedPreferences.getString(key, null);
            }
            if (key.startsWith("LigneId1_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favori1.containsKey(widgetId)) {
                    favori1.put(widgetId, new ArretFavori());
                }
                favori1.get(widgetId).ligneId = sharedPreferences.getString(key, null);
            }
            if (key.startsWith("ArretId2_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favori2.containsKey(widgetId)) {
                    favori2.put(widgetId, new ArretFavori());
                }
                favori2.get(widgetId).arretId = sharedPreferences.getString(key, null);
            }
            if (key.startsWith("LigneId2_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favori2.containsKey(widgetId)) {
                    favori2.put(widgetId, new ArretFavori());
                }
                favori2.get(widgetId).ligneId = sharedPreferences.getString(key, null);
            }
            if (key.startsWith("ArretId3_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favori3.containsKey(widgetId)) {
                    favori3.put(widgetId, new ArretFavori());
                }
                favori3.get(widgetId).arretId = sharedPreferences.getString(key, null);
            }
            if (key.startsWith("LigneId3_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favori3.containsKey(widgetId)) {
                    favori3.put(widgetId, new ArretFavori());
                }
                favori3.get(widgetId).ligneId = sharedPreferences.getString(key, null);
            }
        }
        for (ArretFavori favoriWidget : favori1.values()) {
            if (favori.arretId.equals(favoriWidget.arretId) && favori.ligneId.equals(favoriWidget.ligneId)) {
                return false;
            }
        }
        for (ArretFavori favoriWidget : favori2.values()) {
            if (favori.arretId.equals(favoriWidget.arretId) && favori.ligneId.equals(favoriWidget.ligneId)) {
                return false;
            }
        }
        for (ArretFavori favoriWidget : favori3.values()) {
            if (favori.arretId.equals(favoriWidget.arretId) && favori.ligneId.equals(favoriWidget.ligneId)) {
                return false;
            }
        }
        return true;
    }

    public static Iterable<Integer> getWidgetIds(Context context) {
        Collection<Integer> widgetIds = new ArrayList<Integer>(4);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("ArretId1_")) {
                widgetIds.add(Integer.parseInt(key.split("_")[1]));
            }
        }
        return widgetIds;
    }

    static List<ArretFavori> loadSettings(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<ArretFavori> favoris = new ArrayList<ArretFavori>(3);
        int count = 1;
        while (true) {
            ArretFavori favori = new ArretFavori();
            favori.arretId = sharedPreferences.getString("ArretId" + count + '_' + appWidgetId, null);
            favori.ligneId = sharedPreferences.getString("LigneId" + count + '_' + appWidgetId, null);
            if (favori.arretId == null || favori.ligneId == null) {
                break;
            }
            favoris.add(favori);
            count++;
        }
        return favoris;
    }

    static void deleteSettings(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        int count = 1;
        while (sharedPreferences.getString("ArretId" + count + '_' + appWidgetId, null) != null) {
            edit.remove("ArretId" + count + '_' + appWidgetId);
            edit.remove("LigneId" + count + '_' + appWidgetId);
            count++;
        }
        edit.commit();
    }

    static void deleteAllSettings(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> allPrefs = sharedPreferences.getAll();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        for (String key : allPrefs.keySet()) {
            if (key.startsWith("ArretId") || key.startsWith("LigneId")) {
                edit.remove(key);
            }
        }
        edit.commit();

    }
}
