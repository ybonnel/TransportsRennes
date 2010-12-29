package fr.ybo.transportsrennes;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.*;
import fr.ybo.transportsrennes.adapters.VeloAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.VeloFavori;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activité de type liste permettant de lister les stations pas distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListStationsByPosition extends ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListStationsByPosition.class);

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private Keolis keolis = Keolis.getInstance();

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des stations.
	 */
	private List<Station> stations = new ArrayList<Station>();
	private List<Station> stationsFiltrees = new ArrayList<Station>();

	private Location lastLocation = null;

	private String printLocaltion(Location location) {
		if (location == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Localtion[provider : ");
		stringBuilder.append(location.getProvider());
		stringBuilder.append(", accuracy : ");
		stringBuilder.append(location.getAccuracy());
		stringBuilder.append(", longitude : ");
		stringBuilder.append(location.getLongitude());
		stringBuilder.append(", latitude : ");
		stringBuilder.append(location.getLatitude());
		stringBuilder.append(']');
		return stringBuilder.toString();
	}

	/**
	 * Permet de mettre à jour les distances des stations par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(Location location) {
		LOG_YBO.debug("Nouvelle location : " + printLocaltion(location));
		if (lastLocation == null || location.getAccuracy() <= (lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			for (Station station : stations) {
				station.calculDistance(location);
			}
			Collections.sort(stations, new Station.ComparatorDistance());
			Collections.sort(stationsFiltrees, new Station.ComparatorDistance());
			((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
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
			locationManager.requestLocationUpdates(providerName, 10000l, 20l, this);
			if (providerName.equals(LocationManager.GPS_PROVIDER)) {
				gpsTrouve = true;
			}
		}
		if (!gpsTrouve) {
			Toast.makeText(getApplicationContext(), "Pour mieux profiter de cette page, il est préférable d'allumer son GPS.", Toast.LENGTH_SHORT)
					.show();
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
		String query = editText.getText().toString().toUpperCase();
		stationsFiltrees.clear();
		for (Station station : stations) {
			if (station.getName().toUpperCase().contains(query.toUpperCase())) {
				stationsFiltrees.add(station);
			}
		}
		((ArrayAdapter<Station>) listView.getAdapter()).notifyDataSetChanged();
	}

	private EditText editText;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liststations);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new VeloAdapter(getApplicationContext(), R.layout.dispovelo, stationsFiltrees));
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
				VeloAdapter veloAdapter = (VeloAdapter) ((ListView) adapterView).getAdapter();
				Station station = veloAdapter.getItem(position);
				String _lat = Double.toString(station.getLatitude());
				String _lon = Double.toString(station.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(station.getName()) + "+@" + _lat + "," + _lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException noGoogleMapsException) {
					LOG_YBO.erreur("Google maps de doit pas être présent", noGoogleMapsException);
					Toast.makeText(getApplicationContext(), "Vous n'avez pas GoogleMaps d'installé...", Toast.LENGTH_LONG).show();
				}
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequeteVeloStar), true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					stations = keolis.getStations();
					Collections.sort(stations, new Comparator<Station>() {
						public int compare(Station o1, Station o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
					stationsFiltrees.clear();
					stationsFiltrees.addAll(stations);
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans ListStationsByPosition.doInBackGround", exception);
					erreur = true;
				}

				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				myProgressDialog.dismiss();
				if (!erreur) {
					activeGps();
					((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(), "Une erreur est survenu lors de l'interrogation de VeloStar...",
							Toast.LENGTH_LONG);
					toast.show();
				}
			}
		}.execute();
	}


	private static final int GROUP_ID = 0;
	private static final int MENU_REFRESH = Menu.FIRST;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(GROUP_ID, MENU_REFRESH, Menu.NONE, R.string.menu_refresh);
		item.setIcon(android.R.drawable.ic_menu_rotate);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case MENU_REFRESH:
				myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequeteVeloStar), true);
				new AsyncTask<Void, Void, Void>() {

					private boolean erreur = false;

					@Override
					protected Void doInBackground(final Void... pParams) {
						try {
							stations = keolis.getStations();
							Collections.sort(stations, new Comparator<Station>() {
								public int compare(Station o1, Station o2) {
									return o1.getName().compareToIgnoreCase(o2.getName());
								}
							});
							stationsFiltrees.clear();
							stationsFiltrees.addAll(stations);
						} catch (Exception exception) {
							LOG_YBO.erreur("Erreur dans ListStationsByPosition.doInBackGround", exception);
							erreur = true;
						}

						return null;
					}

					@Override
					@SuppressWarnings("unchecked")
					protected void onPostExecute(final Void pResult) {
						super.onPostExecute(pResult);
						myProgressDialog.dismiss();
						if (!erreur) {
							metterAJourListeStations();
							mettreAjoutLoc(lastLocation);
							((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
						} else {
							Toast toast = Toast.makeText(getApplicationContext(), "Une erreur est survenu lors de l'interrogation de VeloStar...",
									Toast.LENGTH_LONG);
							toast.show();
						}
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
			veloFavori.setNumber(station.getNumber());
			veloFavori = TransportsRennesApplication.getDataBaseHelper().selectSingle(veloFavori);
			menu.setHeaderTitle(Formatteur.formatterChaine(station.getName()));
			menu.add(Menu.NONE, veloFavori == null ? R.id.ajoutFavori : R.id.supprimerFavori, 0,
					veloFavori == null ? "Ajouter aux favoris" : "Supprimer des favoris");
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
				veloFavori.setNumber(station.getNumber());
				TransportsRennesApplication.getDataBaseHelper().insert(veloFavori);
				return true;
			case R.id.supprimerFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.setNumber(station.getNumber());
				TransportsRennesApplication.getDataBaseHelper().delete(veloFavori);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
