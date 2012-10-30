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
package fr.ybo.transportsbordeaux.activity.map;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.bus.ListArretByPosition;
import fr.ybo.transportsbordeaux.activity.velos.ListStationsByPosition;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.map.MyGeoClusterer;
import fr.ybo.transportsbordeaux.map.MyGeoItem;
import fr.ybo.transportsbordeaux.map.mapviewutil.markerclusterer.MarkerBitmap;
import fr.ybo.transportsbordeaux.tbcapi.Keolis;
import fr.ybo.transportsbordeaux.tbcapi.modele.Station;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseMapActivity;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.FixedMyLocationOverlay;
import fr.ybo.transportscommun.util.LogYbo;

public class AllOnMap extends BaseMapActivity {

	private static final LogYbo LOG_YBO = new LogYbo(AllOnMap.class);

	private MapView mapView;
	private MyGeoClusterer<Arret> clustererForArret;
	private MyGeoClusterer<Station> clustererForVelo;
	// marker icons
	private final List<MarkerBitmap> markerIconBmpsForArrets = new ArrayList<MarkerBitmap>();
	private final List<MarkerBitmap> markerIconBmpsForVelo = new ArrayList<MarkerBitmap>();
	private float screenDensity;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		MapController mapCtrl = mapView.getController();

		Bitmap bitmapBus = BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus);
		markerIconBmpsForArrets.add(new MarkerBitmap(bitmapBus, bitmapBus, new Point(25, 35), 20, 100));
		markerIconBmpsForArrets.add(new MarkerBitmap(bitmapBus, bitmapBus, new Point(25, 35), 18, 10000));

		Bitmap bitmapVelo = BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo);
		markerIconBmpsForVelo.add(new MarkerBitmap(bitmapVelo, bitmapVelo, new Point(25, 35), 20, 100));

		screenDensity = getResources().getDisplayMetrics().density;

		// toPlace=44.825920698932%2C-0.58469463769264&fromPlace=44.830912280174%2C-0.57263542596218
		mapCtrl.setCenter(new GeoPoint(44825920, -584694));
		mapCtrl.setZoom(14);

		updateOverlays();
		gestionButtonLayout();
	}

	private void updateOverlays() {
		mapView.getOverlays().clear();
		if (myLocationOverlay == null) {
			myLocationOverlay = new FixedMyLocationOverlay(this, mapView);
		}
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();
		clustererForArret = new MyGeoClusterer<Arret>(this, mapView, markerIconBmpsForArrets, screenDensity, "arrets",
				ListArretByPosition.class);
		clustererForVelo = new MyGeoClusterer<Station>(this, mapView, markerIconBmpsForVelo, screenDensity, "stations",
				ListStationsByPosition.class);
		new AllOnMap.BackgroundTasks().execute((Void) null);
	}

	private List<Arret> arrets;

	private List<Station> stations;

	private class BackgroundTasks extends AsyncTask<Void, Void, Void> {

		private void ajouterArrets() {
			LOG_YBO.debug("ajouterArrets");
			if (arrets == null) {
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
				Cursor cursor = TransportsBordeauxApplication.getDataBaseHelper().executeSelectQuery(
						requete.toString(), null);

				int arretIdIndex = cursor.getColumnIndex("arretId");
				int arretNomIndex = cursor.getColumnIndex("arretNom");
				int ligneIdIndex = cursor.getColumnIndex("ligneId");
				int ligneNomCourtIndex = cursor.getColumnIndex("ligneNomCourt");
				int ligneNomLongIndex = cursor.getColumnIndex("ligneNomLong");
				int directionIndex = cursor.getColumnIndex("direction");
				int latitudeIndex = cursor.getColumnIndex("latitude");
				int longitudeIndex = cursor.getColumnIndex("longitude");
				arrets = new ArrayList<Arret>(1500);
				int idGeoItem = 0;
				while (cursor.moveToNext()) {
					Arret arret = new Arret();
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
					arret.favori.macroDirection = 0;
					arret.favori.arretId = arret.id;
					arrets.add(arret);
					clustererForArret.addItem(new MyGeoItem<Arret>(idGeoItem, arret));
					idGeoItem++;
				}
				cursor.close();
			} else {
				int idGeoItem = 0;
				for (Arret arret : arrets) {
					clustererForArret.addItem(new MyGeoItem<Arret>(idGeoItem, arret));
					idGeoItem++;
				}
			}
			LOG_YBO.debug("Nombre d'arrêts : " + arrets.size());
		}

		private void ajouterVelos() throws ErreurReseau {
			LOG_YBO.debug("ajouterVelo");
			if (stations == null) {
				stations = Keolis.getInstance().getStationsVcub();
			}
			int idGeoItem = 0;
			for (Station station : stations) {
				clustererForVelo.addItem(new MyGeoItem<Station>(idGeoItem, station));
				idGeoItem++;
			}
			LOG_YBO.debug("Nombre de stations : " + stations.size());
		}

		private boolean erreurKeolis;

		@Override
		protected Void doInBackground(Void... voids) {
			ajouterArrets();
			try {
				ajouterVelos();
			} catch (ErreurReseau erreurReseau) {
				erreurKeolis = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (erreurKeolis) {
				Toast.makeText(AllOnMap.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
			}
			clustererForArret.resetViewport();
			clustererForVelo.resetViewport();
			mapView.invalidate();
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
