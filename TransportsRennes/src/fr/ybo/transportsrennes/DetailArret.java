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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.DetailArretAdapter;
import fr.ybo.transportsrennes.keolis.LigneInexistanteException;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Horaire;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.TacheAvecProgressDialog;
import fr.ybo.transportsrennes.util.UpdateTimeUtil;
import fr.ybo.transportsrennes.util.UpdateTimeUtil.UpdateTime;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends MenuAccueil.ListActivity {

	private static final double DISTANCE_RECHERCHE_METRE = 1000.0;
	private static final double DEGREE_LATITUDE_EN_METRES = 111192.62;
	private static final double DISTANCE_LAT_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LATITUDE_EN_METRES;
	private static final double DEGREE_LONGITUDE_EN_METRES = 74452.10;
	private static final double DISTANCE_LNG_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LONGITUDE_EN_METRES;
	private static final int DISTANCE_MAX_METRE = 151;

	private boolean prochainArrets = true;

	private Cursor currentCursor;

	private Calendar calendar = Calendar.getInstance();
	private Calendar calendarLaVeille = Calendar.getInstance();

	private ArretFavori favori;

	private void recuperationDonneesIntent() {
		favori = (ArretFavori) getIntent().getExtras().getSerializable("favori");
		if (favori == null) {
			favori = new ArretFavori();
			favori.arretId = getIntent().getExtras().getString("idArret");
			favori.nomArret = getIntent().getExtras().getString("nomArret");
			favori.direction = getIntent().getExtras().getString("direction");
			favori.macroDirection = getIntent().getExtras().getInt("macroDirection");
			Ligne ligne = (Ligne) getIntent().getExtras().getSerializable("ligne");
			if (ligne == null) {
				finish();
				return;
			}
			favori.ligneId = ligne.id;
			favori.nomCourt = ligne.nomCourt;
			favori.nomLong = ligne.nomLong;
		}
	}

	private void gestionViewsTitle() {
		((TextView) findViewById(R.id.nomLong)).setText(favori.nomLong);
		((ImageView) findViewById(R.id.iconeLigne)).setImageResource(IconeLigne.getIconeResource(favori.nomCourt));
		((TextView) findViewById(R.id.detailArret_nomArret)).setText(favori.nomArret + ' ' + getString(R.string.vers) + ' ' + favori.direction);
	}

	private ListAdapter construireAdapter() {
		closeCurrentCursor();
		if (prochainArrets) {
			return construireAdapterProchainsDeparts();
		}
		return construireAdapterAllDeparts();
	}

	private ListAdapter construireAdapterAllDeparts() {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		currentCursor = Horaire.getAllHorairesAsCursor(favori.ligneId, favori.arretId, favori.macroDirection, calendar);
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private ListAdapter construireAdapterProchainsDeparts() {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		currentCursor = Horaire.getProchainHorairesAsCursor(favori.ligneId, favori.arretId, favori.macroDirection,
				null, calendar);
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private Ligne myLigne;
	private LayoutInflater mInflater;

	private UpdateTimeUtil updateTimeUtil;

	private boolean firstUpdate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(this);
		calendar = Calendar.getInstance();
		calendarLaVeille = Calendar.getInstance();
		calendarLaVeille.roll(Calendar.DATE, false);
		setContentView(R.layout.detailarret);
		recuperationDonneesIntent();
		if (favori.ligneId == null) {
			return;
		}
		gestionViewsTitle();
		ImageView imageGoogleMap = (ImageView) findViewById(R.id.googlemap);
		imageGoogleMap.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Arret arret = new Arret();
				arret.id = favori.arretId;
				arret = TransportsRennesApplication.getDataBaseHelper().selectSingle(arret);
				String lat = Double.toString(arret.getLatitude());
				String lon = Double.toString(arret.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + favori.nomArret + "+@" + lat + ',' + lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException activityNotFound) {
					Toast.makeText(DetailArret.this, R.string.noGoogleMap, Toast.LENGTH_LONG).show();
				}
			}
		});
		myLigne = new Ligne();
		myLigne.id = favori.ligneId;
		myLigne = TransportsRennesApplication.getDataBaseHelper().selectSingle(myLigne);
		if (myLigne == null) {
			Toast.makeText(DetailArret.this, R.string.erreurLigneInconue, Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

			@Override
			public void update(Calendar calendar) {
				DetailArret.this.calendar = Calendar.getInstance();
				calendarLaVeille = Calendar.getInstance();
				calendarLaVeille.roll(Calendar.DATE, false);
				setListAdapter(construireAdapter());
				getListView().invalidate();
			}
		}, this);
		if (!myLigne.isChargee()) {
			chargerLigne();
		} else {
			setListAdapter(construireAdapter());
			updateTimeUtil.start();
			firstUpdate = true;
		}
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings({"unchecked"})
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Adapter arretAdapter = ((AdapterView<ListAdapter>) adapterView).getAdapter();
				Cursor cursor = (Cursor) arretAdapter.getItem(position);
				Intent intent = new Intent(DetailArret.this, DetailTrajet.class);
				intent.putExtra("trajetId", cursor.getInt(cursor.getColumnIndex("trajetId")));
				intent.putExtra("sequence", cursor.getInt(cursor.getColumnIndex("sequence")));
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);

		final ImageView correspondance = (ImageView) findViewById(R.id.imageCorrespondance);
		final LinearLayout detailCorrespondance = (LinearLayout) findViewById(R.id.detailCorrespondance);
		correspondance.setImageResource(R.drawable.arrow_right_float);
		detailCorrespondance.removeAllViews();
		detailCorrespondance.setVisibility(View.INVISIBLE);
		correspondance.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (detailCorrespondance.getVisibility() == View.VISIBLE) {
					correspondance.setImageResource(R.drawable.arrow_right_float);
					detailCorrespondance.removeAllViews();
					detailCorrespondance.setVisibility(View.INVISIBLE);
				} else {
					detailCorrespondance.setVisibility(View.VISIBLE);
					detailCorrespondance.removeAllViews();
					construireCorrespondance(detailCorrespondance);
					correspondance.setImageResource(R.drawable.arrow_down_float);
				}
			}
		});
		if (TransportsRennesApplication.hasAlert(myLigne.nomCourt)) {
			findViewById(R.id.alerte).setVisibility(View.VISIBLE);
			findViewById(R.id.alerte).setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(DetailArret.this, ListAlerts.class);
					intent.putExtra("ligne", myLigne);
					startActivity(intent);
				}
			});
		} else {
			findViewById(R.id.alerte).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		if (firstUpdate) {
			updateTimeUtil.start();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		updateTimeUtil.stop();
		super.onPause();
	}

	private void construireCorrespondance(LinearLayout detailCorrespondance) {
		/* Recuperation de l'arretCourant */
		Arret arretCourant = new Arret();
		arretCourant.id = favori.arretId;
		arretCourant = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretCourant);
		Location locationArret = new Location("myProvider");
		locationArret.setLatitude(arretCourant.latitude);
		locationArret.setLongitude(arretCourant.longitude);

		/** Construction requête. */
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT Arret.id as arretId, ArretRoute.ligneId as ligneId, Direction.direction as direction,");
		requete.append(" Arret.nom as arretNom, Arret.latitude as latitude, Arret.longitude as longitude,");
		requete.append(" Ligne.nomCourt as nomCourt, Ligne.nomLong as nomLong, ArretRoute.macroDirection as macroDirection ");
		requete.append("FROM Arret, ArretRoute, Direction, Ligne ");
		requete.append("WHERE Arret.id = ArretRoute.arretId and Direction.id = ArretRoute.directionId AND Ligne.id = ArretRoute.ligneId");
		requete.append(" AND Arret.latitude > :minLatitude AND Arret.latitude < :maxLatitude");
		requete.append(" AND Arret.longitude > :minLongitude AND Arret.longitude < :maxLongitude");

		/** Paramètres de la requête */
		double minLatitude = arretCourant.latitude - DISTANCE_LAT_IN_DEGREE;
		double maxLatitude = arretCourant.latitude + DISTANCE_LAT_IN_DEGREE;
		double minLongitude = arretCourant.longitude - DISTANCE_LNG_IN_DEGREE;
		double maxLongitude = arretCourant.longitude + DISTANCE_LNG_IN_DEGREE;
		List<String> selectionArgs = new ArrayList<String>(4);
		selectionArgs.add(String.valueOf(minLatitude));
		selectionArgs.add(String.valueOf(maxLatitude));
		selectionArgs.add(String.valueOf(minLongitude));
		selectionArgs.add(String.valueOf(maxLongitude));

		Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);

		/** Recuperation des index dans le cussor */
		int arretIdIndex = cursor.getColumnIndex("arretId");
		int ligneIdIndex = cursor.getColumnIndex("ligneId");
		int directionIndex = cursor.getColumnIndex("direction");
		int arretNomIndex = cursor.getColumnIndex("arretNom");
		int latitudeIndex = cursor.getColumnIndex("latitude");
		int longitudeIndex = cursor.getColumnIndex("longitude");
		int nomCourtIndex = cursor.getColumnIndex("nomCourt");
		int nomLongIndex = cursor.getColumnIndex("nomLong");
		int macroDirectionIndex = cursor.getColumnIndex("macroDirection");

		List<Arret> arrets = new ArrayList<Arret>(20);

		while (cursor.moveToNext()) {
			Arret arret = new Arret();
			arret.id = cursor.getString(arretIdIndex);
			arret.favori = new ArretFavori();
			arret.favori.arretId = arret.id;
			arret.favori.ligneId = cursor.getString(ligneIdIndex);
			arret.favori.direction = cursor.getString(directionIndex);
			arret.nom = cursor.getString(arretNomIndex);
			arret.favori.nomArret = arret.nom;
			arret.latitude = cursor.getDouble(latitudeIndex);
			arret.longitude = cursor.getDouble(longitudeIndex);
			arret.favori.nomCourt = cursor.getString(nomCourtIndex);
			arret.favori.nomLong = cursor.getString(nomLongIndex);
			arret.favori.macroDirection = cursor.getInt(macroDirectionIndex);
			if (!arret.id.equals(favori.arretId) || !arret.favori.ligneId.equals(favori.ligneId)) {
				arret.calculDistance(locationArret);
				if (arret.distance < DISTANCE_MAX_METRE) {
					arrets.add(arret);
				}
			}
		}
		cursor.close();

		Collections.sort(arrets, new Arret.ComparatorDistance());

		for (final Arret arret : arrets) {
			RelativeLayout relativeLayout = (RelativeLayout) mInflater.inflate(R.layout.arretgps, null);
			ImageView iconeLigne = (ImageView) relativeLayout.findViewById(R.id.iconeLigne);
			iconeLigne.setImageResource(IconeLigne.getIconeResource(arret.favori.nomCourt));
			TextView arretDirection = (TextView) relativeLayout.findViewById(R.id.arretgps_direction);
			arretDirection.setText(arret.favori.direction);
			TextView nomArret = (TextView) relativeLayout.findViewById(R.id.arretgps_nomArret);
			nomArret.setText(arret.nom);
			TextView distance = (TextView) relativeLayout.findViewById(R.id.arretgps_distance);
			distance.setText(arret.formatDistance());
			relativeLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(DetailArret.this, DetailArret.class);
					intent.putExtra("favori", arret.favori);
					startActivity(intent);
				}
			});
			detailCorrespondance.addView(relativeLayout);
		}
	}

	private void chargerLigne() {
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.premierAccesLigne, myLigne.nomCourt)) {

			private boolean erreurLigneNonTrouvee = false;

			@Override
			protected Void myDoBackground(Void... pParams) {
				try {
					UpdateDataBase.chargeDetailLigne(myLigne, getResources());
				} catch (LigneInexistanteException e) {
					erreurLigneNonTrouvee = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (erreurLigneNonTrouvee) {
					Toast.makeText(DetailArret.this, getString(R.string.erreurLigneInconue, myLigne.nomCourt),
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					setListAdapter(construireAdapter());
					getListView().invalidate();
					updateTimeUtil.start();
					firstUpdate = true;
				}
			}

		}.execute();

	}

	private void closeCurrentCursor() {
		if (currentCursor != null && !currentCursor.isClosed()) {
			currentCursor.close();
		}
	}

	@Override
	protected void onDestroy() {
		closeCurrentCursor();
		super.onDestroy();
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ALL_STOPS = Menu.FIRST;
	private static final int MENU_SELECT_DAY = MENU_ALL_STOPS + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(GROUP_ID, MENU_ALL_STOPS, Menu.NONE, R.string.menu_prochainArrets).setIcon(android.R.drawable.ic_menu_view);
		menu.add(GROUP_ID, MENU_SELECT_DAY, Menu.NONE, R.string.menu_selectDay).setIcon(android.R.drawable.ic_menu_month);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(MENU_ALL_STOPS).setTitle(prochainArrets ? R.string.menu_allArrets : R.string.menu_prochainArrets);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case MENU_ALL_STOPS:
				prochainArrets = !prochainArrets;
				setListAdapter(construireAdapter());
				getListView().invalidate();
				return true;
			case MENU_SELECT_DAY:
				showDialog(DATE_DIALOG_ID);
				return true;
		}
		return false;
	}

	private static final int DATE_DIALOG_ID = 0;

	private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendarLaVeille.set(Calendar.YEAR, year);
			calendarLaVeille.set(Calendar.MONTH, monthOfYear);
			calendarLaVeille.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendarLaVeille.roll(Calendar.DATE, false);
			setListAdapter(construireAdapter());
			getListView().invalidate();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DATE_DIALOG_ID) {
			return new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

}
