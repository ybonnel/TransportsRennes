package fr.ybo.transportsrennes;

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
import android.view.*;
import android.widget.*;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.ParkRelaiAdapter;
import fr.ybo.transportsrennes.adapters.VeloAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.VeloFavori;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activité de type liste permettant de lister les parks relais par distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListParkRelais extends MenuAccueil.ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListParkRelais.class);

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
	private List<ParkRelai> parkRelais = new ArrayList<ParkRelai>();
	private List<ParkRelai> parkRelaisFiltres = new ArrayList<ParkRelai>();

	private Location lastLocation = null;

	/**
	 * Permet de mettre à jour les distances des park relais par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(Location location) {
		if (lastLocation == null || location.getAccuracy() <= (lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			for (ParkRelai parkRelai : parkRelais) {
				parkRelai.calculDistance(location);
			}
			Collections.sort(parkRelais, new ParkRelai.ComparatorDistance());
			Collections.sort(parkRelaisFiltres, new ParkRelai.ComparatorDistance());
			((ArrayAdapter<ParkRelai>) getListAdapter()).notifyDataSetChanged();
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
			Toast.makeText(getApplicationContext(), "Pour mieux profiter de cette page, il est préférable d'activer votre GPS.", Toast.LENGTH_SHORT)
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
	private void metterAJourListeParkRelais() {
		String query = editText.getText().toString().toUpperCase();
		parkRelaisFiltres.clear();
		for (ParkRelai parkRelai : parkRelais) {
			if (parkRelai.getName().toUpperCase().contains(query.toUpperCase())) {
				parkRelaisFiltres.add(parkRelai);
			}
		}
		((ArrayAdapter<ParkRelai>) listView.getAdapter()).notifyDataSetChanged();
	}

	private EditText editText;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listparkrelais);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new ParkRelaiAdapter(this, R.layout.dispoparkrelai, parkRelaisFiltres));
		listView = getListView();
		editText = (EditText) findViewById(R.id.listparkrelai_input);
		editText.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void afterTextChanged(Editable editable) {
				metterAJourListeParkRelais();
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				ParkRelaiAdapter adapter = (ParkRelaiAdapter) ((ListView) adapterView).getAdapter();
				ParkRelai parkRelai = adapter.getItem(position);
				String _lat = Double.toString(parkRelai.getLatitude());
				String _lon = Double.toString(parkRelai.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + parkRelai.getName() + "+@" + _lat + "," + _lon);
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
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequeteParkRelais), true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					parkRelais = keolis.getParkRelais();
					Collections.sort(parkRelais, new Comparator<ParkRelai>() {
						public int compare(ParkRelai o1, ParkRelai o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
					parkRelaisFiltres.clear();
					parkRelaisFiltres.addAll(parkRelais);
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans ListParkRelais.doInBackGround", exception);
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
					((ArrayAdapter<ParkRelai>) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(), "Une erreur est survenu lors de l'interrogation du STAR...",
							Toast.LENGTH_LONG);
					toast.show();
					ListParkRelais.this.finish();
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
		item.setIcon(R.drawable.ic_menu_refresh);
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
							parkRelais = keolis.getParkRelais();
							Collections.sort(parkRelais, new Comparator<ParkRelai>() {
								public int compare(ParkRelai o1, ParkRelai o2) {
									return o1.getName().compareToIgnoreCase(o2.getName());
								}
							});
							parkRelaisFiltres.clear();
							parkRelaisFiltres.addAll(parkRelais);
						} catch (Exception exception) {
							LOG_YBO.erreur("Erreur dans ListParkRelais.doInBackGround", exception);
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
							metterAJourListeParkRelais();
							mettreAjoutLoc(lastLocation);
							((ArrayAdapter<ParkRelai>) getListAdapter()).notifyDataSetChanged();
						} else {
							Toast toast = Toast.makeText(getApplicationContext(), "Une erreur est survenu lors de l'interrogation du STAR...",
									Toast.LENGTH_LONG);
							toast.show();
							ListParkRelais.this.finish();
						}
					}
				}.execute();
				return true;
		}
		return false;
	}
}
