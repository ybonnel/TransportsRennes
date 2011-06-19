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
package fr.ybo.transportsbordeaux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.activity.TacheAvecProgressDialog;
import fr.ybo.transportsbordeaux.adapters.VeloAdapter;
import fr.ybo.transportsbordeaux.modele.VeloFavori;
import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.util.Formatteur;
import fr.ybo.transportsbordeaux.vcub.Station;

/**
 * Activité de type liste permettant de lister les stations de velos favorites.
 * 
 * @author ybonnel
 */
public class ListStationsFavoris extends MenuAccueil.ListActivity {

	/**
	 * Liste des stations.
	 */
	private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listvelofavoris);
		setListAdapter(new VeloAdapter(getApplicationContext(), stations));
		ListView listView = getListView();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings({ "unchecked" })
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				VeloAdapter veloAdapter = (VeloAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				Station station = veloAdapter.getItem(position);
				String lat = Double.toString(station.getLatitude());
				String lon = Double.toString(station.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(station.name) + "+@" + lat + ',' + lon);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		new GetStations().execute();

		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}

	private class GetStations extends TacheAvecProgressDialog<Void, Void, Void> {
		public GetStations() {
			super(ListStationsFavoris.this,
					getString(R.string.dialogRequeteVcub));
		}

		private boolean erreurReseaux = false;

		@Override
		protected Void doInBackground(Void... pParams) {
			try {
				List<VeloFavori> velosFavoris = TransportsBordeauxApplication
						.getDataBaseHelper().select(new VeloFavori());
				Collection<Integer> ids = new ArrayList<Integer>(10);
				for (VeloFavori favori : velosFavoris) {
					ids.add(favori.id);
				}
				Collection<Station> stationsTmp = Station.recupererStations();
				synchronized (stations) {
					stations.clear();
					for (Station station : stationsTmp) {
						if (ids.contains(station.id)) {
							stations.add(station);
						}
					}
					Collections.sort(stations, new Comparator<Station>() {
						public int compare(Station o1, Station o2) {
							return o1.name.compareToIgnoreCase(o2.name);
						}
					});
				}
			} catch (TbcErreurReseaux exceptionReseau) {
				erreurReseaux = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (erreurReseaux) {
				Toast.makeText(ListStationsFavoris.this,
						getString(R.string.erreurReseau), Toast.LENGTH_LONG)
						.show();
			}
			((BaseAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_REFRESH = Menu.FIRST;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(GROUP_ID, MENU_REFRESH, Menu.NONE, R.string.menu_refresh);
		item.setIcon(R.drawable.ic_menu_refresh);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == MENU_REFRESH) {
			new GetStations().execute();
			return true;
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Station station = (Station) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(Formatteur.formatterChaine(station.name));
			menu.add(Menu.NONE, R.id.supprimerFavori, 0, getString(R.string.suprimerFavori));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Station station;
		VeloFavori veloFavori;
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.id = station.id;
				TransportsBordeauxApplication.getDataBaseHelper().delete(veloFavori);
				((VeloAdapter) getListAdapter()).getStations().remove(station);
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
