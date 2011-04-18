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

package fr.ybo.transportsrennes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.Response;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.TrajetAdapter;
import fr.ybo.transportsrennes.util.GsonUtil;

public class Itineraires extends MenuAccueil.ListActivity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itineraires);
		Response itineraireReponse = GsonUtil.getGson().fromJson(
				getIntent().getExtras().getString("itinerairesReponse"), Response.class);
		int heureDepart = getIntent().getIntExtra("heureDepart", 0);
		setListAdapter(new TrajetAdapter(this, itineraireReponse.getPlan(), heureDepart));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Itinerary trajet = (Itinerary) adapterView.getItemAtPosition(position);
				Intent intent = new Intent(Itineraires.this, TrajetOnMap.class);
				intent.putExtra("trajet", GsonUtil.getGson().toJson(trajet));
				startActivity(intent);
			}

		});
	}
}
