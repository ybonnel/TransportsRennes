/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.AlertAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListAlerts extends MenuAccueil.ListActivity {

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	private static final LogYbo LOG_YBO = new LogYbo(ListAlerts.class);

	private ProgressDialog myProgressDialog;

	private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<Alert>());

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		setListAdapter(new AlertAdapter(this, R.layout.alert, alerts));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final Alert alert = (Alert) ((ListView) adapterView).getItemAtPosition(position);
				final Intent intent = new Intent(ListAlerts.this, DetailAlert.class);
				intent.putExtra("alert", alert);
				startActivity(intent);
			}

		});

		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequeteAlerts), true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					alerts.addAll(keolis.getAlerts());
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans ListAlerts.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				((ArrayAdapter<Alert>) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
				if (erreur) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Une erreur est survenu lors de l'interrogation du STAR, si cela persiste envoyez un mail au développeur.",
							Toast.LENGTH_LONG);
					toast.show();
					ListAlerts.this.finish();
				}
			}
		}.execute();
	}

}
