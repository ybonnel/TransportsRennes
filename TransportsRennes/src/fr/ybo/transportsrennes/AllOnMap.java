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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.map.MyGeoClusterer;
import fr.ybo.transportsrennes.map.MyGeoItem;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.MarkerBitmap;
import fr.ybo.transportsrennes.util.ErreurReseau;

public class AllOnMap extends MapActivity {

	private MapView mapView;
	private MyGeoClusterer<Arret> clustererForArret;
	private MyGeoClusterer<Station> clustererForVelo;
	private MyGeoClusterer<ParkRelai> clustererForParc;
	private MyGeoClusterer<PointDeVente> clustererForPos;
	// marker icons
	private final List<MarkerBitmap> markerIconBmpsForArrets = new ArrayList<MarkerBitmap>(2);
	private final List<MarkerBitmap> markerIconBmpsForVelo = new ArrayList<MarkerBitmap>(1);
	private final List<MarkerBitmap> markerIconBmpsForParc = new ArrayList<MarkerBitmap>(1);
	private final List<MarkerBitmap> markerIconBmpsForPos = new ArrayList<MarkerBitmap>(1);
	private float screenDensity;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		boolean afficheMessage = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("AllOnMap_dialog", true);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapView.setSatellite(true);
		MapController mapCtrl = mapView.getController();

		Bitmap bitmapBus = BitmapFactory.decodeResource(getResources(), R.drawable.icone_bus);
		markerIconBmpsForArrets.add(new MarkerBitmap(bitmapBus, bitmapBus, new Point(25, 35), 20, 100));
		markerIconBmpsForArrets.add(new MarkerBitmap(bitmapBus, bitmapBus, new Point(25, 35), 18, 10000));

		Bitmap bitmapVelo = BitmapFactory.decodeResource(getResources(), R.drawable.icone_velo);
		markerIconBmpsForVelo.add(new MarkerBitmap(bitmapVelo, bitmapVelo, new Point(25, 35), 20, 100));

		Bitmap bitmapParc = BitmapFactory.decodeResource(getResources(), R.drawable.icone_parc);
		markerIconBmpsForParc.add(new MarkerBitmap(bitmapParc, bitmapParc, new Point(25, 35), 20, 10));

		Bitmap bitmapPos = BitmapFactory.decodeResource(getResources(), R.drawable.icone_pos);
		markerIconBmpsForPos.add(new MarkerBitmap(bitmapPos, bitmapPos, new Point(25, 35), 20, 1000));

		screenDensity = getResources().getDisplayMetrics().density;

		mapCtrl.setCenter(new GeoPoint(48109681, -1679277));
		mapCtrl.setZoom(14);

		if (afficheMessage) {
			showDialog();
			saveAfficheMessage();
		}

