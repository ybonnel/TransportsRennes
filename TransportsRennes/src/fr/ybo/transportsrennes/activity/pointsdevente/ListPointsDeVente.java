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
package fr.ybo.transportsrennes.activity.pointsdevente;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Searchable;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.LocationUtil;
import fr.ybo.transportscommun.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportscommun.util.TacheAvecProgressDialog;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.pointsdevente.PointDeVenteAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;

/**
 * Activité de type liste permettant de lister les points de vente par distances
 * de la position actuelle.
 *
 * @author ybonnel
 */
public class ListPointsDeVente extends BaseListActivity implements UpdateLocationListenner, Searchable {

    private LocationUtil locationUtil;

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

	private String currentQuery = "";

	@Override
	public void updateQuery(final String newQuery) {
		currentQuery = newQuery;
		final String query = newQuery.toUpperCase();
		pointsDeVenteFiltres.clear();
		synchronized (pointsDeVente) {
			for (final PointDeVente pointDeVente : pointsDeVente) {
				if (pointDeVente.name.toUpperCase().contains(query)) {
					pointsDeVenteFiltres.add(pointDeVente);
				}
			}
		}
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}

    private ListView listView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listpointsdevente);
		getActivityHelper().setupActionBar(R.menu.listpdv_menu_items, R.menu.holo_listpdv_menu_items);
        pointsDeVenteIntent = (List<PointDeVente>) (getIntent().getExtras() == null ? null : getIntent().getExtras()
                .getSerializable("pointsDeVente"));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        locationUtil = new LocationUtil(this, this);
        setListAdapter(new PointDeVenteAdapter(this, pointsDeVenteFiltres));
        listView = getListView();
        listView.setFastScrollEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
                final ArrayAdapter<PointDeVente> adapter = (ArrayAdapter<PointDeVente>) ((AdapterView<ListAdapter>) adapterView)
                        .getAdapter();
                final PointDeVente pointDeVente = adapter.getItem(position);
                final String lat = Double.toString(pointDeVente.getLatitude());
                final String lon = Double.toString(pointDeVente.getLongitude());
                final Uri uri = Uri.parse("geo:" + lat + ',' + lon + "?q=" + lat + ',' + lon);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        listView.setTextFilterEnabled(true);
        registerForContextMenu(listView);
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequetePointsDeVente), true) {

            @Override
            protected void myDoBackground() throws ErreurReseau {
                final Collection<PointDeVente> listPdvTmp = pointsDeVenteIntent == null ? Keolis.getPointDeVente()
                        : pointsDeVenteIntent;
				if (isCancelled()) {
					return;
				}
                synchronized (pointsDeVente) {
                    pointsDeVente.clear();
                    pointsDeVente.addAll(listPdvTmp);
                    Collections.sort(pointsDeVente, new PointDeVenteComparator());
                    pointsDeVenteFiltres.clear();
                    pointsDeVenteFiltres.addAll(pointsDeVente);
                }
            }

            @Override
            protected void onPostExecute(final Void result) {
                super.onPostExecute(result);
				if (!isCancelled()) {
					updateLocation(locationUtil.getCurrentBestLocation());
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
            }
        }.execute();
        if (!locationUtil.activeGps()) {
            Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateLocation(final Location location) {
        if (location == null) {
            return;
        }
        synchronized (pointsDeVente) {
            for (final PointDeVente pointDeVente : pointsDeVente) {
                pointDeVente.calculDistance(location);
            }
            Collections.sort(pointsDeVente, new PointDeVente.ComparatorDistance());
        }
		updateQuery(currentQuery);
    }

    private static class PointDeVenteComparator implements Comparator<PointDeVente> {
        @Override
        public int compare(final PointDeVente o1, final PointDeVente o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    }
}
