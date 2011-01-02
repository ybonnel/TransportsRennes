package fr.ybo.transportsrennes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
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
import android.widget.*;
import fr.ybo.transportsrennes.adapters.DetailArretAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.Formatteur;
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
public class DetailArret extends ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(DetailArret.class);
	private final static Class<?> classDrawable = R.drawable.class;

	private boolean prochainArrets = true;

	private Cursor currentCursor;

	private Calendar calendar = Calendar.getInstance();

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
			favori.setStopId(getIntent().getExtras().getString("idArret"));
			favori.setNomArret(getIntent().getExtras().getString("nomArret"));
			favori.setDirection(getIntent().getExtras().getString("direction"));
			final Route myRoute = (Route) getIntent().getExtras().getSerializable("route");
			favori.setRouteId(myRoute.getId());
			favori.setRouteNomCourt(myRoute.getNomCourt());
			favori.setRouteNomLong(myRoute.getNomLong());
		}
	}

	private void gestionViewsTitle() {
		LinearLayout conteneur = (LinearLayout) findViewById(R.id.conteneurImage);
		TextView nomLong = (TextView) findViewById(R.id.nomLong);
		nomLong.setText(Formatteur.formatterChaine(favori.getRouteNomLong()));
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori.getRouteNomCourt().toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(getApplicationContext());
			imgView.setImageResource(ressourceImg);
			conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(getApplicationContext());
			textView.setTextSize(16);
			textView.setText(favori.getRouteNomCourt());
			conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(getApplicationContext());
			textView.setTextSize(16);
			textView.setText(favori.getRouteNomCourt());
			conteneur.addView(textView);
		}
		((TextView) findViewById(R.id.detailArret_nomArret)).setText(
				favori.getNomArret() + " vers " + Formatteur.formatterChaine(favori.getDirection().replaceAll(favori.getRouteNomCourt(), "")));
	}

	private DetailArretAdapter construireAdapter(Calendar calendar) {
		closeCurrentCursor();
		if (prochainArrets) {
			return construireAdapterProchainsDeparts(calendar);
		}
		return construireAdapterAllDeparts(calendar);
	}

	private DetailArretAdapter construireAdapterAllDeparts(Calendar calendar) {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		StringBuilder requete = new StringBuilder();
		requete.append("select HeuresArrets.heureDepart as _id ");
		requete.append("from Calendrier,  HeuresArrets");
		requete.append(Route.getIdWithoutSpecCar(favori.getRouteId()));
		requete.append(" as HeuresArrets ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendar));
		requete.append(" and HeuresArrets.serviceId = Calendrier.id");
		requete.append(" and HeuresArrets.routeId = :routeId");
		requete.append(" and HeuresArrets.stopId = :arretId");
		requete.append(" order by HeuresArrets.heureDepart;");
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(favori.getRouteId());
		selectionArgs.add(favori.getStopId());
		LOG_YBO.debug("Exécution de la requête permettant de récupérer tous les horaires des arrêts.");
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		LOG_YBO.debug("Exécution de la requête permettant de récupérer tous les horaires des arrêts terminée : " + currentCursor.getCount());
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private DetailArretAdapter construireAdapterProchainsDeparts(Calendar calendar) {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		StringBuilder requete = new StringBuilder();
		requete.append("select HeuresArrets.heureDepart as _id ");
		requete.append("from Calendrier,  HeuresArrets");
		requete.append(Route.getIdWithoutSpecCar(favori.getRouteId()));
		requete.append(" as HeuresArrets ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendar));
		requete.append(" and HeuresArrets.serviceId = Calendrier.id");
		requete.append(" and HeuresArrets.routeId = :routeId");
		requete.append(" and HeuresArrets.stopId = :arretId");
		requete.append(" and HeuresArrets.heureDepart >= :maintenant");
		requete.append(" order by HeuresArrets.heureDepart;");
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(favori.getRouteId());
		selectionArgs.add(favori.getStopId());
		selectionArgs.add(Long.toString(now));
		LOG_YBO.debug("Exécution de la requête permettant de récupérer les arrêts avec les temps avant les prochains bus");
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		LOG_YBO.debug("Exécution de la requête permettant de récupérer les arrêts terminée : " + currentCursor.getCount());
		return new DetailArretAdapter(getApplicationContext(), currentCursor, now);
	}

	private Route myRoute;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendar = Calendar.getInstance();
		setContentView(R.layout.detailarret);
		recuperationDonneesIntent();
		gestionViewsTitle();
		ImageView imageGoogleMap = (ImageView) findViewById(R.id.googlemap);
		imageGoogleMap.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Arret arret = new Arret();
				arret.setId(favori.getStopId());
				arret = TransportsRennesApplication.getDataBaseHelper().selectSingle(arret);
				String _lat = Double.toString(arret.getLatitude());
				String _lon = Double.toString(arret.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(favori.getNomArret()) + "+@" + _lat + "," + _lon);
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException noGoogleMapsException) {
					LOG_YBO.erreur("Google maps de doit pas être présent", noGoogleMapsException);
					Toast.makeText(getApplicationContext(), "Vous n'avez pas GoogleMaps d'installé...", Toast.LENGTH_LONG).show();
				}
			}
		});
		myRoute = new Route();
		myRoute.setId(favori.getRouteId());
		myRoute = TransportsRennesApplication.getDataBaseHelper().selectSingle(myRoute);
		if (myRoute.getChargee() == null || !myRoute.getChargee()) {
			chargerRoute();
		} else {
			setListAdapter(construireAdapter(calendar));
		}
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
	}

	private ProgressDialog myProgressDialog;

	private void chargerRoute() {
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog
						.show(DetailArret.this, "", "Premier accès aux horaires de la ligne " + myRoute.getNomCourt() + ", chargement des données...",
								true);
			}

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.chargeDetailRoute(myRoute);
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
							"Une erreur est survenue lors de la récupération des données de la Star, réessayez plus tard, si cela persiste, envoyer un mail au développeur...",
							Toast.LENGTH_LONG).show();
				} else {
					setListAdapter(construireAdapter(calendar));
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
		menu.add(GROUP_ID, MENU_ALL_STOPS, Menu.NONE, R.string.menu_prochainArrets);
		menu.add(GROUP_ID, MENU_SELECT_DAY, Menu.NONE, R.string.menu_selectDay);
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
				setListAdapter(construireAdapter(calendar));
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
			LOG_YBO.debug("Date choisie : " + calendar.getTime().toString());
			setListAdapter(construireAdapter(calendar));
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
