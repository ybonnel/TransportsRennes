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
import android.view.View;
import android.widget.CheckBox;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.map.MyGeoClusterer;
import fr.ybo.transportsrennes.map.MyGeoItem;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.MarkerBitmap;

import java.util.ArrayList;
import java.util.List;

public class AllOnMap extends MapActivity {

	private MapView mapView;
	private MyGeoClusterer clustererForArret;
	private MyGeoClusterer clustererForVelo;
	private MyGeoClusterer clustererForParc;
	// marker icons
	private List<MarkerBitmap> markerIconBmpsForArrets = new ArrayList<MarkerBitmap>();
	private List<MarkerBitmap> markerIconBmpsForVelo = new ArrayList<MarkerBitmap>();
	private List<MarkerBitmap> markerIconBmpsForParc = new ArrayList<MarkerBitmap>();
	private float screenDensity;

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
		MapController mapCtrl = mapView.getController();

		markerIconBmpsForArrets.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(35, 59), 20, 10));
		markerIconBmpsForArrets.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(35, 58), 18, 100));
		markerIconBmpsForArrets.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(35, 58), 15, 1000));
		markerIconBmpsForArrets.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus_inverse), new Point(35, 58), 12, 10000));

		markerIconBmpsForVelo.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo_inverse), new Point(35, 59), 20, 10));
		markerIconBmpsForVelo.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo_inverse), new Point(35, 58), 18, 100));
		markerIconBmpsForVelo.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo_inverse), new Point(35, 58), 15, 1000));

		markerIconBmpsForParc.add(new MarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icone_parc),
				BitmapFactory.decodeResource(getResources(), R.drawable.icone_parc_inverse), new Point(35, 59), 20, 10));


		screenDensity = this.getResources().getDisplayMetrics().density;

		mapCtrl.setCenter(new GeoPoint(48109681, -1679277));
		mapCtrl.setZoom(14);

		updateOverlays();
	}

	private boolean arretVisible = true;
	private boolean veloVisible = true;
	private boolean parcVisible = true;

	private void updateOverlays() {
		mapView.getOverlays().clear();
		if (myLocationOverlay == null) {
			myLocationOverlay = new MyLocationOverlay(this, mapView);
		}
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();
		if (arretVisible) {
			clustererForArret = new MyGeoClusterer<Arret>(this, mapView, markerIconBmpsForArrets, screenDensity, "arrets", ListArretByPosition.class);
		}
		if (veloVisible) {
			clustererForVelo =
					new MyGeoClusterer<Station>(this, mapView, markerIconBmpsForVelo, screenDensity, "stations", ListStationsByPosition.class);
		}
		if (parcVisible) {
			clustererForParc = new MyGeoClusterer<ParkRelai>(this, mapView, markerIconBmpsForParc, screenDensity, "parcRelais", ListParkRelais.class);
		}
		new BackgroundTasks().execute();
	}

	private List<Arret> arrets = null;

	private void ajouterArrets() {
		if (!arretVisible) {
			return;
		}
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
			arrets = new ArrayList<Arret>();
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
				arrets.add(arret);
				clustererForArret.addItem(new MyGeoItem<Arret>(idGeoItem++, arret));
			}
			cursor.close();
		} else {
			int idGeoItem = 0;
			for (Arret arret : arrets) {
				clustererForArret.addItem(new MyGeoItem<Arret>(idGeoItem++, arret));
			}
		}
	}

	private List<Station> stations = null;

	private void ajouterVelos() {
		if (!veloVisible) {
			return;
		}
		if (stations == null) {
			stations = Keolis.getInstance().getStations();
		}
		int idGeoItem = 0;
		for (Station station : stations) {
			clustererForVelo.addItem(new MyGeoItem<Station>(idGeoItem++, station));
		}
	}

	private List<ParkRelai> parcRelais = null;

	private void ajouterParcs() {
		if (!parcVisible) {
			return;
		}
		if (parcRelais == null) {
			parcRelais = Keolis.getInstance().getParkRelais();
		}
		int idGeoItem = 0;
		for (ParkRelai parcRelai : parcRelais) {
			clustererForParc.addItem(new MyGeoItem<ParkRelai>(idGeoItem++, parcRelai));
		}
	}

	private boolean gestionCheckBoxFaite = false;

	private class BackgroundTasks extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			ajouterArrets();
			ajouterVelos();
			ajouterParcs();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			if (!gestionCheckBoxFaite) {
				findViewById(R.id.allCheckBox).setVisibility(View.VISIBLE);
				CheckBox checkBoxBus = (CheckBox) findViewById(R.id.checkBus);
				CheckBox checkBoxVelo = (CheckBox) findViewById(R.id.checkVelo);
				CheckBox checkBoxParc = (CheckBox) findViewById(R.id.checkParc);
				checkBoxBus.setChecked(true);
				checkBoxVelo.setChecked(true);
				checkBoxParc.setChecked(true);
				checkBoxBus.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						arretVisible = ((CheckBox) view).isChecked();
						updateOverlays();
					}
				});
				checkBoxVelo.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						veloVisible = ((CheckBox) view).isChecked();
						updateOverlays();
					}
				});
				checkBoxParc.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						parcVisible = ((CheckBox) view).isChecked();
						updateOverlays();
					}
				});
				gestionCheckBoxFaite = true;
			}
			if (arretVisible) {
				clustererForArret.resetViewport();
			}
			if (veloVisible) {
				clustererForVelo.resetViewport();
			}
			if (parcVisible) {
				clustererForParc.resetViewport();
			}
			mapView.invalidate();
		}
	}

	private MyLocationOverlay myLocationOverlay = null;

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
