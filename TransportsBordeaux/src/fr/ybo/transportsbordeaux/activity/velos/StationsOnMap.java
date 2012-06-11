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
package fr.ybo.transportsbordeaux.activity.velos;

import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.activity.velo.AbstractStationOnMap;

public class StationsOnMap extends AbstractStationOnMap {

	@Override
	protected int getLayout() {
		return R.layout.map;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
	}

	@Override
	protected MapView getMapView() {
		return (MapView) findViewById(R.id.mapview);
	}

	@Override
	protected Drawable getMarkee() {
		return getResources().getDrawable(R.drawable.markee_velo);
	}
}
