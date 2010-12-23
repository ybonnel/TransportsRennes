package fr.ybo.transportsrennes;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.Formatteur;
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

	private Cursor currentCursor;

	private String clauseWhereForTodayCalendrier() {
		final Calendar calendar = Calendar.getInstance();
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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailarret);
		ArretFavori favori = (ArretFavori) getIntent().getExtras().getSerializable("favori");
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
		((TextView) findViewById(R.id.detailArret_direction))
				.setText(Formatteur.formatterChaine(favori.getDirection().replaceAll(favori.getRouteNomCourt(), "")));
		((TextView) findViewById(R.id.detailArret_nomArret)).setText(favori.getNomArret());
		final DataBaseHelper dataBaseHelper = BusRennesApplication.getDataBaseHelper();

		final Calendar calendar = Calendar.getInstance();

		final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		final StringBuilder requete = new StringBuilder();
		requete.append("select HeuresArrets.heureDepart as _id ");
		requete.append("from Calendrier,  HeuresArrets");
		requete.append(Route.getIdWithoutSpecCar(favori.getRouteId()));
		requete.append(" as HeuresArrets ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier());
		requete.append(" and HeuresArrets.serviceId = Calendrier.id");
		requete.append(" and HeuresArrets.routeId = :routeId");
		requete.append(" and HeuresArrets.stopId = :arretId");
		requete.append(" and HeuresArrets.heureDepart >= :maintenant");
		requete.append(" order by HeuresArrets.heureDepart limit 10;");
		final List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(favori.getRouteId());
		selectionArgs.add(favori.getStopId());
		selectionArgs.add(Long.toString(now));
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts avec les temps avant les prochains bus");
		LOG_YBO.debug(requete.toString());
		LOG_YBO.debug(selectionArgs.toString());
		currentCursor = dataBaseHelper.executeSelectQuery(requete.toString(), selectionArgs);
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts terminée : " + currentCursor.getCount());

		setListAdapter(new DetailArretAdapter(getApplicationContext(), currentCursor, now));
		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);
	}

	@Override
	protected void onDestroy() {
		if (currentCursor != null && !currentCursor.isClosed()) {
			currentCursor.close();
		}
		super.onDestroy();
	}

}
