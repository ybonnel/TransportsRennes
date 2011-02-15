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
import android.widget.BaseAdapter;
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
 * Activité de type liste permettant de lister les stations de velos favorites.
 *
 * @author ybonnel
 */
public class ListStationsFavoris extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(ListStationsFavoris.class);

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	/**
	 * Liste des stations.
	 */
	private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>(10));

	private ProgressDialog myProgressDialog;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listvelofavoris);
		setListAdapter(new VeloAdapter(getApplicationContext(), stations));
		final ListView listView = getListView();
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
				myProgressDialog = ProgressDialog.show(ListStationsFavoris.this, "", getString(R.string.dialogRequeteVeloStar), true);
			}

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					final List<VeloFavori> velosFavoris = TransportsRennesApplication.getDataBaseHelper().select(new VeloFavori());
					final Collection<String> numbers = new ArrayList<String>(10);
					for (final VeloFavori favori : velosFavoris) {
						numbers.add(favori.number);
					}
					synchronized (stations) {
						stations.clear();
						stations.addAll(keolis.getStationByNumbers(numbers));
						Collections.sort(stations, new Comparator<Station>() {
							public int compare(final Station o1, final Station o2) {
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
			protected void onPostExecute(final Void result) {
				myProgressDialog.dismiss();
				if (erreur) {
					final Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationVeloStar), Toast.LENGTH_LONG);
					toast.show();
					finish();
				} else {
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
					myProgressDialog = ProgressDialog.show(ListStationsFavoris.this, "", getString(R.string.dialogRequeteVeloStar), true);
				}

				@Override
				protected Void doInBackground(final Void... pParams) {
					try {
						final List<VeloFavori> velosFavoris = TransportsRennesApplication.getDataBaseHelper().select(new VeloFavori());
						final Collection<String> numbers = new ArrayList<String>(10);
						for (final VeloFavori favori : velosFavoris) {
							numbers.add(favori.number);
						}
						synchronized (stations) {
							stations.clear();
							stations.addAll(keolis.getStationByNumbers(numbers));
							Collections.sort(stations, new Comparator<Station>() {
								public int compare(final Station o1, final Station o2) {
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
				protected void onPostExecute(final Void result) {
					myProgressDialog.dismiss();
					if (erreur) {
						final Toast toast =
								Toast.makeText(getApplicationContext(), getString(R.string.erreur_interrogationVeloStar), Toast.LENGTH_LONG);
						toast.show();
						finish();
					} else {
						((BaseAdapter) getListAdapter()).notifyDataSetChanged();
					}
					super.onPostExecute(result);
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
			menu.setHeaderTitle(Formatteur.formatterChaine(station.name));
			menu.add(Menu.NONE, R.id.supprimerFavori, 0, getString(R.string.suprimerFavori));
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final Station station;
		final VeloFavori veloFavori;
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				station = (Station) getListAdapter().getItem(info.position);
				veloFavori = new VeloFavori();
				veloFavori.number = station.number;
				TransportsRennesApplication.getDataBaseHelper().delete(veloFavori);
				((VeloAdapter) getListAdapter()).getStations().remove(station);
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}