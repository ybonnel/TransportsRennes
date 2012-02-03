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
package fr.ybo.transportsrennes.fragments.bus;

import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.fragments.AbstractListArretFragment;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.alerts.ListAlertsForOneLine;
import fr.ybo.transportsrennes.activity.bus.DetailArret;
import fr.ybo.transportsrennes.adapters.bus.ArretAdapter;

/**
 * Liste des arrêts d'une ligne de bus.
 * 
 * @author ybonnel
 */
public class ListArretFragment extends AbstractListArretFragment {

	@Override
	protected int getLayout() {
		return R.layout.fragment_listearrets;
	}

	@Override
	protected void setupAdapter() {
		setListAdapter(new ArretAdapter(getActivity(), currentCursor, myLigne));
	}

	@Override
	protected Class<? extends AbstractDetailArret> getDetailArret() {
		return DetailArret.class;
	}

	@Override
	protected Class<? extends BaseFragmentActivity> getListAlertsForOneLine() {
		return ListAlertsForOneLine.class;
	}
}
