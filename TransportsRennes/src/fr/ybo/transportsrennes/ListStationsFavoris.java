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
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activité de type liste permettant de lister les stations de velos favorites.
 *
 * @author ybonnel
 */
public class ListStationsFavoris extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(ListStationsFavoris.class);

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private Keolis keolis = Keolis.getInstance();

	/**
	 * Liste des stations.
	 */
	private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>());

	private ProgressDialog myProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listvelofavoris);
		setListAdapter(new VeloAdapter(getApplicationContext(), stations));
		ListView listView = getListView();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				VeloAdapter veloAdapter = (VeloAdapter) ((ListView) adapterView).getAdapter();
				Station station = veloAdapter.getItem(position);
				String _lat = Double.toString(station.getLatitude());
				String _lon = Double.toString(station.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(station.name) + "+@" + _lat + "," + _lon);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
					List<VeloFavori> velosFavoris = TransportsRennesApplication.getDataBaseHelper().select(new VeloFavori());
					List<String> numbers = new ArrayList<String>();
					for (VeloFavori favori : velosFavoris) {
						numbers.add(favori.number);
					}
					synchronized (stations) {
						stations.clear();
						stations.addAll(keolis.getStationByNumbers(numbers));
						Collections.sort(stations, new Comparator<Station>() {
							public int compare(Station o1, Station o2) {
								return o1.name.compareToIgnoreCase(o2.name);
							}
						});
					}
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
					((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationVeloStar), Toast.LENGTH_LONG);
					toast.show();
					ListStationsFavoris.this.finish();
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
							List<VeloFavori> velosFavoris = TransportsRennesApplication.getDataBaseHelper().select(new VeloFavori());
							List<String> numbers = new ArrayList<String>();
							for (VeloFavori favori : velosFavoris) {
								numbers.add(favori.number);
							}
							synchronized (stations) {
								stations.clear();
								stations.addAll(keolis.getStationByNumbers(numbers));
								Collections.sort(stations, new Comparator<Station>() {
									public int compare(Station o1, Station o2) {
										return o1.name.compareToIgnoreCase(o2.name);
									}
								});
							}
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
							((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
						} else {
							Toast toast =
									Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationVeloStar), Toast.LENGTH_LONG);
							toast.show();
							ListStationsFavoris.this.finish();
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
				veloFavori.number = station.number;
				TransportsRennesApplication.getDataBaseHelper().delete(veloFavori);
				((VeloAdapter) getListAdapter()).getStations().remove(station);
				((VeloAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}