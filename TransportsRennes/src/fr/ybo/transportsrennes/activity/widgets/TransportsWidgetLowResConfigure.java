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
import fr.ybo.transportsrennes.adapters.widget.FavoriAdapterForWidget1;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class TransportsWidgetLowResConfigure extends ListActivity {

    private int appWidgetId;
    private List<ArretFavori> favoris;
	private FavoriAdapterForWidget1 adapter;

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

        setContentView(R.layout.configurewidget11);

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
    	adapter = new FavoriAdapterForWidget1(getApplicationContext(), favoris);
        setListAdapter(adapter);
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                if (!checkBox.isChecked()) {
                    if (adapter.getFavoriSelectionne() == null) {
                    	adapter.setFavoriSelectionne(position);
                    	checkBox.setChecked(true);
                    } else {
                    	
                        Toast.makeText(TransportsWidgetLowResConfigure.this, getString(R.string.justOneFavori), Toast.LENGTH_SHORT).show();
                        
                    }
                } else {
                    adapter.setFavoriSelectionne(null);
                    checkBox.setChecked(false);
                }
            }
        });
        lv.setTextFilterEnabled(true);
        registerForContextMenu(lv);
        findViewById(R.id.terminerChoix).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FavoriAdapterForWidget1 favoriAdapter = (FavoriAdapterForWidget1) getListAdapter();
                ArretFavori favoriSelectionne = favoriAdapter.getFavoriSelectionne();
                if (favoriSelectionne == null) {
                    Toast.makeText(TransportsWidgetLowResConfigure.this, getString(R.string.erreur_auMoinsUnFavori),
                            Toast.LENGTH_SHORT).show();
                } else {
                    saveSettings(TransportsWidgetLowResConfigure.this, appWidgetId, favoriSelectionne);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(TransportsWidgetLowResConfigure.this);
					TransportsWidgetLowRes.updateAppWidget(TransportsWidgetLowResConfigure.this, appWidgetManager,
							appWidgetId);
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        });
    }

    private static void saveSettings(Context context, int appWidgetId, ArretFavori favori) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString("LowResArretId_" + appWidgetId, favori.arretId);
		edit.putString("LowResLigneId_" + appWidgetId, favori.ligneId);

        edit.commit();
    }

    public static boolean isNotUsed(Context context, ArretFavori favori) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<Integer, ArretFavori> favorisWidget = new HashMap<Integer, ArretFavori>();
        for (String key : sharedPreferences.getAll().keySet()) {
			if (key.startsWith("LowResArretId_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favorisWidget.containsKey(widgetId)) {
                    favorisWidget.put(widgetId, new ArretFavori());
                }
                favorisWidget.get(widgetId).arretId = sharedPreferences.getString(key, null);
            }
			if (key.startsWith("LowResLigneId_")) {
                int widgetId = Integer.parseInt(key.split("_")[1]);
                if (!favorisWidget.containsKey(widgetId)) {
                    favorisWidget.put(widgetId, new ArretFavori());
                }
                favorisWidget.get(widgetId).ligneId = sharedPreferences.getString(key, null);
            }
        }
        for (ArretFavori favoriWidget : favorisWidget.values()) {
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
			if (key.startsWith("LowResArretId_")) {
                widgetIds.add(Integer.parseInt(key.split("_")[1]));
            }
        }
        return widgetIds;
    }

    static ArretFavori loadSettings(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArretFavori favori = new ArretFavori();
		favori.arretId = sharedPreferences.getString("LowResArretId_" + appWidgetId, null);
		favori.ligneId = sharedPreferences.getString("LowResLigneId_" + appWidgetId, null);
        if (favori.arretId == null || favori.ligneId == null) {
            return null;
        }
        return favori;
    }

    static void deleteSettings(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.remove("LowResArretId_" + appWidgetId);
		edit.remove("LowResLigneId_" + appWidgetId);

        edit.commit();
    }

    static void deleteAllSettings(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> allPrefs = sharedPreferences.getAll();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        for (String key : allPrefs.keySet()) {
			if (key.startsWith("LowResArretId") || key.startsWith("LowResLigneId")) {
                edit.remove(key);
            }
        }
        edit.commit();

    }
}
