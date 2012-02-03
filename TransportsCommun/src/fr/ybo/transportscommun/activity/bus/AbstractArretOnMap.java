package fr.ybo.transportscommun.activity.bus;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseMapActivity;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.map.MapItemizedOverlayArret;
import fr.ybo.transportscommun.util.FixedMyLocationOverlay;
import fr.ybo.transportscommun.util.IconeLigne;

public abstract class AbstractArretOnMap extends BaseMapActivity {

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		Ligne myLigne = (Ligne) getIntent().getSerializableExtra("ligne");
		String currentDirection = getIntent().getStringExtra("direction");

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();

		// Creation du geo point
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(IconeLigne.getMarkeeResource(myLigne.nomCourt));
		MapItemizedOverlayArret itemizedoverlay = new MapItemizedOverlayArret(drawable, this, myLigne.id,
				currentDirection);
		List<String> selectionArgs = new ArrayList<String>(2);
		selectionArgs.add(myLigne.id);
		StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as _id, Arret.nom as arretName,");
		requete.append(" Direction.direction as direction, Arret.latitude as latitude,");
		requete.append(" Arret.longitude, ArretRoute.macroDirection ");
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
		Cursor cursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
				selectionArgs);
		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;

		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			String nom = cursor.getString(cursor.getColumnIndex("arretName"));
			String direction = cursor.getString(cursor.getColumnIndex("direction"));
			int macroDirection = cursor.getInt(cursor.getColumnIndex("macroDirection"));
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
			arretFavori.macroDirection = macroDirection;
			itemizedoverlay.addOverlay(overlayitem, arretFavori);
		}
		cursor.close();
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
