package fr.ybo.transportsrennes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.ArretAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.LogYbo;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Liste des arrêts d'une ligne de bus.
 *
 * @author ybonnel
 */
public class ListArret extends MenuAccueil.ListActivity {

	private final static Class<?> classDrawable = R.drawable.class;

	private final static LogYbo LOG_YBO = new LogYbo(ListArret.class);

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
			mapDirections.put(arretRoute.getDirection(), arretRoute.getDirection());
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
				((TextView) findViewById(R.id.directionArretCourante)).setText(currentDirection == null ? "Toutes" : currentDirection);
				findViewById(R.id.directionArretCouranteScroll).invalidate();
				getListView().invalidate();
				dialogInterface.dismiss();
			}
		});
		builder.create().show();
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
		setListAdapter(new ArretAdapter(this, currentCursor, myRoute));
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
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listearrets);
		myRoute = (Route) getIntent().getExtras().getSerializable("route");
		findViewById(R.id.directionArretCourante).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ListArret.this.onDirectionClick();
			}
		});
		findViewById(R.id.googlemap).setOnClickListener(new View.OnClickListener(){
			public void onClick(View view) {
				Intent intent = new Intent(ListArret.this, ArretsOnMap.class);
				intent.putExtra("route", myRoute);
				startActivity(intent);
			}
		});
		LinearLayout conteneur = (LinearLayout) findViewById(R.id.conteneurImage);
		TextView nomLong = (TextView) findViewById(R.id.nomLong);
		nomLong.setText(myRoute.getNomLong());
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
	protected void onDestroy() {
		closeCurrentCursor();
		super.onPause();
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
		menu.findItem(MENU_ORDER).setIcon(orderDirection ? android.R.drawable.ic_menu_sort_alphabetically : android.R.drawable.ic_menu_sort_by_size);
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