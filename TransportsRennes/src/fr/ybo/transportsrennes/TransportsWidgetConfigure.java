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

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.adapters.FavoriAdapterForWidget;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportsWidgetConfigure extends ListActivity {

	private final static LogYbo LOG_YBO = new LogYbo(TransportsWidgetConfigure.class);

	private int appWidgetId;
	private List<ArretFavori> favoris;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG_YBO.debug("onCreate");
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		LOG_YBO.debug("appWidgetId : " + appWidgetId);
		// If they gave us an intent without the widget id, just bail.
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			LOG_YBO.debug("finish");
			finish();
		}

		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);

		setContentView(R.layout.configurewidget);

		if (TransportsRennesApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour()) == null) {
			Toast.makeText(this, "Vous n'avez pas encore lancé l'application, veuillez la lancer avant d'essayer d'ajouter des widgets.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori());
		if (favoris.isEmpty()) {
			Toast.makeText(this, "Vous n'avez pas encore de favoris, pour utiliser les widgets, il faut ajouter des favoris.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		construireListe();
	}

	private void construireListe() throws DataBaseException {
		setListAdapter(new FavoriAdapterForWidget(getApplicationContext(), favoris));
		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
		findViewById(R.id.terminerChoix).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				FavoriAdapterForWidget favoriAdapter = (FavoriAdapterForWidget) getListAdapter();
				List<ArretFavori> favorisSelectionnes = favoriAdapter.getFavorisSelectionnes();
				if (favorisSelectionnes.isEmpty()) {
					Toast.makeText(TransportsWidgetConfigure.this, "Sélectionnez au moins un arrêt favori.", Toast.LENGTH_SHORT);
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

	protected static void saveSettings(Context context, int appWidgetId, List<ArretFavori> favoris) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = sharedPreferences.edit();
		int count = 1;
		for (ArretFavori favori : favoris) {
			edit.putString("ArretId" + count + "_" + appWidgetId, favori.arretId);
			edit.putString("LigneId" + count + "_" + appWidgetId, favori.ligneId);
			count++;
		}
		edit.commit();
	}

	public static boolean isUsed(Context context, ArretFavori favori) {
		Map<Integer, ArretFavori> favori1 = new HashMap<Integer, ArretFavori>();
		Map<Integer, ArretFavori> favori2 = new HashMap<Integer, ArretFavori>();
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
		}
		for (ArretFavori favoriWidget : favori1.values()) {
			if (favori.arretId.equals(favoriWidget.arretId)
					&& favori.ligneId.equals(favoriWidget.ligneId)) {
				return true;
			}
		}
		return false;
	}

	protected static List<Integer> getWidgetIds(Context context) {
		List<Integer> widgetIds = new ArrayList<Integer>();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		for (String key : sharedPreferences.getAll().keySet()) {
			if (key.startsWith("ArretId1_")) {
				widgetIds.add(Integer.parseInt(key.split("_")[1]));
			}
		}
		return widgetIds;
	}

	protected static List<ArretFavori> loadSettings(Context context, int appWidgetId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		ArretFavori favori;
		List<ArretFavori> favoris = new ArrayList<ArretFavori>();
		int count = 1;
		while (true) {
			favori = new ArretFavori();
			favori.arretId = sharedPreferences.getString("ArretId" + count + "_" + appWidgetId, null);
			favori.ligneId = sharedPreferences.getString("LigneId" + count + "_" + appWidgetId, null);
			if (favori.arretId == null || favori.ligneId == null) {
				break;
			}
			favoris.add(favori);
			count++;
		}
		return favoris;
	}

	protected static void deleteSettings(Context context, int appWidgetId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = sharedPreferences.edit();
		int count = 1;
		while (true) {
			if (sharedPreferences.getString("ArretId" + count + "_" + appWidgetId, null) == null) {
				break;
			}
			edit.remove("ArretId" + count + "_" + appWidgetId);
			edit.remove("LigneId" + count + "_" + appWidgetId);
			count++;
		}
		edit.commit();
	}

	protected static void deleteAllSettings(Context context) {
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
