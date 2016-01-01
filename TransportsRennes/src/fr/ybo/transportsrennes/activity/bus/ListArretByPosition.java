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

import android.widget.Toast;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.bus.AbstractListArretByPosition;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidgetConfigure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidgetLowResConfigure;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

/**
 * Activité de type liste permettant de lister les arrêts de bus par distances
 * de la position actuelle.
 * 
 * @author ybonnel
 */
public class ListArretByPosition extends AbstractListArretByPosition {

	@Override
	protected int getLayout() {
		return R.layout.listarretgps;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items_with_search,
				R.menu.holo_default_menu_items_with_search);
	}

	@Override
	protected Class<? extends AbstractDetailArret> getDetailArret() {
		return DetailArret.class;
	}

	@Override
	protected void deleteFavori(final ArretFavori favori) {
		if (TransportsWidgetConfigure.isNotUsed(this, favori) && TransportsWidget11Configure.isNotUsed(this, favori)
				&& TransportsWidget21Configure.isNotUsed(this, favori)
				&& TransportsWidgetLowResConfigure.isNotUsed(this, favori)) {
			TransportsRennesApplication.getDataBaseHelper().delete(favori);
		} else {
			Toast.makeText(this, R.string.favoriUsedByWidget, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected Class<?> getRawClass() {
		return R.raw.class;
	}
}
