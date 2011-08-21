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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.FavoriAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.UpdateTimeUtil;
import fr.ybo.transportsrennes.util.UpdateTimeUtil.UpdateTime;

/**
 * @author ybonnel
 */
public class ListFavoris extends MenuAccueil.ListActivity {

	private void construireListe() {
		ArretFavori favoriExemple = new ArretFavori();
		if (groupe != null) {
			favoriExemple.groupe = groupe;
		}
		List<ArretFavori> favoris = TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori(), "ordre");

		setListAdapter(new FavoriAdapter(getApplicationContext(), favoris));
		ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent(ListFavoris.this, DetailArret.class);
				intent.putExtra("favori", (Serializable) adapterView.getAdapter().getItem(position));
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	private UpdateTimeUtil updateTimeUtil;

	private String groupe = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfavoris);
		if (getIntent().getExtras() != null) {
			groupe = getIntent().getExtras().getString("groupe");
		}
		construireListe();
		updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

			@Override
			public void update(Calendar calendar) {
				((FavoriAdapter) getListAdapter()).majCalendar();
				((FavoriAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}, this);
		updateTimeUtil.start();
	}

	@Override
	protected void onResume() {
		updateTimeUtil.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		updateTimeUtil.stop();
		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(favori.nomArret);
			menu.add(Menu.NONE, R.id.supprimerFavori, 0, getString(R.string.suprimerFavori));
		}
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);

				if (TransportsWidgetConfigure.isNotUsed(this, favori)
						&& TransportsWidget11Configure.isNotUsed(this, favori)
						&& TransportsWidget21Configure.isNotUsed(this, favori)) {
					TransportsRennesApplication.getDataBaseHelper().delete(favori);
					((FavoriAdapter) getListAdapter()).getFavoris().clear();
					((FavoriAdapter) getListAdapter()).getFavoris().addAll(TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori()));
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast.makeText(this, getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG).show();
				}
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
}
