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
package fr.ybo.transportsbordeaux.activity.itineraires;

import android.os.Bundle;
import android.widget.ListView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.adapters.itineraires.TrajetAdapter;
import fr.ybo.transportsbordeaux.itineraires.ItineraireReponse;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;

public class Itineraires extends BaseListActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itineraires);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        ItineraireReponse itineraireReponse = (ItineraireReponse) getIntent().getExtras().getSerializable(
                "itineraireReponse");
        int heureDepart = getIntent().getIntExtra("heureDepart", 0);
        setListAdapter(new TrajetAdapter(this, itineraireReponse.getTrajets(), heureDepart));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        // Look up the AdView as a resource and load a request.
        ((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
    }
}
