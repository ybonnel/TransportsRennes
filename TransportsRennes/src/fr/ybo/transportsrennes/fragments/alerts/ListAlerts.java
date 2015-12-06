package fr.ybo.transportsrennes.fragments.alerts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.alerts.DetailAlert;
import fr.ybo.transportsrennes.adapters.alerts.AlertAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

public class ListAlerts extends ListFragment {

	private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<Alert>(50));

	private Ligne ligne;

	public void setLigne(final Ligne ligne) {
		this.ligne = ligne;
	}

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		final Serializable alert = (Serializable) l.getItemAtPosition(position);
		final Intent intent = new Intent(getActivity(), DetailAlert.class);
		intent.putExtra("alert", alert);
		startActivity(intent);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setTextFilterEnabled(true);
		lv.setCacheColorHint(Color.TRANSPARENT);
		setListAdapter(new AlertAdapter(getActivity(), alerts));
		final Alert alertChargement = new Alert();
		alertChargement.title = getString(R.string.dialogRequeteAlerts);
		alerts.add(alertChargement);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreurReseau;

			private final Collection<Alert> alertsTmp = new ArrayList<Alert>();

			@Override
			protected Void doInBackground(final Void... params) {
				try {
					for (final Alert alerte : Keolis.getAlerts()) {
						while (alerte.lines.size() > 1) {
							final Alert newAlerte = new Alert(alerte);
							newAlerte.lines.add(alerte.lines.remove(0));
							if (ligne != null) {
								if (ligne.nomCourt.equals(newAlerte.lines.get(0))) {
									alertsTmp.add(newAlerte);
								}
							} else {
								alertsTmp.add(newAlerte);
							}
						}
						if (ligne != null) {
							if (ligne.nomCourt.equals(alerte.lines.get(0))) {
								alertsTmp.add(alerte);
							}
						} else {
							alertsTmp.add(alerte);
						}
					}
				} catch (final ErreurReseau e) {
					erreurReseau = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				if (erreurReseau) {
					try {
						Toast.makeText(getActivity(), getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
					} catch (final Exception ignore) {

					}
				} else {
					alerts.clear();
					alerts.addAll(alertsTmp);
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}

		}.execute((Void) null);
	}

}
