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

package fr.ybo.transportsrennes;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.maps.*;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.map.MapItemizedOverlayArret;
import fr.ybo.transportsrennes.map.MapItemizedOverlayVelo;
import fr.ybo.transportsrennes.util.Formatteur;

import java.util.List;

public class StationsOnMap extends MapActivity {

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		List<Station> stations = (List<Station>) getIntent().getExtras().getSerializable("stations");

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		mapView.setSatellite(true);

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.markee_velo);
		MapItemizedOverlayVelo itemizedoverlay = new MapItemizedOverlayVelo(drawable, this);

		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;


		for (Station station : stations) {
			int latitude = (int) (station.latitude * 1E6);
			int longitude = (int) (station.longitude * 1E6);
			GeoPoint geoPoint = new GeoPoint(latitude, longitude);
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
			int placesTotales = station.bikesavailable + station.slotsavailable;
			OverlayItem overlayitem = new OverlayItem(geoPoint, Formatteur.formatterChaine(station.name), station.bikesavailable + " / " + placesTotales);
			itemizedoverlay.addOverlay(overlayitem, station);
		}
		mapOverlays.add(itemizedoverlay);
		mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		mc.setZoom(14);

		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapOverlays.add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
