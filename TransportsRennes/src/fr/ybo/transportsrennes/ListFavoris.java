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

package fr.ybo.transportsrennes;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.FavoriAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;

import java.util.concurrent.TimeUnit;

/**
 * @author ybonnel
 */
public class ListFavoris extends MenuAccueil.ListActivity {

	private void construireListe() throws DataBaseException {
		setListAdapter(new FavoriAdapter(getApplicationContext(),
				TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori(), null, null, "ordre")));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final FavoriAdapter favoriAdapter = (FavoriAdapter) ((ListView) adapterView).getAdapter();
				final Intent intent = new Intent(ListFavoris.this, DetailArret.class);
				intent.putExtra("favori", favoriAdapter.getItem(position));
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	private Runnable runnableMajToRunOnUiThread = new Runnable() {
		public void run() {
			((FavoriAdapter) ListFavoris.this.getListAdapter()).majCalendar();
			((FavoriAdapter) ListFavoris.this.getListAdapter()).getFavoris().clear();
			((FavoriAdapter) ListFavoris.this.getListAdapter()).getFavoris()
					.addAll(TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori(), null, null, "ordre"));
			((FavoriAdapter) ListFavoris.this.getListAdapter()).notifyDataSetChanged();
		}
	};

	private Runnable runnableMajHoraires = new Runnable() {
		public void run() {
			while (true) {
				try {
					TimeUnit.SECONDS.sleep(20);
					ListFavoris.this.runOnUiThread(runnableMajToRunOnUiThread);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	};

	private Thread threadCourant;

	@Override
	protected void onPause() {
		if (threadCourant != null) {
			threadCourant.interrupt();
			threadCourant = null;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (threadCourant == null) {
			runnableMajToRunOnUiThread.run();
			threadCourant = new Thread(runnableMajHoraires);
			threadCourant.start();
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfavoris);
		construireListe();
		if (threadCourant == null) {
			threadCourant = new Thread(runnableMajHoraires);
			threadCourant.start();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(favori.nomArret);
			menu.add(Menu.NONE, R.id.supprimerFavori, 0, "Supprimer des favoris");
		}
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);

				if (TransportsWidgetConfigure.isNotUsed(this, favori)) {
					TransportsRennesApplication.getDataBaseHelper().delete(favori);
					((FavoriAdapter) getListAdapter()).getFavoris().clear();
					((FavoriAdapter) getListAdapter()).getFavoris().addAll(TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori()));
					((FavoriAdapter) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast.makeText(this, "Un widget utilise ce favori, merci de le supprimer avant de supprimer ce favori.", Toast.LENGTH_LONG)
							.show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}