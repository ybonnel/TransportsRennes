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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.LigneAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.List;

/**
 * Activité affichant les lignes de bus..
 *
 * @author ybonnel
 */
public class BusRennes extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(BusRennes.class);

	private void constructionListe() {
		List<Ligne> lignes = TransportsRennesApplication.getDataBaseHelper().select(new Ligne(), null, null, "ordre");
		setListAdapter(new LigneAdapter(this, lignes));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Ligne ligne = (Ligne) ((ListView) adapterView).getItemAtPosition(position);
				Intent intent = new Intent(BusRennes.this, ListArret.class);
				intent.putExtra("ligne", ligne);
				startActivity(intent);
			}

		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LOG_YBO.startChrono("onCreate");
		LOG_YBO.startChrono("super.onCreate");
		super.onCreate(savedInstanceState);
		LOG_YBO.stopChrono("super.onCreate");
		LOG_YBO.startChrono("setContentVew");
		setContentView(R.layout.bus);
		LOG_YBO.stopChrono("setContentVew");
		LOG_YBO.startChrono("constructionListe");
		constructionListe();
		LOG_YBO.stopChrono("constructionListe");
		LOG_YBO.stopChrono("onCreate");
	}
}