package fr.ybo.transportsrennes;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import fr.ybo.transportsrennes.adapters.ArretAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.LogYbo;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Liste des arrêts d'une ligne de bus.
 *
 * @author ybonnel
 */
public class ListArret extends ListActivity {

	private final static Class<?> classDrawable = R.drawable.class;

	private final static LogYbo LOG_YBO = new LogYbo(ListArret.class);

	private ProgressDialog myProgressDialog;

	private Route myRoute;

	private Cursor currentCursor;

	private void closeCurrentCursor() {
		if (currentCursor != null && !currentCursor.isClosed()) {
			currentCursor.close();
		}
	}

	private String currentDirection = null;

	public void onDirectionClick() {
		final Map<String, String> mapDirections = new HashMap<String, String>();
		ArretRoute arretRouteRef = new ArretRoute();
		arretRouteRef.setRouteId(myRoute.getId());
		for (ArretRoute arretRoute : TransportsRennesApplication.getDataBaseHelper().select(arretRouteRef)) {
			String directionFormattee = Formatteur.formatterChaine(arretRoute.getDirection());
			if (!mapDirections.containsKey(directionFormattee)) {
				mapDirections.put(directionFormattee, arretRoute.getDirection());
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choisissez une direction");
		final List<String> items = new ArrayList<String>();
		items.add("Toutes");
		items.addAll(mapDirections.keySet());
		Collections.sort(items, new Comparator<String>() {
			public int compare(String o1, String o2) {
				if ("Toutes".equals(o1)) {
					return -1;
				}
				if ("Toutes".equals(o2)) {
					return 1;
				}
				return o1.compareToIgnoreCase(o2);
			}
		});
		builder.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int item) {
				currentDirection = mapDirections.get(items.get(item));
				construireListe();
				getListView().invalidate();
				dialogInterface.dismiss();
			}
		});
		builder.create().show();
	}

	private void ajoutFavori(final Cursor cursor) throws DataBaseException {
		myRoute = TransportsRennesApplication.getDataBaseHelper().selectSingle(myRoute);
		if (myRoute.getChargee() == null || !myRoute.getChargee()) {
			chargerRoute();
		}
		ArretFavori arretFavori = new ArretFavori();
		arretFavori.setStopId(cursor.getString(cursor.getColumnIndex("_id")));
		arretFavori.setNomArret(cursor.getString(cursor.getColumnIndex("arretName")));
		arretFavori.setDirection(cursor.getString(cursor.getColumnIndex("direction")));
		arretFavori.setRouteId(myRoute.getId());
		arretFavori.setRouteNomCourt(myRoute.getNomCourt());
		arretFavori.setRouteNomLong(myRoute.getNomLong());
		LOG_YBO.debug("Ajout du favori " + arretFavori.getStopId());
		TransportsRennesApplication.getDataBaseHelper().insert(arretFavori);
	}

	private void chargerRoute() {

		myProgressDialog = ProgressDialog.show(this, "", "Premier accès à la ligne " + myRoute.getNomCourt() + ", chargement des données...", true);

		new AsyncTask<Void, Void, Void>() {

			boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.chargeDetailRoute(myRoute);
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur lors du chargement du détail de la route", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(ListArret.this,
							"Une erreur est survenue lors de la récupération des données du STAR, réessayez plus tard, si cela persiste, envoyer un mail au développeur...",
							Toast.LENGTH_LONG).show();
					ListArret.this.finish();
				}
			}

		}.execute();

	}

	private void construireCursor() {
		closeCurrentCursor();
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(myRoute.getId());
		StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as _id, Arret.nom as arretName,");
		requete.append(" ArretRoute.direction as direction ");
		requete.append("from ArretRoute, Arret ");
		requete.append("where");
		requete.append(" ArretRoute.routeId = :routeId");
		requete.append(" and ArretRoute.arretId = Arret.id");
		if (currentDirection != null) {
			requete.append(" and ArretRoute.direction = :direction");
			selectionArgs.add(currentDirection);
		}
		requete.append(" order by ArretRoute.direction, ");
		if (orderDirection) {
			requete.append("ArretRoute.sequence");
		} else {
			requete.append("Arret.nom");
		}
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts avec le temps avant le prochain");
		LOG_YBO.debug(requete.toString());
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts terminée : " + currentCursor.getCount());
	}

	private void construireListe() {
		construireCursor();
		setListAdapter(new ArretAdapter(getApplicationContext(), currentCursor, myRoute));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final ArretAdapter arretAdapter = (ArretAdapter) ((ListView) adapterView).getAdapter();
				final Cursor cursor = (Cursor) arretAdapter.getItem(position);
				final Intent intent = new Intent(ListArret.this, DetailArret.class);
				intent.putExtra("idArret", cursor.getString(cursor.getColumnIndex("_id")));
				intent.putExtra("nomArret", cursor.getString(cursor.getColumnIndex("arretName")));
				intent.putExtra("direction", cursor.getString(cursor.getColumnIndex("direction")));
				intent.putExtra("route", myRoute);
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.ajoutFavori:
				ajoutFavori((Cursor) getListAdapter().getItem(info.position));
				return true;
			case R.id.supprimerFavori:
				supprimeFavori((Cursor) getListAdapter().getItem(info.position));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listearrets);
		myRoute = (Route) getIntent().getExtras().getSerializable("route");
		((TextView) findViewById(R.id.directionArretEntete)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ListArret.this.onDirectionClick();
			}
		});
		LinearLayout conteneur = (LinearLayout) findViewById(R.id.conteneurImage);
		TextView nomLong = (TextView) findViewById(R.id.nomLong);
		nomLong.setText(myRoute.getNomLongFormate());
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + myRoute.getNomCourt().toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(getApplicationContext());
			imgView.setImageResource(ressourceImg);
			conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(getApplicationContext());
			textView.setTextSize(16);
			textView.setText(myRoute.getNomCourt());
			conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(getApplicationContext());
			textView.setTextSize(16);
			textView.setText(myRoute.getNomCourt());
			conteneur.addView(textView);
		}
		final Route routeTmp = new Route();
		routeTmp.setId(myRoute.getId());
		construireListe();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
			String idArret = cursor.getString(cursor.getColumnIndex("_id"));
			String nomArret = cursor.getString(cursor.getColumnIndex("arretName"));
			ArretFavori arretFavori = new ArretFavori();
			arretFavori.setStopId(idArret);
			arretFavori.setRouteId(myRoute.getId());
			arretFavori = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretFavori);
			menu.setHeaderTitle(nomArret);
			menu.add(Menu.NONE, arretFavori == null ? R.id.ajoutFavori : R.id.supprimerFavori, 0,
					arretFavori == null ? "Ajouter aux favoris" : "Supprimer des favoris");
		}
	}

	@Override
	protected void onDestroy() {
		closeCurrentCursor();
		super.onPause();
	}

	private void supprimeFavori(final Cursor cursor) throws DataBaseException {
		final ArretFavori arretFavori = new ArretFavori();
		arretFavori.setStopId(cursor.getString(cursor.getColumnIndex("_id")));
		arretFavori.setRouteId(myRoute.getId());
		LOG_YBO.debug("Suppression du favori " + arretFavori.getStopId());
		TransportsRennesApplication.getDataBaseHelper().delete(arretFavori);
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ORDER = Menu.FIRST;

	private boolean orderDirection = true;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(GROUP_ID, MENU_ORDER, Menu.NONE, R.string.menu_orderByName);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(MENU_ORDER).setTitle(orderDirection ? R.string.menu_orderByName : R.string.menu_orderBySequence);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case MENU_ORDER:
				orderDirection = !orderDirection;
				construireListe();
				getListView().invalidate();
				return true;
		}
		return false;
	}
}