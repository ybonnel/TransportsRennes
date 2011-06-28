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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.LocationUtil;
import fr.ybo.transportsrennes.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportsrennes.util.TacheAvecProgressDialog;

/**
 * Activité de type liste permettant de lister les points de vente par distances
 * de la position actuelle.
 * 
 * @author ybonnel
 */
public class ListPointsDeVente extends MenuAccueil.ListActivity implements UpdateLocationListenner {

	private LocationUtil locationUtil;

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	/**
	 * Liste des points de vente.
	 */
	private List<PointDeVente> pointsDeVenteIntent;
	private final List<PointDeVente> pointsDeVente = Collections.synchronizedList(new ArrayList<PointDeVente>());
	private final List<PointDeVente> pointsDeVenteFiltres = Collections.synchronizedList(new ArrayList<PointDeVente>());

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
		pointsDeVenteIntent = (List<PointDeVente>) (getIntent().getExtras() == null ? null : getIntent().getExtras()
				.getSerializable("pointsDeVente"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationUtil = new LocationUtil(this, this);
		setListAdapter(new PointDeVenteAdapter(this, pointsDeVenteFiltres));
		listView = getListView();
		listView.setFastScrollEnabled(true);
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
				PointDeVenteAdapter adapter = (PointDeVenteAdapter) ((AdapterView<ListAdapter>) adapterView)
						.getAdapter();
				PointDeVente pointDeVente = adapter.getItem(position);
				String lat = Double.toString(pointDeVente.getLatitude());
				String lon = Double.toString(pointDeVente.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + pointDeVente.name + "+@" + lat + ',' + lon);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequetePointsDeVente)) {

			@Override
			protected Void myDoBackground(Void... pParams) throws ErreurReseau {
				List<PointDeVente> listPdvTmp = (pointsDeVenteIntent == null ? keolis.getPointDeVente()
						: pointsDeVenteIntent);
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

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				findViewById(R.id.enteteGoogleMap).setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						if (!pointsDeVenteFiltres.isEmpty()) {
							Intent intent = new Intent(ListPointsDeVente.this, PointsDeVentesOnMap.class);
							ArrayList<PointDeVente> pointsDeVenteSerialisable = new ArrayList<PointDeVente>(
									pointsDeVenteFiltres.size());
							pointsDeVenteSerialisable.addAll(pointsDeVenteFiltres);
							intent.putExtra("pointsDeVente", pointsDeVenteSerialisable);
							startActivity(intent);
						}
					}
				});
				updateLocation(locationUtil.getCurrentLocation());
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}.execute();
		if (!locationUtil.activeGps()) {
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
		}
	}

	public void updateLocation(Location location) {
		if (location == null) {
			return;
		}
		synchronized (pointsDeVente) {
			for (PointDeVente pointDeVente : pointsDeVente) {
				pointDeVente.calculDistance(location);
			}
			Collections.sort(pointsDeVente, new PointDeVente.ComparatorDistance());
		}
		metterAJourListe();
	}
}
