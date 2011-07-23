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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.Leg;
import fr.ybo.opentripplanner.client.modele.TraverseMode;
import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.map.LineItemizedOverlay;
import fr.ybo.transportsbordeaux.map.MapItemizedOverlayTrajet;
import fr.ybo.transportsbordeaux.util.Coordinate;
import fr.ybo.transportsbordeaux.util.FixedMyLocationOverlay;
import fr.ybo.transportsbordeaux.util.IconeLigne;
import fr.ybo.transportsbordeaux.util.PolylineEncoder;

public class TrajetOnMap extends MenuAccueil.MapActivity {

	private static final SimpleDateFormat SDF_HEURE = new SimpleDateFormat("HH:mm");

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trajet_map);
		Itinerary trajet = (Itinerary) getIntent().getSerializableExtra("trajet");

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		LinearLayout layoutTrajet = (LinearLayout) findViewById(R.id.trajetDetail);
		layoutTrajet.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(this);
		if (trajet.legs != null) {
			int minLatitude = Integer.MAX_VALUE;
			int maxLatitude = Integer.MIN_VALUE;
			int minLongitude = Integer.MAX_VALUE;
			int maxLongitude = Integer.MIN_VALUE;
			int latitude;
			int longitude;
			ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
			for (Leg leg : trajet.legs.leg) {
				// icône
				RelativeLayout portionLayout = (RelativeLayout) inflater.inflate(R.layout.portion_trajet, null);
				TextView directionTrajet = (TextView) portionLayout.findViewById(R.id.directionTrajet);
				int iconeMarkee;
				int icone;
				if (TraverseMode.valueOf(leg.mode).isOnStreetNonTransit()) {
					iconeMarkee = R.drawable.mpieton;
					icone = R.drawable.ipieton;
					directionTrajet.setVisibility(View.GONE);
				} else {
					String route = leg.route;
					if (route.length() == 1 && route.charAt(0) >= '1' && route.charAt(0) <= '9') {
						route = "0" + route;
					}
					iconeMarkee = IconeLigne.getMarkeeResource(route);
					icone = IconeLigne.getIconeResource(route);

					directionTrajet.setVisibility(View.VISIBLE);
					directionTrajet.setText(getString(R.string.directionEntete) + ' ' + leg.getDirection());
				}

				// icône du départ.
				MapItemizedOverlayTrajet itemizedOverlay = new MapItemizedOverlayTrajet(getResources().getDrawable(
						iconeMarkee));
				latitude = (int) (leg.from.lat * 1.0E6);
				longitude = (int) (leg.from.lon * 1.0E6);
				if (latitude > maxLatitude) {
					maxLatitude = latitude;
				}
				if (latitude < minLatitude) {
					minLatitude = latitude;
				}
				if (longitude > maxLongitude) {
					maxLongitude = longitude;
				}
				if (longitude < minLongitude) {
					minLongitude = longitude;
				}
				OverlayItem item = new OverlayItem(new GeoPoint(latitude, longitude), leg.from.name, null);
				itemizedOverlay.addOverlay(item);
				mapOverlays.add(itemizedOverlay);

				for (Coordinate coordinate : PolylineEncoder.decode(leg.legGeometry)) {
					geoPoints.add(coordinate.toGeoPoint());
				}

				// Détail du trajet.
				((ImageView) portionLayout.findViewById(R.id.iconePortion)).setImageResource(icone);
				((TextView) portionLayout.findViewById(R.id.departHeure)).setText(SDF_HEURE.format(leg.startTime));
				((TextView) portionLayout.findViewById(R.id.depart)).setText(leg.from.name);
				((TextView) portionLayout.findViewById(R.id.arriveeHeure)).setText(SDF_HEURE.format(leg.endTime));
				((TextView) portionLayout.findViewById(R.id.arrivee)).setText(leg.to.name);
				layoutTrajet.addView(portionLayout);
			}
			// icone de l'arrivee.
			Leg leg = trajet.legs.leg.get(trajet.legs.leg.size() - 1);
			MapItemizedOverlayTrajet itemizedOverlay = new MapItemizedOverlayTrajet(getResources().getDrawable(
					R.drawable.mpieton));
			latitude = (int) (leg.to.lat * 1.0E6);
			longitude = (int) (leg.to.lon * 1.0E6);
			if (latitude > maxLatitude) {
				maxLatitude = latitude;
			}
			if (latitude < minLatitude) {
				minLatitude = latitude;
			}
			if (longitude > maxLongitude) {
				maxLongitude = longitude;
			}
			if (longitude < minLongitude) {
				minLongitude = longitude;
			}
			OverlayItem item = new OverlayItem(new GeoPoint(latitude, longitude), leg.to.name, null);
			itemizedOverlay.addOverlay(item);
			mapOverlays.add(itemizedOverlay);
			
			LineItemizedOverlay lineItemizedOverlay = new LineItemizedOverlay(geoPoints);
			mapOverlays.add(lineItemizedOverlay);

			mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		}
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
