package fr.ybo.transportscommun.activity.bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Searchable;
import fr.ybo.transportscommun.adapters.bus.ArretGpsAdapter;
import fr.ybo.transportscommun.donnees.manager.LigneInexistanteException;
import fr.ybo.transportscommun.donnees.manager.gtfs.UpdateDataBase;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.LocationUtil;
import fr.ybo.transportscommun.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportscommun.util.NoSpaceLeftException;
import fr.ybo.transportscommun.util.UpdateTimeUtil;
import fr.ybo.transportscommun.util.UpdateTimeUtil.UpdateTime;

public abstract class AbstractListArretByPosition extends BaseListActivity implements UpdateLocationListenner,
		Searchable {

	private LocationUtil locationUtil;

	/**
	 * Liste des stations.
	 */
	private final List<Arret> arrets = Collections.synchronizedList(new ArrayList<Arret>(1500));
	private final List<Arret> arretsFiltrees = Collections.synchronizedList(new ArrayList<Arret>(1500));

	private List<Arret> arretsIntent;

	private boolean startUpdateTime = false;

	private UpdateTimeUtil updateTimeUtil = null;

	@Override
	protected void onResume() {
		super.onResume();
		locationUtil.activeGps();
		if (startUpdateTime) {
			updateTimeUtil.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationUtil.desactiveGps();
		updateTimeUtil.stop();
	}

	private String currentQuery = "";

	@Override
	public void updateQuery(String newQuery) {
		currentQuery = newQuery;
		metterAJourListeArrets(newQuery);
	}

	private void metterAJourListeArrets(String newQuery) {
		String query = newQuery.toUpperCase();
		arretsFiltrees.clear();
		synchronized (arrets) {
			for (Arret arret : arrets) {
				if (arret.nom.toUpperCase().contains(query.toUpperCase())) {
					arretsFiltrees.add(arret);
				}
			}
		}
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}

	private ListView listView;

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	protected abstract Class<? extends AbstractDetailArret> getDetailArret();

	protected abstract void deleteFavori(ArretFavori favori);

	protected abstract Class<?> getRawClass();

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		arretsIntent = (List<Arret>) (getIntent().getExtras() == null ? null : getIntent().getExtras().getSerializable(
				"arrets"));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationUtil = new LocationUtil(this, this);
		setListAdapter(new ArretGpsAdapter(getApplicationContext(), arretsFiltrees));
		updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

			public void update(Calendar calendar) {
				((ArretGpsAdapter) getListAdapter()).setCalendar(calendar);
				((ArretGpsAdapter) getListAdapter()).notifyDataSetChanged();
			}

			@Override
			public boolean updateSecond() {
				return false;
			}

			@Override
			public Set<Integer> secondesToUpdate() {
				return null;
			}
		}, this);
		listView = getListView();
		listView.setFastScrollEnabled(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Arret arret = (Arret) getListAdapter().getItem(position);
				Intent intent = new Intent(AbstractListArretByPosition.this, getDetailArret());
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
				myProgressDialog = ProgressDialog.show(AbstractListArretByPosition.this, "",
						getString(R.string.rechercheArrets), true);
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
			protected void onPostExecute(Void result) {
				metterAJourListeArrets(currentQuery);
				updateLocation(locationUtil.getCurrentLocation());
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				try {
					myProgressDialog.dismiss();
				} catch (IllegalArgumentException ignore) {
				}
				updateTimeUtil.start();
				startUpdateTime = true;
				super.onPostExecute(result);
			}
		}.execute();
		if (!locationUtil.activeGps()) {
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
		}

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
		requete.append(" Ligne.nomLong as nomLong, ");
		requete.append(" ArretRoute.macroDirection as macroDirection ");
		requete.append("FROM Arret, ArretRoute, Ligne, Direction ");
		requete.append("WHERE Arret.id = ArretRoute.arretId");
		requete.append(" AND ArretRoute.ligneId = Ligne.id");
		requete.append(" AND ArretRoute.directionId = Direction.id");
		Cursor cursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), null);
		arrets.clear();
		int arretIdIndex = cursor.getColumnIndex("arretId");
		int arretNomIndex = cursor.getColumnIndex("arretNom");
		int latitudeIndex = cursor.getColumnIndex("arretLatitude");
		int longitudeIndex = cursor.getColumnIndex("arretLongitude");
		int directionIndex = cursor.getColumnIndex("favoriDirection");
		int ligneIdIndex = cursor.getColumnIndex("ligneId");
		int nomCourtIndex = cursor.getColumnIndex("nomCourt");
		int nomLongIndex = cursor.getColumnIndex("nomLong");
		int macroDirectionIndex = cursor.getColumnIndex("macroDirection");
		while (cursor.moveToNext()) {
			Arret arret = new Arret();
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
			arret.favori.macroDirection = cursor.getInt(macroDirectionIndex);
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
			arretFavori.macroDirection = arret.favori.macroDirection;
			arretFavori = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arretFavori);
			menu.setHeaderTitle(arret.nom);
			menu.add(Menu.NONE, arretFavori == null ? R.id.ajoutFavori : R.id.supprimerFavori, 0,
					arretFavori == null ? getString(R.string.ajouterFavori) : getString(R.string.suprimerFavori));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Arret arret;
		if (item.getItemId() == R.id.ajoutFavori) {
			arret = (Arret) getListAdapter().getItem(info.position);
			Ligne myLigne = new Ligne();
			myLigne.id = arret.favori.ligneId;
			myLigne = AbstractTransportsApplication.getDataBaseHelper().selectSingle(myLigne);
			if (!myLigne.isChargee()) {
				chargerLigne(myLigne);
			}
			AbstractTransportsApplication.getDataBaseHelper().insert(arret.favori);
			return true;
		} else if (item.getItemId() == R.id.supprimerFavori) {
			arret = (Arret) getListAdapter().getItem(info.position);
			ArretFavori arretFavori = new ArretFavori();
			arretFavori.arretId = arret.id;
			arretFavori.ligneId = arret.favori.ligneId;
			arretFavori.macroDirection = arret.favori.macroDirection;
			deleteFavori(arretFavori);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	private ProgressDialog myProgressDialog;

	private void chargerLigne(final Ligne myLigne) {

		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.premierAccesLigne, myLigne.nomCourt), true);

		new AsyncTask<Void, Void, Void>() {

			private boolean erreurLigneNonTrouvee = false;
			private boolean erreurNoSpaceLeft = false;

			@Override
			protected Void doInBackground(Void... pParams) {
				try {
					UpdateDataBase.chargeDetailLigne(getRawClass(), myLigne, getResources());
				} catch (LigneInexistanteException e) {
					erreurLigneNonTrouvee = true;
				} catch (NoSpaceLeftException e) {
					erreurNoSpaceLeft = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreurLigneNonTrouvee) {
					Toast.makeText(AbstractListArretByPosition.this,
							getString(R.string.erreurLigneInconue, myLigne.nomCourt), Toast.LENGTH_LONG).show();
					finish();
				} else if (erreurNoSpaceLeft) {
					Toast.makeText(AbstractListArretByPosition.this, R.string.erreurNoSpaceLeft, Toast.LENGTH_LONG)
							.show();
					finish();
				}
			}

		}.execute((Void) null);

	}

	public void updateLocation(Location location) {
		if (location == null) {
			return;
		}
		synchronized (arrets) {
			for (Arret arret : arrets) {
				arret.calculDistance(location);
			}
			Collections.sort(arrets, new Arret.ComparatorDistance());
		}
		metterAJourListeArrets(currentQuery);
	}

}
