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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.ArretGpsAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
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
public class ListArretByPosition extends MenuAccueil.ListActivity implements LocationListener {

	private static final LogYbo LOG_YBO = new LogYbo(ListArretByPosition.class);

	/**
	 * Le locationManager permet d'accéder au GPS du téléphone.
	 */
	private LocationManager locationManager;

	/**
	 * Liste des stations.
	 */
	private final List<Arret> arrets = Collections.synchronizedList(new ArrayList<Arret>());
	private final List<Arret> arretsFiltrees = Collections.synchronizedList(new ArrayList<Arret>());

	private List<Arret> arretsIntent = null;

	private Location lastLocation = null;

	/**
	 * Permet de mettre à jour les distances des arrêts par rapport à une
	 * nouvelle position.
	 *
	 * @param location position courante.
	 */
	@SuppressWarnings("unchecked")
	private void mettreAjoutLoc(Location location) {
		if (location != null && (lastLocation == null || location.getAccuracy() <= (lastLocation.getAccuracy() + 50.0))) {
			lastLocation = location;
			synchronized (arrets) {
				for (Arret arret : arrets) {
					arret.calculDistance(location);
				}
				Collections.sort(arrets, new Arret.ComparatorDistance());
			}
			metterAJourListeArrets();
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
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
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
		synchronized (arrets) {
			for (Arret arret : arrets) {
				if (arret.nom.toUpperCase().contains(query.toUpperCase())) {
					arretsFiltrees.add(arret);
				}
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
		arretsIntent = (List<Arret>) (getIntent().getExtras() == null ? null : getIntent().getExtras().getSerializable("arrets"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setListAdapter(new ArretGpsAdapter(getApplicationContext(), arretsFiltrees));
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
				intent.putExtra("favori", arret.favori);
				startActivity(intent);
			}
		});

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog.show(ListArretByPosition.this, "", getString(R.string.rechercheArrets), true);
			}

			@Override
			protected Void doInBackground(Void... voids) {
				construireListeArrets();
				synchronized (arrets) {
					Collections.sort(arrets, new Comparator<Arret>() {
						public int compare(Arret o1, Arret o2) {
							return o1.nom.compareToIgnoreCase(o2.nom);
						}
					});
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				metterAJourListeArrets();
				activeGps();
				((ArrayAdapter<Station>) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
				super.onPostExecute(aVoid);
			}
		}.execute();

	}

	private void construireListeArrets() {
		if (arretsIntent != null) {
			arrets.clear();
			arrets.addAll(arretsIntent);
			return;
		}
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT");
		requete.append(" Arret.id as arretId,");
		requete.append(" Arret.nom as arretNom,");
		requete.append(" Arret.latitude as arretLatitude,");
		requete.append(" Arret.longitude as arretLongitude,");
		requete.append(" Direction.direction as favoriDirection,");
		requete.append(" Ligne.id as ligneId,");
		requete.append(" Ligne.nomCourt as nomCourt,");
		requete.append(" Ligne.nomLong as nomLong ");
		requete.append("FROM Arret, ArretRoute, Ligne, Direction ");
		requete.append("WHERE Arret.id = ArretRoute.arretId");
		requete.append(" AND ArretRoute.ligneId = Ligne.id");
		requete.append(" AND ArretRoute.directionId = Direction.id");
		Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), null);
		Arret arret;
		arrets.clear();
		int arretIdIndex = cursor.getColumnIndex("arretId");
		int arretNomIndex = cursor.getColumnIndex("arretNom");
		int latitudeIndex = cursor.getColumnIndex("arretLatitude");
		int longitudeIndex = cursor.getColumnIndex("arretLongitude");
		int directionIndex = cursor.getColumnIndex("favoriDirection");
		int ligneIdIndex = cursor.getColumnIndex("ligneId");
		int nomCourtIndex = cursor.getColumnIndex("nomCourt");
		int nomLongIndex = cursor.getColumnIndex("nomLong");
		while (cursor.moveToNext()) {
			arret = new Arret();
			arret.id = cursor.getString(arretIdIndex);
			arret.nom = cursor.getString(arretNomIndex);
			arret.latitude = cursor.getDouble(latitudeIndex);
			arret.longitude = cursor.getDouble(longitudeIndex);
			arret.favori = new ArretFavori();
			arret.favori.direction = cursor.getString(directionIndex);
			arret.favori.ligneId = cursor.getString(ligneIdIndex);
			arret.favori.nomCourt = cursor.getString(nomCourtIndex);
			arret.favori.nomLong = cursor.getString(nomLongIndex);
			arret.favori.nomArret = arret.nom;
			arret.favori.arretId = arret.id;
			arrets.add(arret);
		}
		cursor.close();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Arret arret = (Arret) getListAdapter().getItem(info.position);
			ArretFavori arretFavori = new ArretFavori();
			arretFavori.arretId = arret.id;
			arretFavori.ligneId = arret.favori.ligneId;
			arretFavori = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretFavori);
			menu.setHeaderTitle(arret.nom);
			menu.add(Menu.NONE, arretFavori == null ? R.id.ajoutFavori : R.id.supprimerFavori, 0,
					arretFavori == null ? getString(R.string.ajouterFavori) : getString(R.string.suprimerFavori));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Arret arret;
		switch (item.getItemId()) {
			case R.id.ajoutFavori:
				arret = (Arret) getListAdapter().getItem(info.position);
				Ligne myLigne = new Ligne();
				myLigne.id = arret.favori.ligneId;
				myLigne = TransportsRennesApplication.getDataBaseHelper().selectSingle(myLigne);
				if (myLigne.chargee == null || !myLigne.chargee) {
					chargerLigne(myLigne);
				}
				TransportsRennesApplication.getDataBaseHelper().insert(arret.favori);
				return true;
			case R.id.supprimerFavori:
				arret = (Arret) getListAdapter().getItem(info.position);
				ArretFavori arretFavori = new ArretFavori();
				arretFavori.arretId = arret.id;
				arretFavori.ligneId = arret.favori.ligneId;
				if (TransportsWidgetConfigure.isNotUsed(this, arretFavori)) {
					TransportsRennesApplication.getDataBaseHelper().delete(arretFavori);
				} else {
					Toast.makeText(this, getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG)
							.show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private ProgressDialog myProgressDialog;

	private void chargerLigne(final Ligne myLigne) {

		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.premierAccesLigne, myLigne.nomCourt), true);

		new AsyncTask<Void, Void, Void>() {

			boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.chargeDetailLigne(myLigne);
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur lors du chargement du détail de la ligne", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(ListArretByPosition.this,
							getString(R.string.erreur_chargementStar),
							Toast.LENGTH_LONG).show();
					ListArretByPosition.this.finish();
				}
			}

		}.execute();

	}


}