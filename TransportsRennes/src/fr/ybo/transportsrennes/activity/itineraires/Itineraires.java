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
package fr.ybo.transportsrennes.activity.itineraires;

import android.os.Bundle;
import android.widget.ListView;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.itineraires.TrajetAdapter;
import fr.ybo.transportsrennes.itineraires.ItineraireReponse;

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
                "itinerairesReponse");
        int heureDepart = getIntent().getIntExtra("heureDepart", 0);
        setListAdapter(new TrajetAdapter(this, itineraireReponse.getTrajets(), heureDepart));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
    }
}
