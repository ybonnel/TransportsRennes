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

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.map.MapItemizedOverlayParking;
import fr.ybo.transportsbordeaux.modele.Parking;
import fr.ybo.transportsbordeaux.util.FixedMyLocationOverlay;
import fr.ybo.transportsbordeaux.util.Formatteur;

public class ParkingsOnMap extends MenuAccueil.MapActivity {

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		Iterable<Parking> parkRelais = (Iterable<Parking>) getIntent().getExtras().getSerializable("parkRelais");

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.markee_parking);
		MapItemizedOverlayParking itemizedoverlay = new MapItemizedOverlayParking(drawable, this);

		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;


		for (Parking parkRelai : parkRelais) {
			int latitude = (int) (parkRelai.latitude * 1.0E6);
			int longitude = (int) (parkRelai.longitude * 1.0E6);
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
			OverlayItem overlayitem = new OverlayItem(geoPoint, Formatteur.formatterChaine(parkRelai.name), description);
			itemizedoverlay.addOverlay(overlayitem, parkRelai);
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
}
