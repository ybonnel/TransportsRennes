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
package fr.ybo.transportsbordeaux.activity.velos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.ads.Ad;
import com.google.ads.AdRequest;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.adapters.velos.VeloAdapter;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.tbcapi.Keolis;
import fr.ybo.transportsbordeaux.tbcapi.modele.Station;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Refreshable;
import fr.ybo.transportscommun.activity.commun.Searchable;
import fr.ybo.transportscommun.donnees.modele.VeloFavori;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.Formatteur;
import fr.ybo.transportscommun.util.LocationUtil;
import fr.ybo.transportscommun.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportscommun.util.TacheAvecProgressDialog;

/**
 * Activité de type liste permettant de lister les stations pas distances de la
 * position actuelle.
 * 
 * @author ybonnel
 */
public class ListStationsByPosition extends BaseListActivity implements UpdateLocationListenner, Searchable,
		Refreshable {

	private LocationUtil locationUtil;

	/**
	 * Liste des stations.
	 */
	private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>(100));
	private final List<Station> stationsFiltrees = Collections.synchronizedList(new ArrayList<Station>(100));

	@Override
	protected void onResume() {
		super.onResume();
		locationUtil.activeGps();
	}

	@Override
	protected void onPause() {
		locationUtil.desactiveGps();
		super.onPause();
	}

	private String currentQuery = "";

	@Override
	public void updateQuery(final String newQuery) {
		currentQuery = newQuery;
		final String query = newQuery.toUpperCase();
		stationsFiltrees.clear();
		synchronized (stations) {
			for (final Station station : stations) {
				if (station.name.toUpperCase().contains(query.toUpperCase())) {
					stationsFiltrees.add(station);
				}
			}
		}
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

	}

	private ListView listView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liststations);
		getActivityHelper().setupActionBar(R.menu.liststation_menu_items, R.menu.holo_liststation_menu_items);
		locationUtil = new LocationUtil(this, this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setListAdapter(new VeloAdapter(getApplicationContext(), stationsFiltrees));
		listView = getListView();
		listView.setFastScrollEnabled(true);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final VeloAdapter veloAdapter = (VeloAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				final Station station = veloAdapter.getItem(position);
				final String lat = Double.toString(station.getLatitude());
				final String lon = Double.toString(station.getLongitude());
				final Uri uri = Uri.parse("geo:" + lat + ',' + lon + "?q=" + lat + ',' + lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (final ActivityNotFoundException activityNotFound) {
					Toast.makeText(ListStationsByPosition.this, getString(R.string.noGoogleMap), Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);


		new GetStations() {
			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				updateLocation(locationUtil.getCurrentBestLocation());
			}
		}.execute();
		if (!locationUtil.activeGps()) {
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
		}

		// Look up the AdView as a resource and load a request.
		((Ad) findViewById(R.id.adView)).loadAd(new AdRequest());
	}

	private class GetStations extends TacheAvecProgressDialog<Void, Void, Void> {
		private GetStations() {
			super(ListStationsByPosition.this, getString(R.string.dialogRequeteVcub), true);
		}

		@Override
		protected void onPostExecute(final Void result) {
			super.onPostExecute(result);
			if (!isCancelled()) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fr.ybo.transportscommun.util.TacheAvecProgressDialog#myDoBackground()
		 */
		@Override
		protected void myDoBackground() throws ErreurReseau {
			final List<Station> stationsTmp = Keolis.getStationsVcub();
			if (isCancelled()) {
				return;
			}
			synchronized (stations) {
				stations.clear();
				stations.addAll(stationsTmp);
				Collections.sort(stations, new Station.StationComparator());
				stationsFiltrees.clear();
				stationsFiltrees.addAll(stations);
			}
		}
	}

	@Override
	public void refresh() {
		new GetStations() {
			@Override
			protected void onPostExecute(final Void result) {
				updateQuery(currentQuery);
				updateLocation(locationUtil.getCurrentBestLocation());
				super.onPostExecute(result);
			}
		}.execute();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		super.onOptionsItemSelected(item);
		return false;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			final Station station = (Station) getListAdapter().getItem(info.position);
			VeloFavori veloFavori = new VeloFavori();
			veloFavori.number = Integer.toString(station.id);
			veloFavori = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(veloFavori);
			menu.setHeaderTitle(Formatteur.formatterChaine(station.name));
			menu.add(Menu.NONE, veloFavori == null ? R.id.ajoutFavori : R.id.supprimerFavori, 0,
					veloFavori == null ? getString(R.string.ajouterFavori) : getString(R.string.suprimerFavori));
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final Station station;
		final VeloFavori veloFavori;
		switch (item.getItemId()) {
			case R.id.ajoutFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.number = Integer.toString(station.id);
				TransportsBordeauxApplication.getDataBaseHelper().insert(veloFavori);
				return true;
			case R.id.supprimerFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.number = Integer.toString(station.id);
				TransportsBordeauxApplication.getDataBaseHelper().delete(veloFavori);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void updateLocation(final Location location) {
		if (location == null) {
			return;
		}
		synchronized (stations) {
			for (final Station station : stations) {
				station.calculDistance(location);
			}
			Collections.sort(stations, new Station.ComparatorDistance());
		}
		updateQuery(currentQuery);
		((BaseAdapter) getListAdapter()).notifyDataSetChanged();
	}
}
