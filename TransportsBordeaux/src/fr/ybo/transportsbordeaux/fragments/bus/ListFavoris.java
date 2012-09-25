package fr.ybo.transportsbordeaux.fragments.bus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.bus.DetailArret;
import fr.ybo.transportsbordeaux.activity.bus.TabFavoris;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportsbordeaux.adapters.bus.FavoriAdapter;
import fr.ybo.transportsbordeaux.util.UpdateTimeUtil;
import fr.ybo.transportsbordeaux.util.UpdateTimeUtil.UpdateTime;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;

public class ListFavoris extends ListFragment {

	private int groupId;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey("groupe")) {
			groupe = getArguments().getString("groupe");
			groupId = groupe.hashCode();
		} else {
			groupId = getString(R.string.all).hashCode();
		}

		construireListe();
		updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

			public void update(Calendar calendar) {
				((FavoriAdapter) getListAdapter()).majCalendar();
				((FavoriAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}, getActivity());
		updateTimeUtil.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (AbstractTransportsApplication.getDataBaseHelper().selectAll(GroupeFavori.class).isEmpty()) {
			return inflater.inflate(R.layout.fragment_favoris, container);
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		updateTimeUtil.start();
		List<ArretFavori> newFavoris = recupererFavoris();
		if (newFavoris.size() != favoris.size()) {
			favoris = null;
			construireListe();
		} else {
			for (int indice = 0; indice < favoris.size(); indice++) {
				if (!newFavoris.get(indice).arretId.equals(favoris.get(indice).arretId)
						|| !newFavoris.get(indice).ligneId.equals(favoris.get(indice).ligneId)
						|| !newFavoris.get(indice).macroDirection.equals(favoris.get(indice).macroDirection)) {
					favoris = null;
					construireListe();
				}
			}
		}
		super.onResume();
	}

	@Override
	public void onStop() {
		updateTimeUtil.stop();
		super.onPause();
	}

	private void construireListe() {
		List<ArretFavori> favoris = getFavoris();

		setListAdapter(new FavoriAdapter(getActivity().getApplicationContext(), favoris));
		ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DetailArret.class);
				intent.putExtra("favori", (Serializable) adapterView.getAdapter().getItem(position));
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
		ArretFavori favoriExemple = new ArretFavori();
		if (groupe != null) {
			favoriExemple.groupe = groupe;
		}
		return AbstractTransportsApplication.getDataBaseHelper().select(favoriExemple, "ordre");
	}

	private UpdateTimeUtil updateTimeUtil;

	private String groupe = null;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(favori.nomArret);
			menu.add(groupId, R.id.supprimerFavori, 0, getString(R.string.suprimerFavori));
			menu.add(groupId, R.id.deplacerGroupe, 0, getString(R.string.deplacerGroupe));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() != groupId) {
			return super.onContextItemSelected(item);
		}
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final ArretFavori favori;
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				favori = (ArretFavori) getListAdapter().getItem(info.position);

				if (TransportsWidget11Configure.isNotUsed(getActivity(), favori)
						&& TransportsWidget21Configure.isNotUsed(getActivity(), favori)) {
					AbstractTransportsApplication.getDataBaseHelper().delete(favori);
					((FavoriAdapter) getListAdapter()).getFavoris().clear();
					((FavoriAdapter) getListAdapter()).getFavoris().addAll(
							AbstractTransportsApplication.getDataBaseHelper().select(new ArretFavori()));
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast.makeText(getActivity(), getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG).show();
				}
				return true;
			case R.id.deplacerGroupe:
				favori = (ArretFavori) getListAdapter().getItem(info.position);
				final List<String> groupes = new ArrayList<String>();
				groupes.add(getString(R.string.all));
				for (GroupeFavori groupe : AbstractTransportsApplication.getDataBaseHelper()
						.selectAll(GroupeFavori.class)) {
					groupes.add(groupe.name);
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.chooseGroupe));
				builder.setItems(groupes.toArray(new String[groupes.size()]), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int item) {
						favori.groupe = groupes.get(item).equals(getString(R.string.all)) ? null : groupes.get(item);
						AbstractTransportsApplication.getDataBaseHelper().update(favori);
						dialogInterface.dismiss();
						startActivity(new Intent(getActivity(), TabFavoris.class));
						getActivity().finish();
					}
				});
				builder.create().show();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

}
