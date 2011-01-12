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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.adapters.FavoriAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;

import java.util.List;

public class TransportsWidgetConfigure extends ListActivity {

	private int appWidgetId;
	private List<ArretFavori> favoris;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);

		setContentView(R.layout.configurewidget);

		favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori());

		if (favoris.isEmpty()) {
			Toast.makeText(this, "Vous n'avez pas de favoris, pour utiliser le widget, il faut ajouter des favoris.", Toast.LENGTH_LONG).show();
			finish();
		}

		construireListe();

	}

	private void construireListe() throws DataBaseException {
		setListAdapter(new FavoriAdapter(getApplicationContext(),favoris));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				FavoriAdapter favoriAdapter = (FavoriAdapter) ((ListView) adapterView).getAdapter();
				ArretFavori favori = favoriAdapter.getItem(position);
				SharedPreferences sharedPreferences = TransportsWidgetConfigure.this.getSharedPreferences("prefs", 0);
				SharedPreferences.Editor edit = sharedPreferences.edit();
				edit.putString("favoriArretId" + appWidgetId, favori.arretId);
				edit.putString("favoriArretId" + appWidgetId, favori.ligneId);
				edit.commit();
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, resultValue);
				TransportsWidgetConfigure.this.finish();
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}
}
