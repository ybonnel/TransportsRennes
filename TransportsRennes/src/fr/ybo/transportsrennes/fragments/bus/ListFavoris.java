package fr.ybo.transportsrennes.fragments.bus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportscommun.util.UpdateTimeUtil;
import fr.ybo.transportscommun.util.UpdateTimeUtil.UpdateTime;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.bus.DetailArret;
import fr.ybo.transportsrennes.activity.bus.TabFavoris;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidgetConfigure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidgetLowResConfigure;
import fr.ybo.transportsrennes.adapters.bus.FavoriAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class ListFavoris extends ListFragment {

	private int groupId;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey("groupe")) {
			groupe = getArguments().getString("groupe");
			groupId = groupe.hashCode();
		} else {
			groupId = getString(R.string.all).hashCode();
		}

		construireListe();
		updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

			@Override
			public void update(final Calendar calendar) {
				((FavoriAdapter) getListAdapter()).majCalendar();
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			}

			@Override
			public boolean updateSecond() {
				return false;
			}

			@Override
			public Set<Integer> secondesToUpdate() {
				return null;
			}
		}, getActivity());
		updateTimeUtil.start();

	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		if (TransportsRennesApplication.getDataBaseHelper().selectAll(GroupeFavori.class).isEmpty()) {
			return inflater.inflate(R.layout.fragment_favoris, container);
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		updateTimeUtil.start();
		final List<ArretFavori> newFavoris = recupererFavoris();
		if (newFavoris.size() == favoris.size()) {
			for (int indice = 0; indice < favoris.size(); indice++) {
				if (!newFavoris.get(indice).arretId.equals(favoris.get(indice).arretId)
						|| !newFavoris.get(indice).ligneId.equals(favoris.get(indice).ligneId)
						|| !newFavoris.get(indice).macroDirection.equals(favoris.get(indice).macroDirection)) {
					favoris = null;
					construireListe();
				}
			}
		} else {
			favoris = null;
			construireListe();
		}
		onResume();
	}

	@Override
	public void onStop() {
		updateTimeUtil.stop();
		onPause();
	}

	private void construireListe() {
		final List<ArretFavori> favoris = getFavoris();

		setListAdapter(new FavoriAdapter(getActivity().getApplicationContext(), favoris));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final Intent intent = new Intent(getActivity(), DetailArret.class).putExtra("favori", (Serializable) adapterView.getAdapter().getItem(position));
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	private List<ArretFavori> favoris;

	private List<ArretFavori> getFavoris() {
		if (favoris == null) {
			favoris = recupererFavoris();
		}
		return favoris;
	}

	private List<ArretFavori> recupererFavoris() {
		final ArretFavori favoriExemple = new ArretFavori();
		if (groupe != null) {
			favoriExemple.groupe = groupe;
		}
		return TransportsRennesApplication.getDataBaseHelper().select(favoriExemple, "ordre");
	}

	private UpdateTimeUtil updateTimeUtil;

	private String groupe;

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			final ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(favori.nomArret);
			menu.add(groupId, R.id.supprimerFavori, 0, getString(R.string.suprimerFavori));
			menu.add(groupId, R.id.deplacerGroupe, 0, getString(R.string.deplacerGroupe));
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		if (item.getGroupId() != groupId) {
			return super.onContextItemSelected(item);
		}
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final ArretFavori favori;
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				favori = (ArretFavori) getListAdapter().getItem(info.position);

				if (TransportsWidgetConfigure.isNotUsed(getActivity(), favori)
						&& TransportsWidget11Configure.isNotUsed(getActivity(), favori)
						&& TransportsWidget21Configure.isNotUsed(getActivity(), favori)
						&& TransportsWidgetLowResConfigure.isNotUsed(getActivity(), favori)) {
					TransportsRennesApplication.getDataBaseHelper().delete(favori);
					((FavoriAdapter) getListAdapter()).getFavoris().clear();
					((FavoriAdapter) getListAdapter()).getFavoris().addAll(
							TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori()));
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast.makeText(getActivity(), R.string.favoriUsedByWidget, Toast.LENGTH_LONG).show();
				}
				return true;
			case R.id.deplacerGroupe:
				favori = (ArretFavori) getListAdapter().getItem(info.position);
				final List<String> groupes = new ArrayList<String>();
				groupes.add(getString(R.string.all));
				for (final GroupeFavori groupe : TransportsRennesApplication.getDataBaseHelper()
						.selectAll(GroupeFavori.class)) {
					groupes.add(groupe.name);
				}
				new AlertDialog.Builder(getActivity()).setTitle(R.string.chooseGroupe).setItems(groupes.toArray(new String[groupes.size()]), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialogInterface, final int item) {
						favori.groupe = groupes.get(item).equals(getString(R.string.all)) ? null : groupes.get(item);
						TransportsRennesApplication.getDataBaseHelper().update(favori);
						dialogInterface.dismiss();
						startActivity(new Intent(getActivity(), TabFavoris.class));
						getActivity().finish();
					}
				}).show();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

}
