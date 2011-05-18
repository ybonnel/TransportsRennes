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

package fr.ybo.transportsbordeaux;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.activity.TacheAvecProgressDialog;
import fr.ybo.transportsbordeaux.adapters.DetailTrajetAdapter;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.tbc.Horaire;
import fr.ybo.transportsbordeaux.tbc.PortionTrajet;
import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.util.IconeLigne;

/**
 * Activitée permettant d'afficher le détail d'un trajet
 *
 * @author ybonnel
 */
public class DetailTrajet extends MenuAccueil.ListActivity {

	private Horaire horaire;
	private List<PortionTrajet> trajet = new ArrayList<PortionTrajet>();
	private Ligne ligne;
	private String direction;

	private void recuperationDonneesIntent() {
		ligne = (Ligne) getIntent().getExtras().getSerializable("ligne");
		horaire = (Horaire) getIntent().getExtras().getSerializable("horaire");
		direction = getIntent().getExtras().getString("direction");
	}

	private void gestionViewsTitle() {
		((TextView) findViewById(R.id.nomLong)).setText(ligne.nomLong);
		((ImageView) findViewById(R.id.iconeLigne)).setImageResource(IconeLigne.getIconeResource(ligne.nomCourt));
		((TextView) findViewById(R.id.detailTrajet_nomTrajet)).setText(getString(R.string.vers) + ' ' + direction);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailtrajet);
		recuperationDonneesIntent();
		gestionViewsTitle();
		setListAdapter(new DetailTrajetAdapter(this, trajet));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.recuperationTrajet)) {
			
			private boolean erreurReseau = false;

			@Override
			protected Void doInBackground(Void... params) {
				try {
					trajet.addAll(horaire.getTrajet());
				} catch (TbcErreurReseaux tbcErreurReseaux) {
					erreurReseau = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (erreurReseau) {
					Toast.makeText(DetailTrajet.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
				}
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}.execute();

		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}
}
