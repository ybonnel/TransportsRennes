package fr.ybo.transportsrennes;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.maps.*;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.map.MapItemizedOverlay;

import java.util.ArrayList;
import java.util.List;

public class ArretsOnMap extends MapActivity {

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Route myRoute = (Route) getIntent().getSerializableExtra("route");
		String currentDirection = getIntent().getStringExtra("direction");

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		mapView.setSatellite(true);

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.markee);
		MapItemizedOverlay itemizedoverlay = new MapItemizedOverlay(drawable, this);
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(myRoute.getId());
		StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as _id, Arret.nom as arretName,");
		requete.append(" ArretRoute.direction as direction, Arret.latitude as latitude, Arret.longitude ");
		requete.append("from ArretRoute, Arret ");
		requete.append("where");
		requete.append(" ArretRoute.routeId = :routeId");
		requete.append(" and ArretRoute.arretId = Arret.id");
		if (currentDirection != null) {
			requete.append(" and ArretRoute.direction = :direction");
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
			int latitude = (int) (cursor.getDouble(cursor.getColumnIndex("latitude")) * 1E6);
			int longitude = (int) (cursor.getDouble(cursor.getColumnIndex("longitude")) * 1E6);
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
			arretFavori.setDirection(direction);
			arretFavori.setNomArret(nom);
			arretFavori.setRouteId(myRoute.getId());
			arretFavori.setRouteNomCourt(myRoute.getNomCourt());
			arretFavori.setRouteNomLong(myRoute.getNomLong());
			arretFavori.setStopId(id);
			itemizedoverlay.addOverlay(overlayitem, arretFavori);
		}
		cursor.close();
		mapOverlays.add(itemizedoverlay);
		mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		mc.setZoom(14);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
