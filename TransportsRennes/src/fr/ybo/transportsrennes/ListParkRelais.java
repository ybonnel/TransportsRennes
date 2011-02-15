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
import fr.ybo.transportsrennes.adapters.ParkRelaiAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activité de type liste permettant de lister les parcs relais par distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListParkRelais extends MenuAccueil.ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListParkRelais.class);

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
	private List<ParkRelai> parkRelaiIntent;
	private final List<ParkRelai> parkRelais = Collections.synchronizedList(new ArrayList<ParkRelai>(4));
	private final List<ParkRelai> parkRelaisFiltres = Collections.synchronizedList(new ArrayList<ParkRelai>(4));

	private Location lastLocation;

	/**
	 * Permet de mettre à jour les distances des parc relais par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(final Location location) {
		if (location != null && (lastLocation == null || location.getAccuracy() <= lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			synchronized (parkRelais) {
				for (final ParkRelai parkRelai : parkRelais) {
					parkRelai.calculDistance(location);
				}
				Collections.sort(parkRelais, new ParkRelai.ComparatorDistance());
			}
			metterAJourListeParkRelais();
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
	private void metterAJourListeParkRelais() {
		final String query = editText.getText().toString().toUpperCase();
		synchronized (parkRelais) {
			parkRelaisFiltres.clear();
			for (final ParkRelai parkRelai : parkRelais) {
				if (parkRelai.name.toUpperCase().contains(query.toUpperCase())) {
					parkRelaisFiltres.add(parkRelai);
				}
			}
		}
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}

	private EditText editText;
	private ListView listView;

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listparkrelais);
		parkRelaiIntent = (List<ParkRelai>) (getIntent().getExtras() == null ? null : getIntent().getExtras().getSerializable("parcRelais"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new ParkRelaiAdapter(this, parkRelaisFiltres));
		listView = getListView();
		editText = (EditText) findViewById(R.id.listparkrelai_input);
		editText.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
			}

			public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
			}

			public void afterTextChanged(final Editable editable) {
				metterAJourListeParkRelais();
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final ParkRelaiAdapter adapter = (ParkRelaiAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				final ParkRelai parkRelai = adapter.getItem(position);
				final String _lat = Double.toString(parkRelai.getLatitude());
				final String _lon = Double.toString(parkRelai.getLongitude());
				final Uri uri = Uri.parse("geo:0,0?q=" + parkRelai.name + "+@" + _lat + "," + _lon);
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
				myProgressDialog = ProgressDialog.show(ListParkRelais.this, "", getString(R.string.dialogRequeteParkRelais), true);
			}

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					synchronized (parkRelais) {
						parkRelais.clear();
						parkRelais.addAll(parkRelaiIntent == null ? keolis.getParkRelais() : parkRelaiIntent);
						Collections.sort(parkRelais, new Comparator<ParkRelai>() {
							public int compare(final ParkRelai o1, final ParkRelai o2) {
								return o1.name.compareToIgnoreCase(o2.name);
							}
						});
						parkRelaisFiltres.clear();
						parkRelaisFiltres.addAll(parkRelais);
					}
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans ListParkRelais.doInBackGround", exception);
					erreur = true;
				}

				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void pResult) {
				myProgressDialog.dismiss();
				if (erreur) {
					final Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationStar), Toast.LENGTH_LONG);
					toast.show();
					finish();
				} else {
					findViewById(R.id.enteteGoogleMap).setOnClickListener(new View.OnClickListener() {
						public void onClick(final View view) {
							final Intent intent = new Intent(ListParkRelais.this, ParkRelaisOnMap.class);
							final ArrayList<ParkRelai> parkRelaisSerializable = new ArrayList<ParkRelai>();
							parkRelaisSerializable.addAll(parkRelaisFiltres);
							intent.putExtra("parkRelais", parkRelaisSerializable);
							startActivity(intent);
						}
					});
					activeGps();
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
				super.onPostExecute(pResult);
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
					myProgressDialog = ProgressDialog.show(ListParkRelais.this, "", getString(R.string.dialogRequeteVeloStar), true);
				}

				@Override
				protected Void doInBackground(final Void... pParams) {
					try {
						synchronized (parkRelais) {
							parkRelais.clear();
							if (parkRelaiIntent == null) {
								parkRelais.addAll(keolis.getParkRelais());
							} else {
								final Collection<String> ids = new ArrayList<String>();
								for (final ParkRelai parc : parkRelaiIntent) {
									ids.add(parc.name);
								}
								for (final ParkRelai parc : keolis.getParkRelais()) {
									if (ids.contains(parc.name)) {
										parkRelais.add(parc);
									}
								}
							}
							Collections.sort(parkRelais, new Comparator<ParkRelai>() {
								public int compare(final ParkRelai o1, final ParkRelai o2) {
									return o1.name.compareToIgnoreCase(o2.name);
								}
							});
							parkRelaisFiltres.clear();
							parkRelaisFiltres.addAll(parkRelais);
						}
					} catch (Exception exception) {
						LOG_YBO.erreur("Erreur dans ListParkRelais.doInBackGround", exception);
						erreur = true;
					}

					return null;
				}

				@Override
				@SuppressWarnings("unchecked")
				protected void onPostExecute(final Void pResult) {
					myProgressDialog.dismiss();
					if (erreur) {
						final Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationStar), Toast.LENGTH_LONG);
						toast.show();
						finish();
					} else {
						metterAJourListeParkRelais();
						mettreAjoutLoc(lastLocation);
						((BaseAdapter) getListAdapter()).notifyDataSetChanged();
					}
					super.onPostExecute(pResult);
				}
			}.execute();
			return true;
		}
		return false;
	}
}
