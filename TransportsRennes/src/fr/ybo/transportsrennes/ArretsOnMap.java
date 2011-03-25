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

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.map.MapItemizedOverlayArret;
import fr.ybo.transportsrennes.util.IconeLigne;

public class ArretsOnMap extends MapActivity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Ligne myLigne = (Ligne) getIntent().getSerializableExtra("ligne");
		String currentDirection = getIntent().getStringExtra("direction");

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		mapView.setSatellite(true);

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(IconeLigne.getMarkeeResource(myLigne.nomCourt));
		MapItemizedOverlayArret itemizedoverlay = new MapItemizedOverlayArret(drawable, this);
		List<String> selectionArgs = new ArrayList<String>(2);
		selectionArgs.add(myLigne.id);
		StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as _id, Arret.nom as arretName,");
		requete.append(" Direction.direction as direction, Arret.latitude as latitude, Arret.longitude ");
		requete.append("from ArretRoute, Arret, Direction ");
		requete.append("where");
		requete.append(" ArretRoute.ligneId = :ligneId");
		requete.append(" and ArretRoute.arretId = Arret.id");
		requete.append(" and ArretRoute.directionId = Direction.id");
		if (currentDirection != null) {
			requete.append(" and Direction.direction = :direction");
			selectionArgs.add(currentDirection);
		}
		requete.append(" order by ArretRoute.sequence");
		Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;


		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			String nom = cursor.getString(cursor.getColumnIndex("arretName"));
			String direction = cursor.getString(cursor.getColumnIndex("direction"));
			int latitude = (int) (cursor.getDouble(cursor.getColumnIndex("latitude")) * 1.0E6);
			int longitude = (int) (cursor.getDouble(cursor.getColumnIndex("longitude")) * 1.0E6);
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

			OverlayItem overlayitem = new OverlayItem(geoPoint, nom, direction);
			ArretFavori arretFavori = new ArretFavori();
			arretFavori.direction = direction;
			arretFavori.nomArret = nom;
			arretFavori.ligneId = myLigne.id;
			arretFavori.nomCourt = myLigne.nomCourt;
			arretFavori.nomLong = myLigne.nomLong;
			arretFavori.arretId = id;
			itemizedoverlay.addOverlay(overlayitem, arretFavori);
		}
		cursor.close();
		mapOverlays.add(itemizedoverlay);
		mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		mc.setZoom(14);

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapOverlays.add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();

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
