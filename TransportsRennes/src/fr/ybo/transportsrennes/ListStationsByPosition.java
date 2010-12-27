package fr.ybo.transportsrennes;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.ybo.transportsrennes.adapters.VeloAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activité de type liste permettant de lister les stations pas distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListStationsByPosition extends ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListStationsByPosition.class);

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private Keolis keolis = Keolis.getInstance();

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des stations.
	 */
	private List<Station> stations;

	private Location lastLocation = null;

	private String printLocaltion(Location location) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Localtion[provider : ");
		stringBuilder.append(location.getProvider());
		stringBuilder.append(", accuracy : ");
		stringBuilder.append(location.getAccuracy());
		stringBuilder.append(", longitude : ");
		stringBuilder.append(location.getLongitude());
		stringBuilder.append(", latitude : ");
		stringBuilder.append(location.getLatitude());
		stringBuilder.append(']');
		return stringBuilder.toString();
	}

	/**
	 * Permet de mettre à jour les distances des stations par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(Location location) {
		LOG_YBO.debug("Nouvelle location : " + printLocaltion(location));
		if (lastLocation == null || location.getAccuracy() <= (lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			for (Station station : stations) {
				station.calculDistance(location);
			}
			Collections.sort(stations, new Station.ComparatorDistance());
			((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
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
		for (String providerName : providers) {
			locationManager.requestLocationUpdates(providerName, 10000l, 20l, this);
		}
	}

	protected void desactiveGps() {
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		activeGps();
	}

	@Override
	protected void onPause() {
		super.onPause();
		desactiveGps();
	}

	private ProgressDialog myProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		stations = new ArrayList<Station>();
		setListAdapter(new VeloAdapter(getApplicationContext(), R.layout.dispovelo, stations));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequeteVeloStar), true);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... pParams) {
				stations.addAll(keolis.getStations());
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				activeGps();
				((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
			}
		}.execute();
	}
}