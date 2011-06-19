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
package fr.ybo.transportsbordeaux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.activity.TacheAvecProgressDialog;
import fr.ybo.transportsbordeaux.adapters.AlertAdapter;
import fr.ybo.transportsbordeaux.modele.Alert;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.tbc.TcbConstantes;

public class ListAlerts extends MenuAccueil.ListActivity {

	private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<Alert>());

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
				Alert alert = (Alert) adapterView.getItemAtPosition(position);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TcbConstantes.URL_MOBILE_TBC + alert.url));
				startActivity(intent);
			}

		});

		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteAlerts)) {
			
			private boolean erreurReseau = false;

			@Override
			protected Void doInBackground(Void... pParams) {
				try {
				for (Alert alerte : Alert.getAlertes()) {
					if (ligne != null) {
						if (ligne.nomLong.equals(alerte.ligne)) {
							alerts.add(alerte);
						}
					} else {
						alerts.add(alerte);
					}
				}
				} catch (TbcErreurReseaux exceptionReseau) {
					erreurReseau = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				if (erreurReseau) {
					Toast.makeText(ListAlerts.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

}
