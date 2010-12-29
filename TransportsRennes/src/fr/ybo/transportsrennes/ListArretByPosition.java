package fr.ybo.transportsrennes;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import fr.ybo.transportsrennes.adapters.ArretGpsAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activité de type liste permettant de lister les arrêts de bus par distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListArretByPosition extends ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListArretByPosition.class);

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des stations.
	 */
	private List<Arret> arrets = new ArrayList<Arret>();
	private List<Arret> arretsFiltrees = new ArrayList<Arret>();

	private Location lastLocation = null;

	private String printLocaltion(Location location) {
		if (location == null) {
			return "null";
		}
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
	 * Permet de mettre à jour les distances des arrêts par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(Location location) {
		LOG_YBO.debug("Nouvelle location : " + printLocaltion(location));
		if (lastLocation == null || location.getAccuracy() <= (lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			for (Arret arret : arrets) {
				arret.calculDistance(location);
			}
			Collections.sort(arrets, new Arret.ComparatorDistance());
			Collections.sort(arretsFiltrees, new Arret.ComparatorDistance());
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
		boolean gpsTrouve = false;
		for (String providerName : providers) {
			locationManager.requestLocationUpdates(providerName, 10000l, 20l, this);
			if (providerName.equals(LocationManager.GPS_PROVIDER)) {
				gpsTrouve = true;
			}
		}
		if (!gpsTrouve) {
			Toast.makeText(getApplicationContext(), "Pour mieux profiter de cette page, il est préférable d'allumer son GPS.", Toast.LENGTH_SHORT)
					.show();
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

	@SuppressWarnings("unchecked")
	private void metterAJourListeArrets() {
		String query = editText.getText().toString().toUpperCase();
		arretsFiltrees.clear();
		for (Arret arret : arrets) {
			if (arret.getNom().toUpperCase().contains(query.toUpperCase())) {
				arretsFiltrees.add(arret);
			}
		}
		((ArrayAdapter<Station>) listView.getAdapter()).notifyDataSetChanged();
	}

	private EditText editText;
	private ListView listView;

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listarretgps);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new ArretGpsAdapter(getApplicationContext(), R.layout.arretgps, arretsFiltrees));
		listView = getListView();
		editText = (EditText) findViewById(R.id.listarretgps_input);
		editText.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			public void afterTextChanged(Editable editable) {
				metterAJourListeArrets();
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Arret arret = (Arret) getListAdapter().getItem(position);
				Intent intent = new Intent(ListArretByPosition.this, DetailArret.class);
				intent.putExtra("favori", arret.getFavori());
				startActivity(intent);
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		construireListeArrets();
		Collections.sort(arrets, new Comparator<Arret>() {
			public int compare(Arret o1, Arret o2) {
				return o1.getNom().compareToIgnoreCase(o2.getNom());
			}
		});
		arretsFiltrees.clear();
		arretsFiltrees.addAll(arrets);

		activeGps();
		((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
	}

	private void construireListeArrets() {
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT");
		requete.append(" Arret.id as arretId,");
		requete.append(" Arret.nom as arretNom,");
		requete.append(" Arret.description as arretDescription,");
		requete.append(" Arret.latitude as arretLatitude,");
		requete.append(" Arret.longitude as arretLongitude,");
		requete.append(" ArretRoute.direction as favoriDirection,");
		requete.append(" Route.id as routeId,");
		requete.append(" Route.nomCourt as routeNomCourt,");
		requete.append(" Route.nomLong as routeNomLong ");
		requete.append("FROM Arret, ArretRoute, Route ");
		requete.append("WHERE Arret.id = ArretRoute.arretId");
		requete.append(" AND ArretRoute.routeId = Route.id");
		Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), null);
		Arret arret;
		arrets = new ArrayList<Arret>();
		while (cursor.moveToNext()) {
			arret = new Arret();
			arret.setId(cursor.getString(cursor.getColumnIndex("arretId")));
			arret.setNom(cursor.getString(cursor.getColumnIndex("arretNom")));
			arret.setDescription(cursor.getString(cursor.getColumnIndex("arretDescription")));
			arret.setLatitude(cursor.getDouble(cursor.getColumnIndex("arretLatitude")));
			arret.setLongitude(cursor.getDouble(cursor.getColumnIndex("arretLongitude")));
			arret.setFavori(new ArretFavori());
			arret.getFavori().setDirection(cursor.getString(cursor.getColumnIndex("favoriDirection")));
			arret.getFavori().setRouteId(cursor.getString(cursor.getColumnIndex("routeId")));
			arret.getFavori().setRouteNomCourt(cursor.getString(cursor.getColumnIndex("routeNomCourt")));
			arret.getFavori().setRouteNomLong(cursor.getString(cursor.getColumnIndex("routeNomLong")));
			arret.getFavori().setNomArret(arret.getNom());
			arret.getFavori().setStopId(arret.getId());
			arrets.add(arret);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Arret arret = (Arret) getListAdapter().getItem(info.position);
			ArretFavori arretFavori = new ArretFavori();
			arretFavori.setStopId(arret.getId());
			arretFavori = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretFavori);
			menu.setHeaderTitle(Formatteur.formatterChaine(arret.getNom()));
			menu.add(Menu.NONE, arretFavori == null ? R.id.ajoutFavori : R.id.supprimerFavori, 0,
					arretFavori == null ? "Ajouter aux favoris" : "Supprimer des favoris");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Arret arret;
		switch (item.getItemId()) {
			case R.id.ajoutFavori:
				arret = (Arret) getListAdapter().getItem(info.position);
				TransportsRennesApplication.getDataBaseHelper().insert(arret.getFavori());
				return true;
			case R.id.supprimerFavori:
				arret = (Arret) getListAdapter().getItem(info.position);
				ArretFavori arretFavori = new ArretFavori();
				arretFavori.setStopId(arret.getId());
				TransportsRennesApplication.getDataBaseHelper().delete(arretFavori);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}