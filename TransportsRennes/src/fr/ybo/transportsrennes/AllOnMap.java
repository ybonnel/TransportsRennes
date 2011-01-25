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

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.map.MyGeoClusterer;
import fr.ybo.transportsrennes.map.MyGeoItem;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.GeoClusterer;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.MarkerBitmap;

import java.util.ArrayList;
import java.util.List;

public class AllOnMap extends MapActivity {

	private MapView mapView;
	private MapController mapCtrl;
	private GeoClusterer clusterer;
	// marker icons
	private List<MarkerBitmap> markerIconBmps = new ArrayList<MarkerBitmap>();

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapView.setSatellite(true);
		mapCtrl = mapView.getController();

		markerIconBmps.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(55, 35), 20, 10));
		markerIconBmps.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(53, 35), 18, 100));
		markerIconBmps.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(54, 35), 15, 1000));
		markerIconBmps.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(53, 35), 12, 10000));

		float screenDensity = this.getResources().getDisplayMetrics().density;
		clusterer = new MyGeoClusterer(this, mapView, markerIconBmps, screenDensity);

		mapCtrl.setCenter(new GeoPoint(48109681, -1679277));
		mapCtrl.setZoom(14);

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();

		new BackgroundTasks().execute();
	}

	private class BackgroundTasks extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
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

			int arretIdIndex = cursor.getColumnIndex("arretId");
			int arretNomIndex = cursor.getColumnIndex("arretNom");
			int ligneIdIndex = cursor.getColumnIndex("ligneId");
			int ligneNomCourtIndex = cursor.getColumnIndex("ligneNomCourt");
			int ligneNomLongIndex = cursor.getColumnIndex("ligneNomLong");
			int directionIndex = cursor.getColumnIndex("direction");
			int latitudeIndex = cursor.getColumnIndex("latitude");
			int longitudeIndex = cursor.getColumnIndex("longitude");
			Arret arret;
			int idGeoItem = 0;
			while (cursor.moveToNext()) {
				arret = new Arret();
				arret.id = cursor.getString(arretIdIndex);
				arret.nom = cursor.getString(arretNomIndex);
				arret.latitude = cursor.getDouble(latitudeIndex);
				arret.longitude = cursor.getDouble(longitudeIndex);
				arret.favori = new ArretFavori();
				arret.favori.direction = cursor.getString(directionIndex);
				arret.favori.ligneId = cursor.getString(ligneIdIndex);
				arret.favori.nomCourt = cursor.getString(ligneNomCourtIndex);
				arret.favori.nomLong = cursor.getString(ligneNomLongIndex);
				arret.favori.nomArret = arret.nom;
				arret.favori.arretId = arret.id;
				clusterer.addItem(new MyGeoItem(idGeoItem++, arret));
			}
			cursor.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			mapView.invalidate();
			clusterer.resetViewport();
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
