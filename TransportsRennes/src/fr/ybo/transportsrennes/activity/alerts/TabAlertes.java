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

import android.os.Bundle;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.commun.BaseActivity.BaseTabFragmentActivity;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.fragments.alerts.ListAlerts;
import fr.ybo.transportsrennes.fragments.alerts.ListTwitter;

public class TabAlertes extends BaseTabFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		((TransportsRennesApplication) getApplication()).majTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabalertes);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
		configureTabs();
		addTab("alertes", getString(R.string.alertes), ListAlerts.class);
		addTab("twitter", getString(R.string.twitter), ListTwitter.class);
		setCurrentTab(savedInstanceState);
	}

}
