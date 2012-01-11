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
package fr.ybo.transportsrennes.fragments.bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.alerts.ListAlertsForOneLine;
import fr.ybo.transportsrennes.activity.bus.DetailArret;
import fr.ybo.transportsrennes.activity.bus.ListArret;
import fr.ybo.transportsrennes.adapters.bus.ArretAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.modele.Ligne;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.LogYbo;

/**
 * Liste des arrêts d'une ligne de bus.
 * 
 * @author ybonnel
 */
public class ListArretFragment extends ListFragment {

	private static final LogYbo LOG_YBO = new LogYbo(ListArretFragment.class);

	private Ligne myLigne;

	public Ligne getMyLigne() {
		return myLigne;
	}

	private Cursor currentCursor;

	private void closeCurrentCursor() {
		if (currentCursor != null && !currentCursor.isClosed()) {
			currentCursor.close();
		}
	}

	private String currentDirection;

	public String getCurrentDirection() {
		return currentDirection;
	}

	private void onDirectionClick() {
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT Direction.id as directionId, Direction.direction as direction ");
		requete.append("FROM Direction, ArretRoute ");
		requete.append("WHERE Direction.id = ArretRoute.directionId");
		requete.append(" AND ArretRoute.ligneId = :ligneId ");
		requete.append("GROUP BY Direction.id, Direction.direction");
		Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
				Collections.singletonList(myLigne.id));
		int directionIndex = cursor.getColumnIndex("direction");
		final List<String> items = new ArrayList<String>(5);
		long startTime = System.currentTimeMillis();
		while (cursor.moveToNext()) {
			items.add(cursor.getString(directionIndex));
		}
		cursor.close();
		long elapsedTime = System.currentTimeMillis() - startTime;
		LOG_YBO.debug("Temps requete récupération des directions : " + elapsedTime);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(fr.ybo.transportsrennes.R.string.chooseDirection));
		final String toutes = getString(fr.ybo.transportsrennes.R.string.Toutes);
		items.add(toutes);
		Collections.sort(items, new Comparator<String>() {
			public int compare(String o1, String o2) {
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
			public void onClick(DialogInterface dialogInterface, int item) {
				currentDirection = items.get(item).equals(toutes) ? null : items.get(item);
				construireListe();
				((TextView) getView().findViewById(fr.ybo.transportsrennes.R.id.directionArretCourante)).setText(items
						.get(item));
				getView().findViewById(fr.ybo.transportsrennes.R.id.directionArretCouranteScroll).invalidate();
				getListView().invalidate();
				dialogInterface.dismiss();
			}
		});
		builder.create().show();
	}

	private boolean lastOrderDirection;

	public boolean isLastOrderDirection() {
		return lastOrderDirection;
	}

	private void construireCursor() {
		closeCurrentCursor();
		List<String> selectionArgs = new ArrayList<String>(2);
		selectionArgs.add(myLigne.id);
		StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as _id, Arret.nom as arretName,");
		requete.append(" Direction.direction as direction, ArretRoute.accessible as accessible, ArretRoute.macroDirection as macroDirection ");
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
		lastOrderDirection = ((ListArret) getActivity()).isOrderDirection();
		if (lastOrderDirection) {
			requete.append("ArretRoute.sequence");
		} else {
			requete.append("Arret.nom");
		}
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts avec le temps avant le prochain");
		LOG_YBO.debug(requete.toString());
		currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
				selectionArgs);
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts terminée : "
				+ currentCursor.getCount());
	}

	private boolean construct = false;

	public boolean isConstruct() {
		return construct;
	}

	private boolean activityCreated = false;

	public void construireListe() {
		if (!activityCreated) {
			return;
		}
		LOG_YBO.debug("construireListe");
		construireCursor();
		setListAdapter(new ArretAdapter(getActivity(), currentCursor, myLigne));
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings({ "unchecked" })
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Adapter arretAdapter = ((AdapterView<ListAdapter>) adapterView).getAdapter();
				Cursor cursor = (Cursor) arretAdapter.getItem(position);
				Intent intent = new Intent(getActivity(), DetailArret.class);
				intent.putExtra("idArret", cursor.getString(cursor.getColumnIndex("_id")));
				intent.putExtra("nomArret", cursor.getString(cursor.getColumnIndex("arretName")));
				intent.putExtra("direction", cursor.getString(cursor.getColumnIndex("direction")));
				intent.putExtra("macroDirection", cursor.getInt(cursor.getColumnIndex("macroDirection")));
				intent.putExtra("ligne", myLigne);
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
		getListView().invalidate();
		construct = true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LOG_YBO.debug("onActivityCreated");
		if (getArguments() != null && getArguments().containsKey("ligne")) {
			myLigne = (Ligne) getArguments().getSerializable("ligne");
		}
		getView().findViewById(fr.ybo.transportsrennes.R.id.directionArretCourante).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						onDirectionClick();
					}
				});
		((TextView) getView().findViewById(R.id.nomLong)).setText(myLigne.nomLong);
		((ImageView) getView().findViewById(R.id.iconeLigne)).setImageResource(IconeLigne
				.getIconeResource(myLigne.nomCourt));
		if (TransportsRennesApplication.hasAlert(myLigne.nomCourt)) {
			getView().findViewById(R.id.alerte).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.alerte).setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(getActivity(), ListAlertsForOneLine.class);
					intent.putExtra("ligne", myLigne);
					startActivity(intent);
				}
			});
		} else {
			getView().findViewById(R.id.alerte).setVisibility(View.GONE);
		}
		activityCreated = true;
		if (((ListArret) getActivity()).getCurrrentTabTag().equals(myLigne.id)) {
			construireListe();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_listearrets, null);
	}

	@Override
	public void onDestroy() {
		closeCurrentCursor();
		super.onDestroy();
	}
}
