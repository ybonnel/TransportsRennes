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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
 * Activité de type liste permettant de lister les stations pas distances de la
 * position actuelle.
 * 
 * @author ybonnel
 */
public class ListStationsByPosition extends MenuAccueil.ListActivity implements LocationListener {

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des stations.
	 */
	private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>(100));
	private final List<Station> stationsFiltrees = Collections.synchronizedList(new ArrayList<Station>(100));

	private Location lastLocation;

	/**
	 * Permet de mettre à jour les distances des stations par rapport à une
	 * nouvelle position.
	 * 
	 * @param location
	 *            position courante.
	 */
	private void mettreAjoutLoc(Location location) {
		if (location != null && (lastLocation == null || location.getAccuracy() <= lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			synchronized (stations) {
				for (Station station : stations) {
					station.calculDistance(location);
				}
				Collections.sort(stations, new Station.ComparatorDistance());
			}
			metterAJourListeStations();
			((BaseAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	public void onLocationChanged(Location arg0) {
		mettreAjoutLoc(arg0);
	}

	public void onProviderDisabled(String arg0) {
		desactiveGps();
		activeGps();
	}

	public void onProviderEnabled(String arg0) {
		desactiveGps();
		activeGps();
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	/**
	 * Active le GPS.
	 */
	private void activeGps() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		mettreAjoutLoc(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		List<String> providers = locationManager.getProviders(criteria, true);
		boolean gpsTrouve = false;
		for (String providerName : providers) {
			locationManager.requestLocationUpdates(providerName, 10000L, 20L, this);
			if (providerName.equals(LocationManager.GPS_PROVIDER)) {
				gpsTrouve = true;
			}
		}
		if (!gpsTrouve) {
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
		}
	}

	private void desactiveGps() {
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		activeGps();
	}

	@Override
	protected void onPause() {
		desactiveGps();
		super.onPause();
	}

	private void metterAJourListeStations() {
		String query = editText.getText().toString().toUpperCase();
		stationsFiltrees.clear();
		synchronized (stations) {
			for (Station station : stations) {
				if (station.name.toUpperCase().contains(query.toUpperCase())) {
					stationsFiltrees.add(station);
				}
			}
		}
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}

	private EditText editText;
	private ListView listView;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liststations);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new VeloAdapter(getApplicationContext(), stationsFiltrees));
		listView = getListView();
		editText = (EditText) findViewById(R.id.liststations_input);
		editText.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void afterTextChanged(Editable editable) {
				metterAJourListeStations();
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				VeloAdapter veloAdapter = (VeloAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				Station station = veloAdapter.getItem(position);
				String lat = Double.toString(station.getLatitude());
				String lon = Double.toString(station.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(station.name) + "+@" + lat + ',' + lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException activityNotFound) {
					Toast.makeText(ListStationsByPosition.this, getString(R.string.noGoogleMap), Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);

		findViewById(R.id.enteteGoogleMap).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(ListStationsByPosition.this, StationsOnMap.class);
				ArrayList<Station> stationsSerializable = new ArrayList<Station>(stationsFiltrees.size());
				stationsSerializable.addAll(stationsFiltrees);
				intent.putExtra("stations", stationsSerializable);
				startActivity(intent);
			}
		});

		new GetStations() {
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				activeGps();
			}
		}.execute();

		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}

	private class GetStations extends TacheAvecProgressDialog<Void, Void, Void> {
		public GetStations() {
			super(ListStationsByPosition.this, getString(R.string.dialogRequeteVcub));
		}
		
		private boolean erreurReseaux = false;

		@Override
		protected Void doInBackground(Void... pParams) {
			try {
				List<Station> stationsTmp = Station.recupererStations();
				synchronized (stations) {
					stations.clear();
					stations.addAll(stationsTmp);
					Collections.sort(stations, new Comparator<Station>() {
						public int compare(Station o1, Station o2) {
							return o1.name.compareToIgnoreCase(o2.name);
						}
					});
					stationsFiltrees.clear();
					stationsFiltrees.addAll(stations);
				}
			} catch (TbcErreurReseaux exceptionReseaux) {
				erreurReseaux = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (erreurReseaux) {
				Toast.makeText(ListStationsByPosition.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
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

			new GetStations() {
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					metterAJourListeStations();
					mettreAjoutLoc(lastLocation);
				}
			}.execute();
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
			VeloFavori veloFavori = new VeloFavori();
			veloFavori.id = station.id;
			veloFavori = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(veloFavori);
			menu.setHeaderTitle(Formatteur.formatterChaine(station.name));
			menu.add(Menu.NONE, veloFavori == null ? R.id.ajoutFavori : R.id.supprimerFavori, 0,
					veloFavori == null ? getString(R.string.ajouterFavori) : getString(R.string.suprimerFavori));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Station station;
		VeloFavori veloFavori;
		switch (item.getItemId()) {
			case R.id.ajoutFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.id = station.id;
				TransportsBordeauxApplication.getDataBaseHelper().insert(veloFavori);
				return true;
			case R.id.supprimerFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.id = station.id;
				TransportsBordeauxApplication.getDataBaseHelper().delete(veloFavori);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
