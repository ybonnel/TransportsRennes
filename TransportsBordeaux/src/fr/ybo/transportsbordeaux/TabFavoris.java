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

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.ubikod.capptain.android.sdk.activity.CapptainTabActivity;

import fr.ybo.transportsbordeaux.modele.GroupeFavori;

public class TabFavoris extends CapptainTabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabfavoris);
		List<GroupeFavori> groupes = TransportsBordeauxApplication.getDataBaseHelper().selectAll(GroupeFavori.class);
		if (groupes.isEmpty()) {
			Intent intent = new Intent(this, ListFavoris.class);
			startActivity(intent);
			finish();
			return;
		}

		TabHost tabHost = getTabHost();

		// Initialize a TabSpec for each tab and add it to the TabHost
		Intent intentTous = new Intent().setClass(this, ListFavoris.class);
		tabHost.addTab(tabHost.newTabSpec("all").setIndicator(getString(R.string.all)).setContent(intentTous));

		// Do the same for the other tabs
		for (GroupeFavori groupe : groupes) {
			Intent intent = new Intent().setClass(this, ListFavoris.class);
			intent.putExtra("groupe", groupe.name);
			tabHost.addTab(tabHost.newTabSpec(groupe.name).setIndicator(groupe.name).setContent(intent));
		}

		tabHost.setCurrentTab(0);
	}
}
