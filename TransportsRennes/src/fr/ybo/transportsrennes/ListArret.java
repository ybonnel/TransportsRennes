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

import android.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.ArretAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Liste des arrêts d'une ligne de bus.
 *
 * @author ybonnel
 */
public class ListArret extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(ListArret.class);

	private Ligne myLigne;

	private Cursor currentCursor;

	private void closeCurrentCursor() {
		if (currentCursor != null && !currentCursor.isClosed()) {
			currentCursor.close();
		}
	}

	private String currentDirection;

	void onDirectionClick() {
		final StringBuilder requete = new StringBuilder();
		requete.append("SELECT Direction.id as directionId, Direction.direction as direction ");
		requete.append("FROM Direction, ArretRoute ");
		requete.append("WHERE Direction.id = ArretRoute.directionId");
		requete.append(" AND ArretRoute.ligneId = :ligneId ");
		requete.append("GROUP BY Direction.id, Direction.direction");
		final Cursor cursor =
				TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), Collections.singletonList(myLigne.id));
		final int directionIndex = cursor.getColumnIndex("direction");
		final List<String> items = new ArrayList<String>(5);
		while (cursor.moveToNext()) {
			items.add(cursor.getString(directionIndex));
		}
		cursor.close();
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(fr.ybo.transportsrennes.R.string.chooseDirection));
		final String toutes = getString(fr.ybo.transportsrennes.R.string.Toutes);
		items.add(toutes);
		Collections.sort(items, new Comparator<String>() {
			public int compare(final String o1, final String o2) {
				if (toutes.equals(o1)) {
					return -1;
				}
				if (toutes.equals(o2)) {
					return 1;
				}
				return o1.compareToIgnoreCase(o2);
			}
		});
		builder.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialogInterface, final int item) {
				currentDirection = items.get(item).equals(toutes) ? null : items.get(item);
				construireListe();
				((TextView) findViewById(fr.ybo.transportsrennes.R.id.directionArretCourante)).setText(items.get(item));
				findViewById(fr.ybo.transportsrennes.R.id.directionArretCouranteScroll).invalidate();
				getListView().invalidate();
				dialogInterface.dismiss();
			}
		});
		builder.create().show();
	}

	private void construireCursor() {
		closeCurrentCursor();
		final List<String> selectionArgs = new ArrayList<String>(2);
		selectionArgs.add(myLigne.id);
		final StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as _id, Arret.nom as arretName,");
		requete.append(" Direction.direction as direction, ArretRoute.accessible as accessible ");
		requete.append("from ArretRoute, Arret, Direction ");
		requete.append("where");
		requete.append(" ArretRoute.ligneId = :ligneId");
		requete.append(" and ArretRoute.arretId = Arret.id");
		requete.append(" and Direction.id = ArretRoute.directionId");
		if (currentDirection != null) {
			requete.append(" and Direction.direction = :direction");
			selectionArgs.add(currentDirection);
		}
		requete.append(" order by Direction.direction, ");
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
		setListAdapter(new ArretAdapter(this, currentCursor, myLigne));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final Adapter arretAdapter = (ArretAdapter) ((AdapterView<ListAdapter>) adapterView).getAdapter();
				final Cursor cursor = (Cursor) arretAdapter.getItem(position);
				final Intent intent = new Intent(ListArret.this, DetailArret.class);
				intent.putExtra("idArret", cursor.getString(cursor.getColumnIndex("_id")));
				intent.putExtra("nomArret", cursor.getString(cursor.getColumnIndex("arretName")));
				intent.putExtra("direction", cursor.getString(cursor.getColumnIndex("direction")));
				intent.putExtra("ligne", myLigne);
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(fr.ybo.transportsrennes.R.layout.listearrets);
		myLigne = (Ligne) getIntent().getExtras().getSerializable("ligne");
		findViewById(fr.ybo.transportsrennes.R.id.directionArretCourante).setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onDirectionClick();
			}
		});
		findViewById(fr.ybo.transportsrennes.R.id.googlemap).setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				final Intent intent = new Intent(ListArret.this, ArretsOnMap.class);
				intent.putExtra("ligne", myLigne);
				if (currentDirection != null) {
					intent.putExtra("direction", currentDirection);
				}
				startActivity(intent);
			}
		});
		((TextView) findViewById(fr.ybo.transportsrennes.R.id.nomLong)).setText(myLigne.nomLong);
		((ImageView) findViewById(fr.ybo.transportsrennes.R.id.iconeLigne)).setImageResource(IconeLigne.getIconeResource(myLigne.nomCourt));
		construireListe();
	}

	@Override
	protected void onDestroy() {
		closeCurrentCursor();
		super.onDestroy();
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ORDER = Menu.FIRST;

	private boolean orderDirection = true;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(GROUP_ID, MENU_ORDER, Menu.NONE, fr.ybo.transportsrennes.R.string.menu_orderByName);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(MENU_ORDER)
				.setTitle(orderDirection ? fr.ybo.transportsrennes.R.string.menu_orderByName : fr.ybo.transportsrennes.R.string.menu_orderBySequence);
		menu.findItem(MENU_ORDER).setIcon(orderDirection ? R.drawable.ic_menu_sort_alphabetically : R.drawable.ic_menu_sort_by_size);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		super.onOptionsItemSelected(item);

		if (MENU_ORDER == item.getItemId()) {
			orderDirection = !orderDirection;
			construireListe();
			getListView().invalidate();
			return true;
		}
		return false;
	}
}