/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.DetailArretAdapter;
import fr.ybo.transportsrennes.adapters.DetailTrajetAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.JoursFeries;
import fr.ybo.transportsrennes.util.LogYbo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(DetailArret.class);
	private final static Class<?> classDrawable = R.drawable.class;

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
			Ligne myLigne = (Ligne) getIntent().getExtras().getSerializable("ligne");
			favori.ligneId = myLigne.id;
			favori.nomCourt = myLigne.nomCourt;
			favori.nomLong = myLigne.nomLong;
		}
	}

	private void gestionViewsTitle() {
		LinearLayout conteneur = (LinearLayout) findViewById(R.id.conteneurImage);
		TextView nomLong = (TextView) findViewById(R.id.nomLong);
		nomLong.setText(favori.nomLong);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(getApplicationContext());
			imgView.setImageResource(ressourceImg);
			conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(getApplicationContext());
			textView.setTextSize(16);
			textView.setText(favori.nomCourt);
			conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(getApplicationContext());
			textView.setTextSize(16);
			textView.setText(favori.nomCourt);
			conteneur.addView(textView);
		}
		((TextView) findViewById(R.id.detailArret_nomArret)).setText(favori.nomArret + " vers " + favori.direction);
	}

	private DetailArretAdapter construireAdapter() {
		closeCurrentCursor();
		if (prochainArrets) {
			return construireAdapterProchainsDeparts();
		}
		return construireAdapterAllDeparts();
	}

	private DetailArretAdapter construireAdapterAllDeparts() {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		StringBuilder requete = new StringBuilder();
		requete.append("select Horaire.heureDepart as _id, Trajet.id as trajetId ");
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
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		LOG_YBO.debug("Exécution de la requête permettant de récupérer tous les horaires des arrêts.");
		long startTime = System.currentTimeMillis();
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		long elapsedTime = System.currentTimeMillis() - startTime;
		LOG_YBO.debug("Exécution de la requête permettant de récupérer les arrêts terminée : " + currentCursor.getCount() + " en " + elapsedTime + "ms");
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private DetailArretAdapter construireAdapterProchainsDeparts() {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		StringBuilder requete = new StringBuilder();
		requete.append("select (Horaire.heureDepart - :uneJournee) as _id, Trajet.id as trajetId ");
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
		requete.append("select Horaire.heureDepart as _id, Trajet.id as trajetId ");
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
		List<String> selectionArgs = new ArrayList<String>();
		int uneJournee = 24 * 60;
		selectionArgs.add(Integer.toString(uneJournee));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now + (uneJournee)));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now));
		LOG_YBO.debug("Exécution de la requête permettant de récupérer les arrêts avec les temps avant les prochains bus");
		long startTime = System.currentTimeMillis();
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		long elapsedTime = System.currentTimeMillis() - startTime;
		LOG_YBO.debug("Exécution de la requête permettant de récupérer les arrêts terminée : " + currentCursor.getCount() + " en " + elapsedTime + "ms");
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private Ligne myLigne;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendar = Calendar.getInstance();
		calendarLaVeille = Calendar.getInstance();
		calendarLaVeille.roll(Calendar.DATE, false);
		setContentView(R.layout.detailarret);
		recuperationDonneesIntent();
		gestionViewsTitle();
		ImageView imageGoogleMap = (ImageView) findViewById(R.id.googlemap);
		imageGoogleMap.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Arret arret = new Arret();
				arret.id = favori.arretId;
				arret = TransportsRennesApplication.getDataBaseHelper().selectSingle(arret);
				String _lat = Double.toString(arret.getLatitude());
				String _lon = Double.toString(arret.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + favori.nomArret + "+@" + _lat + "," + _lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException noGoogleMapsException) {
					LOG_YBO.erreur("Google maps de doit pas être présent", noGoogleMapsException);
					Toast.makeText(getApplicationContext(), "Vous n'avez pas GoogleMaps d'installé...", Toast.LENGTH_LONG).show();
				}
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
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				DetailArretAdapter arretAdapter = (DetailArretAdapter) ((ListView) adapterView).getAdapter();
				Cursor cursor = (Cursor) arretAdapter.getItem(position);
				Intent intent = new Intent(DetailArret.this, DetailTrajet.class);
				intent.putExtra("trajetId", cursor.getString(cursor.getColumnIndex("trajetId")));
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
	}

	private ProgressDialog myProgressDialog;

	private void chargerLigne() {
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog
						.show(DetailArret.this, "", "Premier accès aux horaires de la ligne " + myLigne.nomCourt + ", chargement des données...",
								true);
			}

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.chargeDetailLigne(myLigne);
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsRennes.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				if (erreur) {
					Toast.makeText(DetailArret.this,
							"Une erreur est survenue lors de la récupération des données du STAR, réessayez plus tard, si cela persiste, envoyer un mail au développeur...",
							Toast.LENGTH_LONG).show();
					DetailArret.this.finish();
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

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendarLaVeille.set(Calendar.YEAR, year);
			calendarLaVeille.set(Calendar.MONTH, monthOfYear);
			calendarLaVeille.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendarLaVeille.roll(Calendar.DATE, false);
			LOG_YBO.debug("Date choisie : " + calendar.getTime().toString());
			setListAdapter(construireAdapter());
			getListView().invalidate();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

}
