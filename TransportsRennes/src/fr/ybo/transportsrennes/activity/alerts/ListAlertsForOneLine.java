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
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.fragments.alerts.ListAlerts;

public class ListAlertsForOneLine extends BaseFragmentActivity {

    private Ligne ligne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ligne = (Ligne) getIntent().getExtras().getSerializable("ligne");
		setContentView(R.layout.listalert);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
		ListAlerts fragmentAlert = (ListAlerts) getSupportFragmentManager().findFragmentById(R.id.fragment_alerts);
		fragmentAlert.setLigne(ligne);
    }

}
