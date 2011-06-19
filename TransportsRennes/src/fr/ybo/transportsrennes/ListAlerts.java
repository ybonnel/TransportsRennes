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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.AlertAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.TacheAvecProgressDialog;

public class ListAlerts extends MenuAccueil.ListActivity {

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<Alert>(50));

	private Ligne ligne;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ligne = (Ligne) (getIntent().getExtras() == null ? null : getIntent().getExtras().getSerializable("ligne"));
		setContentView(R.layout.liste);
		setListAdapter(new AlertAdapter(this, alerts));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Serializable alert = (Serializable) adapterView.getItemAtPosition(position);
				Intent intent = new Intent(ListAlerts.this, DetailAlert.class);
				intent.putExtra("alert", alert);
				startActivity(intent);
			}

		});

		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteAlerts)) {

			@Override
			protected Void myDoBackground(Void... pParams) throws ErreurReseau {
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
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}

}
