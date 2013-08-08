package fr.ybo.transportscommun.activity.parkings;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Refreshable;
import fr.ybo.transportscommun.activity.commun.Searchable;
import fr.ybo.transportscommun.adapters.parkings.IParking;
import fr.ybo.transportscommun.adapters.parkings.ParkingAdapter;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.LocationUtil;
import fr.ybo.transportscommun.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportscommun.util.TacheAvecProgressDialog;

public abstract class AbstractListParkings<T extends IParking> extends BaseListActivity implements
		UpdateLocationListenner, Searchable, Refreshable {

	private List<T> parkingsIntent;
	private final List<T> parkings = Collections.synchronizedList(new ArrayList<T>());
	private final List<T> parkingsFiltres = Collections.synchronizedList(new ArrayList<T>());

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

	private String currentQuery = "";

	@Override
	public void updateQuery(String newQuery) {
		currentQuery = newQuery;
		String query = newQuery.toUpperCase();
		synchronized (parkings) {
			parkingsFiltres.clear();
			for (T parking : parkings) {
				if (parking.getName().toUpperCase().contains(query.toUpperCase())) {
					parkingsFiltres.add(parking);
				}
			}
		}
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}

	private ListView listView;

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	protected abstract List<T> getParkings() throws ErreurReseau;

	protected abstract int getDialogueRequete();

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		parkingsIntent = (List<T>) (getIntent().getExtras() == null ? null : getIntent().getExtras().getSerializable(
				"parcRelais"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationUtil = new LocationUtil(this, this);

		setListAdapter(new ParkingAdapter<T>(this, parkingsFiltres));
		listView = getListView();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				ParkingAdapter<T> adapter = (ParkingAdapter<T>) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				T parking = adapter.getItem(position);
				String lat = Double.toString(parking.getLatitude());
				String lon = Double.toString(parking.getLongitude());
				Uri uri = Uri.parse("geo:" + lat + ',' + lon + "?q=" + lat + "," + lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException activityNotFound) {
					Toast.makeText(AbstractListParkings.this, R.string.noGoogleMap, Toast.LENGTH_LONG).show();
				}
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(getDialogueRequete()), true) {

			@Override
			protected void myDoBackground() throws ErreurReseau {
				List<T> parkRelaisTmp = (parkingsIntent == null ? getParkings() : parkingsIntent);
				if (isCancelled()) {
					return;
				}
				synchronized (parkings) {
					parkings.clear();
					parkings.addAll(parkRelaisTmp);
					Collections.sort(parkings, new Comparator<T>() {
						public int compare(T o1, T o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
					parkingsFiltres.clear();
					parkingsFiltres.addAll(parkings);
				}
			}

			@Override
			protected void onPostExecute(Void result) {
				if (!isCancelled()) {
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

	@Override
	public void refresh() {
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(getDialogueRequete()), true) {

			@Override
			protected void myDoBackground() throws ErreurReseau {
				List<T> parkingsTmp = getParkings();
				if (isCancelled()) {
					return;
				}
				synchronized (parkings) {
					majParkings(parkingsTmp);
				}
			}

			private void majParkings(List<T> parkingsTmp) {
				parkings.clear();
				if (parkingsIntent == null) {
					parkings.addAll(parkingsTmp);
				} else {
					Collection<String> ids = new ArrayList<String>(parkingsIntent.size());
					for (T parc : parkingsIntent) {
						ids.add(parc.getName());
					}
					for (T parc : parkingsTmp) {
						if (ids.contains(parc.getName())) {
							parkings.add(parc);
						}
					}
				}
				Collections.sort(parkings, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				parkingsFiltres.clear();
				parkingsFiltres.addAll(parkings);

			}

			@Override
			protected void onPostExecute(Void result) {
				if (!isCancelled()) {
					updateQuery(currentQuery);
					updateLocation(locationUtil.getCurrentLocation());
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}
		}.execute((Void) null);
	}

	public void updateLocation(Location location) {
		if (location == null) {
			return;
		}
		synchronized (parkings) {
			for (T parking : parkings) {
				parking.calculDistance(location);
			}
			Collections.sort(parkings, new IParking.ComparatorDistance());
		}
		updateQuery(currentQuery);
	}
}
