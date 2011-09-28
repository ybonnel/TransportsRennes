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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.adapters.TrajetAdapter;
import fr.ybo.transportsbordeaux.itineraires.ItineraireReponse;
import fr.ybo.transportsbordeaux.itineraires.Trajet;

public class Itineraires extends MenuAccueil.ListActivity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itineraires);
		ItineraireReponse itineraireReponse = (ItineraireReponse) getIntent().getExtras().getSerializable(
				"itineraireReponse");
		int heureDepart = getIntent().getIntExtra("heureDepart", 0);
		setListAdapter(new TrajetAdapter(this, itineraireReponse.getTrajets(), heureDepart));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Trajet trajet = (Trajet) adapterView.getItemAtPosition(position);
				Intent intent = new Intent(Itineraires.this, TrajetOnMap.class);
				intent.putExtra("trajet", trajet);
				startActivity(intent);
			}

		});
		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}
}
