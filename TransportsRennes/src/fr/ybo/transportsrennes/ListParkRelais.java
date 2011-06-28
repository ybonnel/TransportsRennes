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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.LocationUtil;
import fr.ybo.transportsrennes.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportsrennes.util.TacheAvecProgressDialog;

/**
 * Activité de type liste permettant de lister les parcs relais par distances de
 * la position actuelle.
 * 
 * @author ybonnel
 */
public class ListParkRelais extends MenuAccueil.ListActivity implements UpdateLocationListenner {

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	/**
	 * Liste des stations.
	 */
	private List<ParkRelai> parkRelaiIntent;
	private final List<ParkRelai> parkRelais = Collections.synchronizedList(new ArrayList<ParkRelai>(4));
	private final List<ParkRelai> parkRelaisFiltres = Collections.synchronizedList(new ArrayList<ParkRelai>(4));

	private LocationUtil locationUtil;


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

	private void metterAJourListeParkRelais() {
		String query = editText.getText().toString().toUpperCase();
		synchronized (parkRelais) {
			parkRelaisFiltres.clear();
			for (ParkRelai parkRelai : parkRelais) {
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listparkrelais);
		parkRelaiIntent = (List<ParkRelai>) (getIntent().getExtras() == null ? null : getIntent().getExtras()
				.getSerializable("parcRelais"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationUtil = new LocationUtil(this, this);

		setListAdapter(new ParkRelaiAdapter(this, parkRelaisFiltres));
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
				ParkRelaiAdapter adapter = (ParkRelaiAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				ParkRelai parkRelai = adapter.getItem(position);
				String lat = Double.toString(parkRelai.getLatitude());
				String lon = Double.toString(parkRelai.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + parkRelai.name + "+@" + lat + ',' + lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException activityNotFound) {
					Toast.makeText(ListParkRelais.this, R.string.noGoogleMap, Toast.LENGTH_LONG).show();
				}
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteParkRelais)) {

			@Override
			protected Void myDoBackground(Void... pParams) throws ErreurReseau {
				List<ParkRelai> parkRelaisTmp = (parkRelaiIntent == null ? keolis.getParkRelais() : parkRelaiIntent);
				synchronized (parkRelais) {
					parkRelais.clear();
					parkRelais.addAll(parkRelaisTmp);
					Collections.sort(parkRelais, new Comparator<ParkRelai>() {
						public int compare(ParkRelai o1, ParkRelai o2) {
							return o1.name.compareToIgnoreCase(o2.name);
						}
					});
					parkRelaisFiltres.clear();
					parkRelaisFiltres.addAll(parkRelais);
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				findViewById(R.id.enteteGoogleMap).setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						if (!parkRelaisFiltres.isEmpty()) {
							Intent intent = new Intent(ListParkRelais.this, ParkRelaisOnMap.class);
							ArrayList<ParkRelai> parkRelaisSerializable = new ArrayList<ParkRelai>(parkRelaisFiltres
									.size());
							parkRelaisSerializable.addAll(parkRelaisFiltres);
							intent.putExtra("parkRelais", parkRelaisSerializable);
							startActivity(intent);
						}
					}
				});
				updateLocation(locationUtil.getCurrentLocation());
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
		if (!locationUtil.activeGps()) {
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
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
			new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteVeloStar)) {

				@Override
				protected Void myDoBackground(Void... pParams) throws ErreurReseau {
					List<ParkRelai> parkRelaisTmp = keolis.getParkRelais();
					synchronized (parkRelais) {
						majParkRelais(parkRelaisTmp);
					}
					return null;
				}

				private void majParkRelais(List<ParkRelai> parkRelaisTmp) {
					parkRelais.clear();
					if (parkRelaiIntent == null) {
						parkRelais.addAll(parkRelaisTmp);
					} else {
						Collection<String> ids = new ArrayList<String>(parkRelaiIntent.size());
						for (ParkRelai parc : parkRelaiIntent) {
							ids.add(parc.name);
						}
						for (ParkRelai parc : parkRelaisTmp) {
							if (ids.contains(parc.name)) {
								parkRelais.add(parc);
							}
						}
					}
					Collections.sort(parkRelais, new Comparator<ParkRelai>() {
						public int compare(ParkRelai o1, ParkRelai o2) {
							return o1.name.compareToIgnoreCase(o2.name);
						}
					});
					parkRelaisFiltres.clear();
					parkRelaisFiltres.addAll(parkRelais);

				}

				@Override
				protected void onPostExecute(Void result) {
					metterAJourListeParkRelais();
					updateLocation(locationUtil.getCurrentLocation());
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
					super.onPostExecute(result);
				}
			}.execute();
			return true;
		}
		return false;
	}

	public void updateLocation(Location location) {
		if (location == null) {
			return;
		}
		synchronized (parkRelais) {
			for (ParkRelai parkRelai : parkRelais) {
				parkRelai.calculDistance(location);
			}
			Collections.sort(parkRelais, new ParkRelai.ComparatorDistance());
		}
		metterAJourListeParkRelais();
	}
}
