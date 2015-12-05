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
package fr.ybo.transportsbordeaux.activity.velos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.ads.Ad;
import com.google.ads.AdRequest;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.adapters.velos.VeloAdapter;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.tbcapi.Keolis;
import fr.ybo.transportsbordeaux.tbcapi.modele.Station;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Refreshable;
import fr.ybo.transportscommun.donnees.modele.VeloFavori;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.Formatteur;
import fr.ybo.transportscommun.util.TacheAvecProgressDialog;

/**
 * Activité de type liste permettant de lister les stations de velos favorites.
 *
 * @author ybonnel
 */
public class ListStationsFavoris extends BaseListActivity implements Refreshable {

    /**
     * Liste des stations.
     */
    private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>());

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listvelofavoris);
		getActivityHelper().setupActionBar(R.menu.liststation_favoris_menu_items,
				R.menu.holo_liststation_favoris_menu_items);
        setListAdapter(new VeloAdapter(getApplicationContext(), stations));
        final ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
                final VeloAdapter veloAdapter = (VeloAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
                final Station station = veloAdapter.getItem(position);
                final String lat = Double.toString(station.getLatitude());
                final String lon = Double.toString(station.getLongitude());
                final Uri uri = Uri.parse("geo:" + lat + ',' + lon + "?q=" + lat + ',' + lon);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        listView.setTextFilterEnabled(true);
        registerForContextMenu(listView);
        new GetStations().execute((Void) null);

        // Look up the AdView as a resource and load a request.
        ((Ad) findViewById(R.id.adView)).loadAd(new AdRequest());
    }

    private final class GetStations extends TacheAvecProgressDialog<Void, Void, Void> {
        private GetStations() {
            super(ListStationsFavoris.this,
 getString(R.string.dialogRequeteVcub), true);
        }

		@Override
		protected void onPostExecute(final Void result) {
			super.onPostExecute(result);
			if (!isCancelled()) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fr.ybo.transportscommun.util.TacheAvecProgressDialog#myDoBackground()
		 */
		@Override
		protected void myDoBackground() throws ErreurReseau {
			final List<VeloFavori> velosFavoris = TransportsBordeauxApplication.getDataBaseHelper().select(new VeloFavori());
			final Collection<String> ids = new ArrayList<String>();
			for (final VeloFavori favori : velosFavoris) {
				ids.add(favori.number);
			}
			final Collection<Station> stationsTmp = Keolis.getStationsVcub();
			if (isCancelled()) {
				return;
			}
			synchronized (stations) {
				stations.clear();
				for (final Station station : stationsTmp) {
					if (ids.contains(Integer.toString(station.id))) {
						stations.add(station);
					}
				}
				Collections.sort(stations, new Comparator<Station>() {
					@Override
                    public int compare(final Station o1, final Station o2) {
						return o1.name.compareToIgnoreCase(o2.name);
					}
				});
			}
		}
    }

	@Override
	public void refresh() {
		new GetStations().execute((Void) null);
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
				veloFavori.number = Integer.toString(station.id);
                TransportsBordeauxApplication.getDataBaseHelper().delete(veloFavori);
                ((VeloAdapter) getListAdapter()).getStations().remove(station);
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }
}
