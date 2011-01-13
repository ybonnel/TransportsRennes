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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.adapters.FavoriAdapterForWidget;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.List;

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
			Toast.makeText(this, "Vous n'avez pas encore lancer l'application, veuillez la lancer avant d'essayer d'ajouter des widgets.", Toast.LENGTH_LONG).show();
			finish();
		}
		favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori());
		if (favoris.isEmpty()) {
			Toast.makeText(this, "Vous n'avez pas de favoris, pour utiliser le widget, il faut ajouter des favoris.", Toast.LENGTH_LONG).show();
			finish();
		}

		construireListe();
	}

	private void construireListe() throws DataBaseException {
		setListAdapter(new FavoriAdapterForWidget(getApplicationContext(), favoris));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				FavoriAdapterForWidget favoriAdapter = (FavoriAdapterForWidget) ((ListView) adapterView).getAdapter();
				ArretFavori favori = favoriAdapter.getItem(position);
				saveSettings(TransportsWidgetConfigure.this, appWidgetId, favori);
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(TransportsWidgetConfigure.this);
				TransportsWidget.updateAppWidget(TransportsWidgetConfigure.this, appWidgetManager, appWidgetId);
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	protected static void saveSettings(Context context, int appWidgetId, ArretFavori favori) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("fr.ybo.transportsrennes", 0);
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString("ArretId_" + appWidgetId, favori.arretId);
		edit.putString("LigneId_" + appWidgetId, favori.ligneId);
		edit.commit();
	}

	protected static ArretFavori loadSettings(Context context, int appWidgetId) {
		ArretFavori favori = new ArretFavori();
		SharedPreferences sharedPreferences = context.getSharedPreferences("fr.ybo.transportsrennes", 0);
		favori.arretId = sharedPreferences.getString("ArretId_" + appWidgetId, null);
		favori.ligneId = sharedPreferences.getString("LigneId_" + appWidgetId, null);
		if (favori.arretId == null || favori.ligneId == null) {
			return null;
		}
		return favori;
	}

	protected static void deleteSettings(Context context, int appWidgetId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("fr.ybo.transportsrennes", 0);
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.remove("ArretId_" + appWidgetId);
		edit.remove("LigneId_" + appWidgetId);
		edit.commit();
	}
}
