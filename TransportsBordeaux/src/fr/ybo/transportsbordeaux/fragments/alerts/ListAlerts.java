package fr.ybo.transportsbordeaux.fragments.alerts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.adapters.alerts.AlertAdapter;
import fr.ybo.transportsbordeaux.database.modele.Alert;
import fr.ybo.transportsbordeaux.tbcapi.TcbConstantes;
import fr.ybo.transportscommun.donnees.modele.Ligne;

public class ListAlerts extends ListFragment {

    private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<Alert>(50));

    private Ligne ligne;

    public void setLigne(final Ligne ligne) {
        this.ligne = ligne;
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        super.onListItemClick(l, v, position, id);
        final Alert alert = (Alert) l.getItemAtPosition(position);
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TcbConstantes.URL_MOBILE_TBC + alert.url));
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

            private final List<Alert> alertsTmp = new ArrayList<Alert>();

            @Override
            protected Void doInBackground(final Void... params) {
                for (final Alert alerte : Alert.getAlertes()) {
                    if (ligne != null) {
                        if (ligne.nomLong.equals(alerte.ligne)) {
                            alertsTmp.add(alerte);
                        }
                    } else {
                        alertsTmp.add(alerte);
                    }
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
