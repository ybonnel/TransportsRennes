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
package fr.ybo.transportsbordeaux.activity.bus;

import android.os.Bundle;

import com.google.ads.Ad;
import com.google.ads.AdRequest;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.bus.AbstractDetailTrajet;

/**
 * Activitée permettant d'afficher le détail d'un trajet
 *
 * @author ybonnel
 */
public class DetailTrajet extends AbstractDetailTrajet {

	@Override
	protected int getLayout() {
		return R.layout.detailtrajet;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
	}

	@Override
	protected Class<? extends AbstractDetailArret> getDetailArretClass() {
		return DetailArret.class;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Look up the AdView as a resource and load a request.
		((Ad) findViewById(R.id.adView)).loadAd(new AdRequest());
	}
}
