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
package fr.ybo.transportsrennes.activity.alerts;

import android.support.v4.app.ListFragment;
import fr.ybo.transportscommun.activity.alerts.AbstractTabAlertes;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.fragments.alerts.ListAlerts;
import fr.ybo.transportsrennes.fragments.alerts.ListTwitter;

public class TabAlertes extends AbstractTabAlertes {

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
	}

	@Override
	protected Class<? extends ListFragment> getListAlertsClass() {
		return ListAlerts.class;
	}

	@Override
	protected Class<? extends ListFragment> getListTwitterClass() {
		return ListTwitter.class;
	}

	@Override
	protected int getLayout() {
		return R.layout.tabalertes;
	}

}
