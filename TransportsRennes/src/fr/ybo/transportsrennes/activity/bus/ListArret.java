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

import android.support.v4.app.ListFragment;
import fr.ybo.transportscommun.activity.bus.AbstractListArret;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.fragments.bus.ListArretFragment;

/**
 * Liste des arrêts d'une ligne de bus.
 * 
 * @author ybonnel
 */
public class ListArret extends AbstractListArret {

	@Override
	protected int getLayout() {
		return R.layout.listearrets;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.listarrets_menu_items, R.menu.holo_listarrets_menu_items);
	}

	@Override
	protected Class<? extends ListFragment> getListArretFragment() {
		return ListArretFragment.class;
	}
}