		updateOverlays();
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(LayoutInflater.from(this).inflate(R.layout.infoallinmap, null));
		builder.setTitle(R.string.titleInfoAllInMap);
		builder.setCancelable(false);
		builder.setNeutralButton(getString(R.string.Terminer), new AllOnMap.TerminerClickListener());
		builder.create().show();
	}

	private void saveAfficheMessage() {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean("AllOnMap_dialog", false);
		editor.commit();
	}

	private boolean arretVisible = true;
	private boolean veloVisible = true;
	private boolean parcVisible = true;
	private boolean posVisible = true;

	private void updateOverlays() {
		mapView.getOverlays().clear();
		if (myLocationOverlay == null) {
			myLocationOverlay = new MyLocationOverlay(this, mapView);
		}
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();
		if (arretVisible) {
			clustererForArret = new MyGeoClusterer<Arret>(this, mapView, markerIconBmpsForArrets, screenDensity,
					"arrets", ListArretByPosition.class);
		}
		if (veloVisible) {
			clustererForVelo = new MyGeoClusterer<Station>(this, mapView, markerIconBmpsForVelo, screenDensity,
					"stations", ListStationsByPosition.class);
		}
		if (parcVisible) {
			clustererForParc = new MyGeoClusterer<ParkRelai>(this, mapView, markerIconBmpsForParc, screenDensity,
					"parcRelais", ListParkRelais.class);
		}
		if (posVisible) {
			clustererForPos = new MyGeoClusterer<PointDeVente>(this, mapView, markerIconBmpsForPos, screenDensity,
					"pointsDeVente", ListPointsDeVente.class);
		}
		new AllOnMap.BackgroundTasks().execute();
	}

	private List<Arret> arrets;

	private List<Station> stations;

	private List<ParkRelai> parcRelais;

	private List<PointDeVente> pointsDeVente;

	private boolean gestionCheckBoxFaite;

	private static class TerminerClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialogInterface, int i) {
			dialogInterface.cancel();
		}
	}

	private class BackgroundTasks extends AsyncTask<Void, Void, Void> {

		private void ajouterArrets() {
			if (!arretVisible) {
				return;
			}
			if (arrets == null) {
				StringBuilder requete = new StringBuilder();
				requete.append("select Arret.id as arretId, Arret.nom as arretNom,");
				requete.append(" Ligne.id as ligneId, Ligne.nomCourt as ligneNomCourt,");
				requete.append(" Ligne.nomLong as ligneNomLong, Direction.direction as direction,");
				requete.append(" Arret.latitude as latitude, Arret.longitude as longitude, ArretRoute.macroDirection as macroDirection ");
				requete.append("from ArretRoute, Arret, Direction, Ligne ");
				requete.append("where");
				requete.append(" ArretRoute.arretId = Arret.id");
				requete.append(" and ArretRoute.directionId = Direction.id");
				requete.append(" and Ligne.id = ArretRoute.ligneId");
				requete.append(" order by ArretRoute.sequence");
				Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
						null);

				int arretIdIndex = cursor.getColumnIndex("arretId");
				int arretNomIndex = cursor.getColumnIndex("arretNom");
				int ligneIdIndex = cursor.getColumnIndex("ligneId");
				int ligneNomCourtIndex = cursor.getColumnIndex("ligneNomCourt");
				int ligneNomLongIndex = cursor.getColumnIndex("ligneNomLong");
				int directionIndex = cursor.getColumnIndex("direction");
				int latitudeIndex = cursor.getColumnIndex("latitude");
				int longitudeIndex = cursor.getColumnIndex("longitude");
				int macroDirectionIndex = cursor.getColumnIndex("macroDirection");
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
					arret.favori.arretId = arret.id;
					arret.favori.macroDirection = cursor.getInt(macroDirectionIndex);
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
		}

		private void ajouterVelos() throws ErreurReseau {
			if (veloVisible) {
				if (stations == null) {
					stations = Keolis.getInstance().getStations();
				}
				int idGeoItem = 0;
				for (Station station : stations) {
					clustererForVelo.addItem(new MyGeoItem<Station>(idGeoItem, station));
					idGeoItem++;
				}
			}
		}

		private void ajouterParcs() throws ErreurReseau {
			if (parcVisible) {
				if (parcRelais == null) {
					parcRelais = Keolis.getInstance().getParkRelais();
				}
				int idGeoItem = 0;
				for (ParkRelai parcRelai : parcRelais) {
					clustererForParc.addItem(new MyGeoItem<ParkRelai>(idGeoItem, parcRelai));
					idGeoItem++;
				}
			}
		}

		private void ajouterPos() throws ErreurReseau {
			if (posVisible) {
				if (pointsDeVente == null) {
					pointsDeVente = Keolis.getInstance().getPointDeVente();
				}
				int idGeoItem = 0;
				for (PointDeVente pointDeVente : pointsDeVente) {
					clustererForPos.addItem(new MyGeoItem<PointDeVente>(idGeoItem, pointDeVente));
					idGeoItem++;
				}
			}
		}

		private boolean erreurKeolis;

		@Override
		protected Void doInBackground(Void... voids) {
			ajouterArrets();
			try {
				ajouterVelos();
				ajouterParcs();
				ajouterPos();
			} catch (ErreurReseau erreurReseau) {
				erreurKeolis = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!gestionCheckBoxFaite) {
				findViewById(R.id.allCheckBox).setVisibility(View.VISIBLE);
				CheckBox checkBoxBus = (CheckBox) findViewById(R.id.checkBus);
				CheckBox checkBoxVelo = (CheckBox) findViewById(R.id.checkVelo);
				CheckBox checkBoxParc = (CheckBox) findViewById(R.id.checkParc);
				CheckBox checkBoxPos = (CheckBox) findViewById(R.id.checkPos);
				checkBoxBus.setChecked(true);
				checkBoxVelo.setChecked(true);
				checkBoxParc.setChecked(true);
				checkBoxPos.setChecked(true);
				checkBoxBus.setOnClickListener(new AllOnMap.BusCheckBoxListener());
				checkBoxVelo.setOnClickListener(new AllOnMap.VeloCheckBoxListener());
				checkBoxParc.setOnClickListener(new AllOnMap.ParcCheckBoxListener());
				checkBoxPos.setOnClickListener(new AllOnMap.PosCheckBoxListener());
				gestionCheckBoxFaite = true;
			}
			if (erreurKeolis) {
				Toast.makeText(AllOnMap.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
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
			if (posVisible) {
				clustererForPos.resetViewport();
			}
			mapView.invalidate();
		}
	}

	private class BusCheckBoxListener implements View.OnClickListener {
		public void onClick(View view) {
			arretVisible = ((Checkable) view).isChecked();
			updateOverlays();
		}
	}

	private class VeloCheckBoxListener implements View.OnClickListener {
		public void onClick(View view) {
			veloVisible = ((Checkable) view).isChecked();
			updateOverlays();
		}
	}

	private class ParcCheckBoxListener implements View.OnClickListener {
		public void onClick(View view) {
			parcVisible = ((Checkable) view).isChecked();
			updateOverlays();
		}
	}

	private class PosCheckBoxListener implements View.OnClickListener {
		public void onClick(View view) {
			posVisible = ((Checkable) view).isChecked();
			updateOverlays();
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

	private static final int GROUP_ID = 0;
	private static final int MENU_ID = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_apropos);
		item.setIcon(android.R.drawable.ic_menu_info_details);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		boolean retour = false;
		if (item.getItemId() == MENU_ID) {
			showDialog();
			retour = true;
		}
		return retour;
	}
}
