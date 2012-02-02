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
package fr.ybo.transportsbordeaux.activity.parkrelais;

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
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.commun.MenuAccueil;
import fr.ybo.transportsbordeaux.adapters.parkrelais.ParkingAdapter;
import fr.ybo.transportsbordeaux.database.modele.Parking;
import fr.ybo.transportsbordeaux.tbcapi.Keolis;
import fr.ybo.transportsbordeaux.tbcapi.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.util.LocationUtil;
import fr.ybo.transportsbordeaux.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportsbordeaux.util.TacheAvecProgressDialog;

/**
 * Activité de type liste permettant de lister les parcs relais par distances de
 * la position actuelle.
 *
 * @author ybonnel
 */
public class ListParkings extends MenuAccueil.ListActivity implements UpdateLocationListenner {

    /**
     * Liste des stations.
     */
    private List<Parking> parkRelaiIntent;
    private final List<Parking> parkRelais = Collections.synchronizedList(new ArrayList<Parking>());
    private final List<Parking> parkRelaisFiltres = Collections.synchronizedList(new ArrayList<Parking>());

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
            for (Parking parkRelai : parkRelais) {
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
        parkRelaiIntent = (List<Parking>) (getIntent().getExtras() == null ? null : getIntent().getExtras()
                .getSerializable("parcRelais"));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        locationUtil = new LocationUtil(this, this);

        setListAdapter(new ParkingAdapter(this, parkRelaisFiltres));
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
                ParkingAdapter adapter = (ParkingAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
                Parking parkRelai = adapter.getItem(position);
                String lat = Double.toString(parkRelai.getLatitude());
                String lon = Double.toString(parkRelai.getLongitude());
                Uri uri = Uri.parse("geo:0,0?q=" + parkRelai.name + "+@" + lat + ',' + lon);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException activityNotFound) {
                    Toast.makeText(ListParkings.this, R.string.noGoogleMap, Toast.LENGTH_LONG).show();
                }
            }
        });

        listView.setTextFilterEnabled(true);
        registerForContextMenu(listView);
        new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteParkRelais)) {
            private boolean erreurReseaux = false;

            @Override
            protected Void doInBackground(Void... params) {
                List<Parking> parkRelaisTmp;
                try {
                    parkRelaisTmp = (parkRelaiIntent == null ? Keolis.getInstance().getParkings() : parkRelaiIntent);
                    parkRelais.clear();
                    parkRelais.addAll(parkRelaisTmp);
                    Collections.sort(parkRelais, new Comparator<Parking>() {
                        public int compare(Parking o1, Parking o2) {
                            return o1.name.compareToIgnoreCase(o2.name);
                        }
                    });
                    parkRelaisFiltres.clear();
                    parkRelaisFiltres.addAll(parkRelais);
                } catch (TbcErreurReseaux e) {
                    erreurReseaux = true;
                }
                synchronized (parkRelais) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (erreurReseaux) {
                    Toast.makeText(ListParkings.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
                } else {
                    findViewById(R.id.enteteGoogleMap).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (!parkRelaisFiltres.isEmpty()) {
                                Intent intent = new Intent(ListParkings.this, ParkingsOnMap.class);
                                ArrayList<Parking> parkRelaisSerializable = new ArrayList<Parking>(parkRelaisFiltres
                                        .size());
                                parkRelaisSerializable.addAll(parkRelaisFiltres);
                                intent.putExtra("parkRelais", parkRelaisSerializable);
                                startActivity(intent);
                            }
                        }
                    });
                    updateLocation(locationUtil.getCurrentLocation());
                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                }
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
            new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteParkRelais)) {

                private boolean erreurReseaux = false;

                @Override
                protected Void doInBackground(Void... pParams) {
                    List<Parking> parkRelaisTmp;
                    try {
                        parkRelaisTmp = Keolis.getInstance().getParkings();
                        synchronized (parkRelais) {
                            majParkRelais(parkRelaisTmp);
                        }
                    } catch (TbcErreurReseaux e) {
                        erreurReseaux = true;
                    }
                    return null;
                }

                private void majParkRelais(List<Parking> parkRelaisTmp) {
                    parkRelais.clear();
                    if (parkRelaiIntent == null) {
                        parkRelais.addAll(parkRelaisTmp);
                    } else {
                        Collection<String> ids = new ArrayList<String>(parkRelaiIntent.size());
                        for (Parking parc : parkRelaiIntent) {
                            ids.add(parc.name);
                        }
                        for (Parking parc : parkRelaisTmp) {
                            if (ids.contains(parc.name)) {
                                parkRelais.add(parc);
                            }
                        }
                    }
                    Collections.sort(parkRelais, new Comparator<Parking>() {
                        public int compare(Parking o1, Parking o2) {
                            return o1.name.compareToIgnoreCase(o2.name);
                        }
                    });
                    parkRelaisFiltres.clear();
                    parkRelaisFiltres.addAll(parkRelais);

                }

                @Override
                protected void onPostExecute(Void result) {
                    if (erreurReseaux) {
                        Toast.makeText(ListParkings.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
                    } else {
                        metterAJourListeParkRelais();
                        updateLocation(locationUtil.getCurrentLocation());
                        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                    super.onPostExecute(result);
                }
            }.execute((Void) null);
            return true;
        }
        return false;
    }

    public void updateLocation(Location location) {
        if (location == null) {
            return;
        }
        synchronized (parkRelais) {
            for (Parking parkRelai : parkRelais) {
                parkRelai.calculDistance(location);
            }
            Collections.sort(parkRelais, new Parking.ComparatorDistance());
        }
        metterAJourListeParkRelais();
    }
}
