/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportscommun.activity.velo;

import java.util.List;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseMapActivity;
import fr.ybo.transportscommun.donnees.modele.IStation;
import fr.ybo.transportscommun.map.MapItemizedOverlayVelo;
import fr.ybo.transportscommun.util.FixedMyLocationOverlay;

/**
 * @author ybonnel
 *
 */
public abstract class AbstractStationOnMap extends BaseMapActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.ybo.transportscommun.activity.commun.BaseActivity.BaseMapActivity#
	 * onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		Iterable<IStation> stations = (Iterable<IStation>) getIntent().getExtras().getSerializable("stations");
		MapView mapView = getMapView();
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		List<Overlay> mapOverlays = mapView.getOverlays();
		BitmapDrawable marker = (BitmapDrawable) getResources().getDrawable(R.drawable.pin);
		MapItemizedOverlayVelo itemizedoverlay = new MapItemizedOverlayVelo(marker, this);

		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;

		for (IStation station : stations) {
			int latitude = (int) (station.getLatitude() * 1.0E6);
			int longitude = (int) (station.getLongitude() * 1.0E6);
			if (latitude < minLatitude) {
				minLatitude = latitude;
			}
			if (latitude > maxLatitude) {
				maxLatitude = latitude;
			}
			if (longitude < minLongitude) {
				minLongitude = longitude;
			}
			if (longitude > maxLongitude) {
				maxLongitude = longitude;
			}
			itemizedoverlay.addOverlay(station);
		}
		mapOverlays.add(itemizedoverlay);
		mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		mc.setZoom(14);

		myLocationOverlay = new FixedMyLocationOverlay(this, mapView);
		mapOverlays.add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();
		gestionButtonLayout();
	}

	private MyLocationOverlay myLocationOverlay;

	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}

	@Override
	protected void onPause() {
		myLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	protected abstract MapView getMapView();
}
