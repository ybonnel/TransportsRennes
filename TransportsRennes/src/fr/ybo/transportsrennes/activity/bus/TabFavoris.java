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
package fr.ybo.transportsrennes.activity.bus;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import fr.ybo.transportscommun.activity.bus.AbstractTabFavoris;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.loading.LoadingActivity;
import fr.ybo.transportsrennes.fragments.bus.ListFavoris;

public class TabFavoris extends AbstractTabFavoris {

	@Override
	protected int getLayout() {
		return R.layout.tabfavoris;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.bus_favoris_menu_items, R.menu.holo_bus_favoris_menu_items);
	}

	@Override
	protected Class<? extends BaseFragmentActivity> getListFavorisForNoGroupClass() {
		return ListFavorisForNoGroup_.class;
	}

	@Override
	protected Class<? extends ListFragment> getListFavoris() {
		return ListFavoris.class;
	}

	@Override
	protected void loadFavoris() {
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_LOAD_FAVORIS);
		startActivity(intent);
	}
}
