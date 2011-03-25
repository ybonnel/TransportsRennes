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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.PointDeVenteAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;
import fr.ybo.transportsrennes.util.LogYbo;

/**
 * Activité de type liste permettant de lister les points de vente par distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListPointsDeVente extends MenuAccueil.ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListPointsDeVente.class);

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des points de vente.
	 */
	private List<PointDeVente> pointsDeVenteIntent;
	private final List<PointDeVente> pointsDeVente = Collections.synchronizedList(new ArrayList<PointDeVente>(150));
	private final List<PointDeVente> pointsDeVenteFiltres = Collections.synchronizedList(new ArrayList<PointDeVente>(150));

	private Location lastLocation;

	/**
	 * Permet de mettre à jour les distances des points de vente par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	private void mettreAjoutLoc(Location location) {
		if (location != null && (lastLocation == null || location.getAccuracy() <= lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			synchronized (pointsDeVente) {
				for (PointDeVente pointDeVente : pointsDeVente) {
					pointDeVente.calculDistance(location);
				}
				Collections.sort(pointsDeVente, new PointDeVente.ComparatorDistance());
			}
			metterAJourListe();
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

	private ProgressDialog myProgressDialog;

	private void metterAJourListe() {
		String query = editText.getText().toString().toUpperCase();
		pointsDeVenteFiltres.clear();
		synchronized (pointsDeVente) {
			for (PointDeVente pointDeVente : pointsDeVente) {
				if (pointDeVente.name.toUpperCase().contains(query.toUpperCase())) {
					pointsDeVenteFiltres.add(pointDeVente);
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
		setContentView(R.layout.listpointsdevente);
		pointsDeVenteIntent =
				(List<PointDeVente>) (getIntent().getExtras() == null ? null : getIntent().getExtras().getSerializable("pointsDeVente"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new PointDeVenteAdapter(this, pointsDeVenteFiltres));
		listView = getListView();
		editText = (EditText) findViewById(R.id.listpointsdevente_input);
		editText.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void afterTextChanged(Editable editable) {
				metterAJourListe();
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				PointDeVenteAdapter adapter = (PointDeVenteAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				PointDeVente pointDeVente = adapter.getItem(position);
				String lat = Double.toString(pointDeVente.getLatitude());
				String lon = Double.toString(pointDeVente.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + pointDeVente.name + "+@" + lat + ',' + lon);
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
				myProgressDialog = ProgressDialog.show(ListPointsDeVente.this, "", getString(R.string.dialogRequetePointsDeVente), true);
			}

			@Override
			protected Void doInBackground(Void... pParams) {
				try {
                    List<PointDeVente> listPdvTmp = (pointsDeVenteIntent == null ? keolis.getPointDeVente() : pointsDeVenteIntent);
					synchronized (pointsDeVente) {
						pointsDeVente.clear();
						pointsDeVente.addAll(listPdvTmp);
						Collections.sort(pointsDeVente, new Comparator<PointDeVente>() {
							public int compare(PointDeVente o1, PointDeVente o2) {
								return o1.name.compareToIgnoreCase(o2.name);
							}
						});
						pointsDeVenteFiltres.clear();
						pointsDeVenteFiltres.addAll(pointsDeVente);
					}
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans ListPointsDeVente.doInBackGround", exception);
					erreur = true;
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (erreur) {
					Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationStar), Toast.LENGTH_LONG);
					toast.show();
					finish();
				} else {
					findViewById(R.id.enteteGoogleMap).setOnClickListener(new View.OnClickListener() {
						public void onClick(View view) {
							Intent intent = new Intent(ListPointsDeVente.this, PointsDeVentesOnMap.class);
							ArrayList<PointDeVente> pointsDeVenteSerialisable = new ArrayList<PointDeVente>(pointsDeVenteFiltres.size());
							pointsDeVenteSerialisable.addAll(pointsDeVenteFiltres);
							intent.putExtra("pointsDeVente", pointsDeVenteSerialisable);
							startActivity(intent);
						}
					});
					activeGps();
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
				myProgressDialog.dismiss();
			}
		}.execute();
	}
}
