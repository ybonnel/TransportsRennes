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

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.JoursFeries;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(DetailArret.class);
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

	private String clauseWhereForTodayCalendrier(Calendar calendar) {
		if (JoursFeries.isJourFerie(calendar.getTime())) {
			return "Dimanche = 1";
		}
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				return "Lundi = 1";
			case Calendar.TUESDAY:
				return "Mardi = 1";
			case Calendar.WEDNESDAY:
				return "Mercredi = 1";
			case Calendar.THURSDAY:
				return "Jeudi = 1";
			case Calendar.FRIDAY:
				return "Vendredi = 1";
			case Calendar.SATURDAY:
				return "Samedi = 1";
			case Calendar.SUNDAY:
				return "Dimanche = 1";
			default:
				return null;
		}
	}

	private ArretFavori favori;

	private void recuperationDonneesIntent() {
		favori = (ArretFavori) getIntent().getExtras().getSerializable("favori");
		if (favori == null) {
			favori = new ArretFavori();
			favori.arretId = getIntent().getExtras().getString("idArret");
			favori.nomArret = getIntent().getExtras().getString("nomArret");
			favori.direction = getIntent().getExtras().getString("direction");
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
		StringBuilder requete = new StringBuilder();
		requete.append("select Horaire.heureDepart as _id,");
		requete.append(" Trajet.id as trajetId, stopSequence as sequence ");
		requete.append("from Calendrier,  Horaire_");
		requete.append(favori.ligneId);
		requete.append(" as Horaire, Trajet ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendar));
		requete.append(" and Trajet.calendrierId = Calendrier.id");
		requete.append(" and Trajet.id = Horaire.trajetId");
		requete.append(" and Trajet.ligneId = :ligneId");
		requete.append(" and Horaire.arretId = :arretId");
		requete.append(" and Horaire.terminus = 0");
		requete.append(" order by Horaire.heureDepart;");
		List<String> selectionArgs = new ArrayList<String>(2);
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		LOG_YBO.debug("Exécution de la requête permettant de récupérer tous les horaires des arrêts.");
		long startTime = System.currentTimeMillis();
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		long elapsedTime = System.currentTimeMillis() - startTime;
		LOG_YBO.debug(
				"Exécution de la requête permettant de récupérer les arrêts terminée : " + currentCursor.getCount() + " en " + elapsedTime + "ms");
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private ListAdapter construireAdapterProchainsDeparts() {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		StringBuilder requete = new StringBuilder();
		requete.append("select (Horaire.heureDepart - :uneJournee) as _id,");
		requete.append(" Trajet.id as trajetId, stopSequence as sequence ");
		requete.append("from Calendrier,  Horaire_");
		requete.append(favori.ligneId);
		requete.append(" as Horaire, Trajet ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendarLaVeille));
		requete.append(" and Trajet.id = Horaire.trajetId");
		requete.append(" and Trajet.calendrierId = Calendrier.id");
		requete.append(" and Trajet.ligneId = :routeId1");
		requete.append(" and Horaire.arretId = :arretId1");
		requete.append(" and Horaire.heureDepart >= :maintenantHier ");
		requete.append(" and Horaire.terminus = 0 ");
		requete.append("UNION ");
		requete.append("select Horaire.heureDepart as _id,");
		requete.append(" Trajet.id as trajetId, stopSequence as sequence ");
		requete.append("from Calendrier,  Horaire_");
		requete.append(favori.ligneId);
		requete.append(" as Horaire, Trajet ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendar));
		requete.append(" and Trajet.id = Horaire.trajetId");
		requete.append(" and Trajet.calendrierId = Calendrier.id");
		requete.append(" and Trajet.ligneId = :routeId2");
		requete.append(" and Horaire.arretId = :arretId2");
		requete.append(" and Horaire.heureDepart >= :maintenant");
		requete.append(" and Horaire.terminus = 0");
		requete.append(" order by _id;");
		List<String> selectionArgs = new ArrayList<String>(7);
		int uneJournee = 24 * 60;
		selectionArgs.add(Integer.toString(uneJournee));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now + uneJournee));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now));
		LOG_YBO.debug("Exécution de la requête permettant de récupérer les arrêts avec les temps avant les prochains bus");
		long startTime = System.currentTimeMillis();
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		long elapsedTime = System.currentTimeMillis() - startTime;
		LOG_YBO.debug(
				"Exécution de la requête permettant de récupérer les arrêts terminée : " + currentCursor.getCount() + " en " + elapsedTime + "ms");
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private Ligne myLigne;
	private LayoutInflater mInflater;

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
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});
		myLigne = new Ligne();
		myLigne.id = favori.ligneId;
		myLigne = TransportsRennesApplication.getDataBaseHelper().selectSingle(myLigne);
		if (myLigne.chargee == null || !myLigne.chargee) {
			chargerLigne();
		} else {
			setListAdapter(construireAdapter());
		}
		ListView lv = getListView();
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
		requete.append(
				" Arret.nom as arretNom, Arret.latitude as latitude, Arret.longitude as longitude, Ligne.nomCourt as nomCourt, Ligne.nomLong as nomLong ");
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

	private ProgressDialog myProgressDialog;

	private void chargerLigne() {
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog.show(DetailArret.this, "", getString(R.string.premierAccesLigne, myLigne.nomCourt), true);
			}

			@Override
			protected Void doInBackground(Void... pParams) {
				try {
					UpdateDataBase.chargeDetailLigne(myLigne, getResources());
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsRennes.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (erreur) {
					Toast.makeText(DetailArret.this, getString(R.string.erreur_chargementStar), Toast.LENGTH_LONG).show();
					finish();
				} else {
					setListAdapter(construireAdapter());
					getListView().invalidate();
				}
				myProgressDialog.dismiss();
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
