package fr.ybo.transportscommun.fragments;

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
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.bus.AbstractListArret;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportscommun.util.LogYbo;

public abstract class AbstractListArretFragment extends ListFragment {

	private static final LogYbo LOG_YBO = new LogYbo(AbstractListArretFragment.class);

	protected Ligne myLigne;

	public Ligne getMyLigne() {
		return myLigne;
	}

	protected Cursor currentCursor;

	private void closeCurrentCursor() {
		if (currentCursor != null && !currentCursor.isClosed()) {
			currentCursor.close();
		}
	}

	protected String currentDirection;

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
		Cursor cursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
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
		builder.setTitle(getString(R.string.chooseDirection));
		final String toutes = getString(R.string.Toutes);
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
				((TextView) getView().findViewById(R.id.directionArretCourante)).setText(items.get(item));
				getView().findViewById(R.id.directionArretCouranteScroll).invalidate();
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
		lastOrderDirection = ((AbstractListArret) getActivity()).isOrderDirection();
		if (lastOrderDirection) {
			requete.append("ArretRoute.sequence");
		} else {
			requete.append("Arret.nom");
		}
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts avec le temps avant le prochain");
		LOG_YBO.debug(requete.toString());
		currentCursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
				selectionArgs);
		LOG_YBO.debug("Exécution de la requete permettant de récupérer les arrêts terminée : "
				+ currentCursor.getCount());
	}

	protected abstract void setupAdapter();

	protected abstract Class<? extends AbstractDetailArret> getDetailArret();

	protected abstract Class<? extends BaseFragmentActivity> getListAlertsForOneLine();

	protected abstract int getLayout();

	public void construireListe() {
		LOG_YBO.debug("construireListe");
		if (myLigne == null) {
			return;
		}
		construireCursor();
		setupAdapter();
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings({ "unchecked" })
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Adapter arretAdapter = ((AdapterView<ListAdapter>) adapterView).getAdapter();
				Cursor cursor = (Cursor) arretAdapter.getItem(position);
				Intent intent = new Intent(getActivity(), getDetailArret());
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
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey("ligne")) {
			myLigne = (Ligne) getArguments().getSerializable("ligne");
		}
		getView().findViewById(R.id.directionArretCourante).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onDirectionClick();
			}
		});
		((TextView) getView().findViewById(R.id.nomLong)).setText(myLigne.nomLong);
		((ImageView) getView().findViewById(R.id.iconeLigne)).setImageResource(IconeLigne
				.getIconeResource(myLigne.nomCourt));
		if (AbstractTransportsApplication.hasAlert(myLigne.nomCourt)) {
			getView().findViewById(R.id.alerte).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.alerte).setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(getActivity(), getListAlertsForOneLine());
					intent.putExtra("ligne", myLigne);
					startActivity(intent);
				}
			});
		} else {
			getView().findViewById(R.id.alerte).setVisibility(View.GONE);
		}
		construireListe();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getLayout(), null);
	}

	@Override
	public void onDestroy() {
		closeCurrentCursor();
		super.onDestroy();
	}

}
