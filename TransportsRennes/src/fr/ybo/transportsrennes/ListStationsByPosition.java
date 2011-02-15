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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.VeloAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.VeloFavori;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activité de type liste permettant de lister les stations pas distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListStationsByPosition extends MenuAccueil.ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListStationsByPosition.class);

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des stations.
	 */
	private List<Station> stationIntent;
	private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>(100));
	private final List<Station> stationsFiltrees = Collections.synchronizedList(new ArrayList<Station>(100));

	private Location lastLocation;

	/**
	 * Permet de mettre à jour les distances des stations par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(final Location location) {
		if (location != null && (lastLocation == null || location.getAccuracy() <= lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			synchronized (stations) {
				for (final Station station : stations) {
					station.calculDistance(location);
				}
				Collections.sort(stations, new Station.ComparatorDistance());
			}
			metterAJourListeStations();
			((BaseAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	public void onLocationChanged(final Location arg0) {
		mettreAjoutLoc(arg0);
	}

	public void onProviderDisabled(final String arg0) {
		desactiveGps();
		activeGps();
	}

	public void onProviderEnabled(final String arg0) {
		desactiveGps();
		activeGps();
	}

	public void onStatusChanged(final String arg0, final int arg1, final Bundle arg2) {
	}

	/**
	 * Active le GPS.
	 */
	private void activeGps() {
		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		mettreAjoutLoc(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		final List<String> providers = locationManager.getProviders(criteria, true);
		boolean gpsTrouve = false;
		for (final String providerName : providers) {
			locationManager.requestLocationUpdates(providerName, 10000L, 20L, this);
			if (providerName.equals(LocationManager.GPS_PROVIDER)) {
				gpsTrouve = true;
			}
		}
		if (!gpsTrouve) {
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
		}
	}

	protected void desactiveGps() {
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

	private ProgressDialog myProgressDialog;

	@SuppressWarnings("unchecked")
	private void metterAJourListeStations() {
		final String query = editText.getText().toString().toUpperCase();
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

	private EditText editText;
	private ListView listView;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liststations);
		stationIntent = (List<Station>) (getIntent().getExtras() == null ? null : getIntent().getExtras().getSerializable("stations"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new VeloAdapter(getApplicationContext(), stationsFiltrees));
		listView = getListView();
		editText = (EditText) findViewById(R.id.liststations_input);
		editText.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
			}

			public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
			}

			public void afterTextChanged(final Editable editable) {
				metterAJourListeStations();
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final VeloAdapter veloAdapter = (VeloAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				final Station station = veloAdapter.getItem(position);
				final String lat = Double.toString(station.getLatitude());
				final String lon = Double.toString(station.getLongitude());
				final Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(station.name) + "+@" + lat + "," + lon);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog.show(ListStationsByPosition.this, "", getString(R.string.dialogRequeteVeloStar), true);
			}

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					synchronized (stations) {
						stations.clear();
						stations.addAll(stationIntent == null ? keolis.getStations() : stationIntent);
						Collections.sort(stations, new Comparator<Station>() {
							public int compare(final Station o1, final Station o2) {
								return o1.name.compareToIgnoreCase(o2.name);
							}
						});
						stationsFiltrees.clear();
						stationsFiltrees.addAll(stations);
					}
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans ListStationsByPosition.doInBackGround", exception);
					erreur = true;
				}

				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void result) {
				myProgressDialog.dismiss();
				if (erreur) {
					final Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationVeloStar), Toast.LENGTH_LONG);
					toast.show();
					finish();
				} else {
					findViewById(R.id.enteteGoogleMap).setOnClickListener(new View.OnClickListener() {
						public void onClick(final View view) {
							final Intent intent = new Intent(ListStationsByPosition.this, StationsOnMap.class);
							final ArrayList<Station> stationsSerializable = new ArrayList<Station>();
							stationsSerializable.addAll(stationsFiltrees);
							intent.putExtra("stations", stationsSerializable);
							startActivity(intent);
						}
					});
					activeGps();
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}
		}.execute();
	}


	private static final int GROUP_ID = 0;
	private static final int MENU_REFRESH = Menu.FIRST;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		final MenuItem item = menu.add(GROUP_ID, MENU_REFRESH, Menu.NONE, R.string.menu_refresh);
		item.setIcon(R.drawable.ic_menu_refresh);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		super.onOptionsItemSelected(item);

		if (MENU_REFRESH == item.getItemId()) {
			new AsyncTask<Void, Void, Void>() {

				private boolean erreur;

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					myProgressDialog = ProgressDialog.show(ListStationsByPosition.this, "", getString(R.string.dialogRequeteVeloStar), true);
				}

				@Override
				protected Void doInBackground(final Void... pParams) {
					try {
						synchronized (stations) {
							stations.clear();
							if (stationIntent == null) {
								stations.addAll(keolis.getStations());
							} else {
								final Collection<String> ids = new ArrayList<String>(10);
								for (final Station station : stationIntent) {
									ids.add(station.number);
								}
								stations.addAll(keolis.getStationByNumbers(ids));
							}
							Collections.sort(stations, new Comparator<Station>() {
								public int compare(final Station o1, final Station o2) {
									return o1.name.compareToIgnoreCase(o2.name);
								}
							});
							stationsFiltrees.clear();
							stationsFiltrees.addAll(stations);
						}
					} catch (Exception exception) {
						LOG_YBO.erreur("Erreur dans ListStationsByPosition.doInBackGround", exception);
						erreur = true;
					}

					return null;
				}

				@Override
				@SuppressWarnings("unchecked")
				protected void onPostExecute(final Void result) {
					super.onPostExecute(result);
					myProgressDialog.dismiss();
					if (erreur) {
						final Toast toast =
								Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationVeloStar), Toast.LENGTH_LONG);
						toast.show();
						finish();
					} else {
						metterAJourListeStations();
						mettreAjoutLoc(lastLocation);
						((BaseAdapter) getListAdapter()).notifyDataSetChanged();
					}
				}
			}.execute();
			return true;
		}
		return false;
	}


	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			final Station station = (Station) getListAdapter().getItem(info.position);
			VeloFavori veloFavori = new VeloFavori();
			veloFavori.number = station.number;
			veloFavori = TransportsRennesApplication.getDataBaseHelper().selectSingle(veloFavori);
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
				veloFavori.number = station.number;
				TransportsRennesApplication.getDataBaseHelper().insert(veloFavori);
				return true;
			case R.id.supprimerFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.number = station.number;
				TransportsRennesApplication.getDataBaseHelper().delete(veloFavori);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
