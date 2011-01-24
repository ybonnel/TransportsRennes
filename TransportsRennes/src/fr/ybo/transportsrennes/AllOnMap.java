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

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.map.MapItemizedOverlayArret;
import fr.ybo.transportsrennes.util.IconeLigne;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllOnMap extends MapActivity {

	private MapController mc;

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		Map<String, MapItemizedOverlayArret> mapItemizedOverlays = new HashMap<String, MapItemizedOverlayArret>();

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(false);

		mc = mapView.getController();
		mapView.setSatellite(true);

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as arretId, Arret.nom as arretNom,");
		requete.append(" Ligne.id as ligneId, Ligne.nomCourt as ligneNomCourt,");
		requete.append(" Ligne.nomLong as ligneNomLong, Direction.direction as direction,");
		requete.append(" Arret.latitude as latitude, Arret.longitude as longitude ");
		requete.append("from ArretRoute, Arret, Direction, Ligne ");
		requete.append("where");
		requete.append(" ArretRoute.arretId = Arret.id");
		requete.append(" and ArretRoute.directionId = Direction.id");
		requete.append(" and Ligne.id = ArretRoute.ligneId");
		requete.append(" order by ArretRoute.sequence");
		Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), null);
		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;

		int arretIdIndex = cursor.getColumnIndex("arretId");
		int arretNomIndex = cursor.getColumnIndex("arretNom");
		int ligneIdIndex = cursor.getColumnIndex("ligneId");
		int ligneNomCourtIndex = cursor.getColumnIndex("ligneNomCourt");
		int ligneNomLongIndex = cursor.getColumnIndex("ligneNomLong");
		int directionIndex = cursor.getColumnIndex("direction");
		int latitudeIndex = cursor.getColumnIndex("latitude");
		int longitudeIndex = cursor.getColumnIndex("longitude");
		ArretFavori arretFavori;
		GeoPoint geoPoint;
		OverlayItem overlayitem;
		while (cursor.moveToNext()) {
			arretFavori = new ArretFavori();
			arretFavori.arretId = cursor.getString(arretIdIndex);
			arretFavori.nomArret = cursor.getString(arretNomIndex);
			arretFavori.direction = cursor.getString(directionIndex);
			arretFavori.ligneId = cursor.getString(ligneIdIndex);
			arretFavori.nomCourt = cursor.getString(ligneNomCourtIndex);
			arretFavori.nomLong = cursor.getString(ligneNomLongIndex);
			int latitude = (int) (cursor.getDouble(latitudeIndex) * 1E6);
			int longitude = (int) (cursor.getDouble(longitudeIndex) * 1E6);
			geoPoint = new GeoPoint(latitude, longitude);
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

			overlayitem = new OverlayItem(geoPoint, arretFavori.nomArret, arretFavori.direction);
			if (!mapItemizedOverlays.containsKey(arretFavori.nomCourt)) {
				mapItemizedOverlays.put(arretFavori.nomCourt,
						new MapItemizedOverlayArret(getResources().getDrawable(IconeLigne.getMarkeeResource(arretFavori.nomCourt)), this));
			}
			mapItemizedOverlays.get(arretFavori.nomCourt).addOverlay(overlayitem, arretFavori);
		}
		cursor.close();
		for (MapItemizedOverlayArret itemizedOverlay : mapItemizedOverlays.values()) {
			mapOverlays.add(itemizedOverlay);
		}

		mc.setCenter(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		mc.setZoom(17);

		myLocationOverlay = new MyLocationOverlayAnimate(this, mapView);
		mapOverlays.add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();
		touch = true;
		mapView.setOnTouchListener(new View.OnTouchListener(){
			public boolean onTouch(View view, MotionEvent motionEvent) {
				touch = false;
				return false;
			}
		});
	}

	private boolean touch;

	private class MyLocationOverlayAnimate extends MyLocationOverlay {

		public MyLocationOverlayAnimate(Context context, MapView mapView) {
			super(context, mapView);
		}

		@Override
		public void onLocationChanged(Location location) {
			super.onLocationChanged(location);
			if (!touch) {
				mc.setCenter(getMyLocation());
			}
		}
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
