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
package fr.ybo.transportsrennes.map;


import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapItemizedOverlayTrajet extends ItemizedOverlay<OverlayItem> {

	//Liste des marqueurs
	private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	private static Drawable leftBottom(Drawable drawable) {
		drawable.setBounds(0, 0 - drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(), 0);
		return drawable;
	}

	public MapItemizedOverlayTrajet(Drawable defaultMarker) {
		super(leftBottom(defaultMarker));
	}

	//Appeler quand on rajoute un nouvel marqueur a la liste des marqueurs
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent pEvent, MapView pMapView) {
		return super.onTouchEvent(pEvent, pMapView);
	}
}
