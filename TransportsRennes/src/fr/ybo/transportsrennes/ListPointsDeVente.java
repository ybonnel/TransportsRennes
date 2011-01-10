package fr.ybo.transportsrennes;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.PointDeVenteAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activité de type liste permettant de lister les points de vente par distances de la
 * position actuelle.
 *
 * @author ybonnel
 */
public class ListPointsDeVente extends MenuAccueil.ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListPointsDeVente.class);

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private Keolis keolis = Keolis.getInstance();

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des points de vente.
	 */
	private final List<PointDeVente> pointsDeVente = Collections.synchronizedList(new ArrayList<PointDeVente>());
	private final List<PointDeVente> pointsDeVenteFiltres = Collections.synchronizedList(new ArrayList<PointDeVente>());

	private Location lastLocation = null;

	/**
	 * Permet de mettre à jour les distances des points de vente par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(Location location) {
		if (lastLocation == null || location.getAccuracy() <= (lastLocation.getAccuracy() + 50.0)) {
			lastLocation = location;
			synchronized (pointsDeVente) {
				for (PointDeVente pointDeVente : pointsDeVente) {
					pointDeVente.calculDistance(location);
				}
			}
			Collections.sort(pointsDeVente, new PointDeVente.ComparatorDistance());
			Collections.sort(pointsDeVenteFiltres, new PointDeVente.ComparatorDistance());
			((ArrayAdapter<PointDeVente>) getListAdapter()).notifyDataSetChanged();
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
			Toast.makeText(getApplicationContext(), "Pour mieux profiter de cette page, il est préférable d'activer votre GPS.", Toast.LENGTH_SHORT)
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
		desactiveGps();
		super.onPause();
	}

	private ProgressDialog myProgressDialog;

	@SuppressWarnings("unchecked")
	private void metterAJourListe() {
		String query = editText.getText().toString().toUpperCase();
		pointsDeVenteFiltres.clear();
		synchronized (pointsDeVente) {
			for (PointDeVente pointDeVente : pointsDeVente) {
				if (pointDeVente.getName().toUpperCase().contains(query.toUpperCase())) {
					pointsDeVenteFiltres.add(pointDeVente);
				}
			}
		}
		((ArrayAdapter<PointDeVente>) listView.getAdapter()).notifyDataSetChanged();
	}

	private EditText editText;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listpointsdevente);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new PointDeVenteAdapter(this, R.layout.pointdevente, pointsDeVenteFiltres));
		listView = getListView();
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
				PointDeVenteAdapter adapter = (PointDeVenteAdapter) ((ListView) adapterView).getAdapter();
				PointDeVente pointDeVente = adapter.getItem(position);
				String _lat = Double.toString(pointDeVente.getLatitude());
				String _lon = Double.toString(pointDeVente.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + pointDeVente.getName() + "+@" + _lat + "," + _lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException noGoogleMapsException) {
					LOG_YBO.erreur("Google maps de doit pas être présent", noGoogleMapsException);
					Toast.makeText(getApplicationContext(), "Vous n'avez pas GoogleMaps d'installé...", Toast.LENGTH_LONG).show();
				}
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequetePointsDeVente), true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					pointsDeVente.clear();
					pointsDeVente.addAll(keolis.getPointDeVente());
					Collections.sort(pointsDeVente, new Comparator<PointDeVente>() {
						public int compare(PointDeVente o1, PointDeVente o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
					pointsDeVenteFiltres.clear();
					pointsDeVenteFiltres.addAll(pointsDeVente);
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans ListPointsDeVente.doInBackGround", exception);
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
					activeGps();
					((ArrayAdapter<PointDeVente>) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast toast =
							Toast.makeText(getApplicationContext(), "Une erreur est survenu lors de l'interrogation du STAR...", Toast.LENGTH_LONG);
					toast.show();
					ListPointsDeVente.this.finish();
				}
			}
		}.execute();
	}
}
