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
package fr.ybo.transportsbordeaux.activity.alerts;


import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.google.ads.Ad;
import com.google.ads.AdRequest;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.fragments.alerts.ListAlerts;
import fr.ybo.transportsbordeaux.fragments.alerts.ListTwitter;
import fr.ybo.transportscommun.activity.alerts.AbstractTabAlertes;

public class TabAlertes extends AbstractTabAlertes {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Look up the AdView as a resource and load a request.
        ((Ad) findViewById(R.id.adView)).loadAd(new AdRequest());
    }

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
	}

	@Override
	protected int getLayout() {
		return R.layout.tabalertes;
	}

	@Override
	protected Class<? extends ListFragment> getListAlertsClass() {
		return ListAlerts.class;
	}

	@Override
	protected Class<? extends ListFragment> getListTwitterClass() {
		return ListTwitter.class;
	}
}
