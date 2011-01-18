/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.map.MapItemizedOverlayParking;
import fr.ybo.transportsrennes.map.MapItemizedOverlayVelo;
import fr.ybo.transportsrennes.util.Formatteur;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkRelaisOnMap extends MapActivity {

	protected static final Map<Integer, String> MAP_STATES = new HashMap<Integer, String>();

	static {
		MAP_STATES.put(1, "Fermé");
		MAP_STATES.put(2, "Complet");
		MAP_STATES.put(3, "Indisponible");
	}

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		List<ParkRelai> parkRelais = (List<ParkRelai>) getIntent().getExtras().getSerializable("parkRelais");

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		mapView.setSatellite(true);

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.markee_parking);
		MapItemizedOverlayParking itemizedoverlay = new MapItemizedOverlayParking(drawable, this);

		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;


		for (ParkRelai parkRelai : parkRelais) {
			int latitude = (int) (parkRelai.latitude * 1E6);
			int longitude = (int) (parkRelai.longitude * 1E6);
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
			String description = parkRelai.carParkAvailable + " / " + parkRelai.carParkCapacity;
			if (parkRelai.state != 0) {
				description = MAP_STATES.get(parkRelai.state);
			}
			OverlayItem overlayitem = new OverlayItem(geoPoint, Formatteur.formatterChaine(parkRelai.name), description);
			itemizedoverlay.addOverlay(overlayitem, parkRelai);
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
