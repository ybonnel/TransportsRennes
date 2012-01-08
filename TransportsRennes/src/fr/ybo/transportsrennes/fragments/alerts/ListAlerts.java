package fr.ybo.transportsrennes.fragments.alerts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.alerts.DetailAlert;
import fr.ybo.transportsrennes.adapters.alerts.AlertAdapter;
import fr.ybo.transportsrennes.database.modele.Ligne;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.TacheAvecProgressDialog;

public class ListAlerts extends ListFragment {

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<Alert>(50));

	private Ligne ligne = null;

	public void setLigne(Ligne ligne) {
		this.ligne = ligne;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Serializable alert = (Serializable) l.getItemAtPosition(position);
		Intent intent = new Intent(getActivity(), DetailAlert.class);
		intent.putExtra("alert", alert);
		startActivity(intent);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setTextFilterEnabled(true);
		setListAdapter(new AlertAdapter(getActivity(), alerts));

		new TacheAvecProgressDialog<Void, Void, Void>(getActivity(), getString(R.string.dialogRequeteAlerts)) {

			@Override
			protected void myDoBackground() throws ErreurReseau {
				for (Alert alerte : keolis.getAlerts()) {
					while (alerte.lines.size() > 1) {
						Alert newAlerte = new Alert(alerte);
						newAlerte.lines.add(alerte.lines.remove(0));
						if (ligne != null) {
							if (ligne.nomCourt.equals(newAlerte.lines.get(0))) {
								alerts.add(newAlerte);
							}
						} else {
							alerts.add(newAlerte);
						}
					}
					if (ligne != null) {
						if (ligne.nomCourt.equals(alerte.lines.get(0))) {
							alerts.add(alerte);
						}
					} else {
						alerts.add(alerte);
					}
				}
			}

			@Override
			protected void onPostExecute(Void result) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute((Void) null);
	}

}
