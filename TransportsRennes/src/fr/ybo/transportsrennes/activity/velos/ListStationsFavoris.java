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
package fr.ybo.transportsrennes.activity.velos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Refreshable;
import fr.ybo.transportscommun.donnees.modele.VeloFavori;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.Formatteur;
import fr.ybo.transportscommun.util.TacheAvecProgressDialog;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.velos.VeloAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;

/**
 * Activité de type liste permettant de lister les stations de velos favorites.
 *
 * @author ybonnel
 */
public class ListStationsFavoris extends BaseListActivity implements Refreshable {

    /**
     * Liste des stations.
     */
    private final List<Station> stations = Collections.synchronizedList(new ArrayList<Station>(10));

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
        new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteVeloStar), true) {
            @Override
            protected void myDoBackground() throws ErreurReseau {
                final List<VeloFavori> velosFavoris = TransportsRennesApplication.getDataBaseHelper()
                        .select(new VeloFavori());
                final Collection<String> numbers = new ArrayList<String>(10);
                for (final VeloFavori favori : velosFavoris) {
                    numbers.add(favori.number);
                }
                final Collection<Station> stationsTmp = Keolis.getStationByNumbers(numbers);
                if (isCancelled()) {
                    return;
                }
                synchronized (stations) {
                    stations.clear();
                    stations.addAll(stationsTmp);
                    Collections.sort(stations, new Station.StationComparator());
                }
            }

            @Override
            protected void onPostExecute(final Void result) {
                if (!isCancelled()) {
                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                }
                super.onPostExecute(result);
            }
        }.execute((Void) null);
    }

    @Override
    public void refresh() {
        new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteVeloStar), true) {
            @Override
            protected void myDoBackground() throws ErreurReseau {
                final List<VeloFavori> velosFavoris = TransportsRennesApplication.getDataBaseHelper()
                        .select(new VeloFavori());
                final Collection<String> numbers = new ArrayList<String>(10);
                for (final VeloFavori favori : velosFavoris) {
                    numbers.add(favori.number);
                }
                final Collection<Station> stationsTmp = Keolis.getStationByNumbers(numbers);
                if (isCancelled()) {
                    return;
                }
                synchronized (stations) {
                    stations.clear();
                    stations.addAll(stationsTmp);
                    Collections.sort(stations, new Station.StationComparator());
                }
            }

            @Override
            protected void onPostExecute(final Void result) {
                if (!isCancelled()) {
                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                }
                super.onPostExecute(result);
            }
        }.execute((Void) null);
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
                return onOptionsItemSelected(item);
        }
    }


}
