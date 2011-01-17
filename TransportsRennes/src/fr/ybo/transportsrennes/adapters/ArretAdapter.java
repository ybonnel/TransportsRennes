/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.DetailArret;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.activity.OnClickFavoriGestionnaire;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.LogYbo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapteur pour les arrêts.
 */
public class ArretAdapter extends CursorAdapter {

	private Ligne ligne;
	private ArretFavori favori;
	private Activity activity;

	private final static double DISTANCE_RECHERCHE_METRE = 1000.0;
	private final static double DEGREE_LATITUDE_EN_METRES = 111192.62;
	private final static double distanceLatitudeInDegree = DISTANCE_RECHERCHE_METRE / DEGREE_LATITUDE_EN_METRES;
	private final static double DEGREE_LONGITUDE_EN_METRES = 74452.10;
	private final static double distanceLongitudeInDegree = DISTANCE_RECHERCHE_METRE / DEGREE_LONGITUDE_EN_METRES;
	private final static int DISTANCE_MAX_METRE = 151;
	private final static Class<?> classDrawable = R.drawable.class;

	private final static LogYbo LOG_YBO = new LogYbo(ArretAdapter.class);

	private final LayoutInflater mInflater;

	public ArretAdapter(Activity activity, Cursor cursor, Ligne ligne) {
		super(activity, cursor);
		this.ligne = ligne;
		favori = new ArretFavori();
		favori.ligneId = this.ligne.id;
		this.activity = activity;
		mInflater = LayoutInflater.from(activity);
	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		int nameCol = cursor.getColumnIndex("arretName");
		String name = cursor.getString(nameCol);
		int directionCol = cursor.getColumnIndex("direction");
		String direction = cursor.getString(directionCol);
		int arretIdCol = cursor.getColumnIndex("_id");
		favori.arretId = cursor.getString(arretIdCol);
		final String arretId = favori.arretId;
		((TextView) view.findViewById(R.id.nomArret)).setText(name);
		((TextView) view.findViewById(R.id.directionArret)).setText("vers " + direction);
		final ImageView imageView = ((ImageView) view.findViewById(R.id.isfavori));
		imageView.setImageResource(
				TransportsRennesApplication.getDataBaseHelper().selectSingle(favori) == null ? android.R.drawable.btn_star_big_off :
						android.R.drawable.btn_star_big_on);
		imageView.setOnClickListener(new OnClickFavoriGestionnaire(ligne, favori.arretId, name, direction, activity));
		final ImageView correspondance = ((ImageView) view.findViewById(R.id.imageCorrespondance));
		final LinearLayout detailCorrespondance = ((LinearLayout) view.findViewById(R.id.detailCorrespondance));
		correspondance.setImageResource(R.drawable.arrow_right_float);
		detailCorrespondance.removeAllViews();
		detailCorrespondance.setVisibility(View.INVISIBLE);
		correspondance.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (detailCorrespondance.getVisibility() == View.VISIBLE) {
					correspondance.setImageResource(R.drawable.arrow_right_float);
					detailCorrespondance.removeAllViews();
					detailCorrespondance.setVisibility(View.INVISIBLE);
				} else {
					detailCorrespondance.setVisibility(View.VISIBLE);
					detailCorrespondance.removeAllViews();
					construireCorrespondance(detailCorrespondance, arretId);
					correspondance.setImageResource(R.drawable.arrow_down_float);
				}
			}
		});

	}

	private void construireCorrespondance(LinearLayout detailCorrespondance, String arretId) {
		/* Recuperation de l'arretCourant */
		Arret arretCourant = new Arret();
		arretCourant.id = arretId;
		arretCourant = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretCourant);
		Location locationArret = new Location("myProvider");
		locationArret.setLatitude(arretCourant.latitude);
		locationArret.setLongitude(arretCourant.longitude);

		/** Construction requête. */
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT Arret.id as arretId, ArretRoute.ligneId as ligneId, Direction.direction as direction,");
		requete.append(
				" Arret.nom as arretNom, Arret.latitude as latitude, Arret.longitude as longitude, Ligne.nomCourt as nomCourt, Ligne.nomLong as nomLong ");
		requete.append("FROM Arret, ArretRoute, Direction, Ligne ");
		requete.append("WHERE Arret.id = ArretRoute.arretId and Direction.id = ArretRoute.directionId AND Ligne.id = ArretRoute.ligneId");
		requete.append(" AND Arret.latitude > :minLatitude AND Arret.latitude < :maxLatitude");
		requete.append(" AND Arret.longitude > :minLongitude AND Arret.longitude < :maxLongitude");

		/** Paramètres de la requête */
		double minLatitude = arretCourant.latitude - distanceLatitudeInDegree;
		double maxLatitude = arretCourant.latitude + distanceLatitudeInDegree;
		double minLongitude = arretCourant.longitude - distanceLongitudeInDegree;
		double maxLongitude = arretCourant.longitude + distanceLongitudeInDegree;
		ArrayList<String> selectionArgs = new ArrayList<String>(4);
		selectionArgs.add(String.valueOf(minLatitude));
		selectionArgs.add(String.valueOf(maxLatitude));
		selectionArgs.add(String.valueOf(minLongitude));
		selectionArgs.add(String.valueOf(maxLongitude));

		LOG_YBO.debug("Exectution de : " + requete.toString());
		Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		LOG_YBO.debug("Resultat : " + cursor.getCount());

		/** Recuperation des index dans le cussor */
		int arretIdIndex = cursor.getColumnIndex("arretId");
		int ligneIdIndex = cursor.getColumnIndex("ligneId");
		int directionIndex = cursor.getColumnIndex("direction");
		int arretNomIndex = cursor.getColumnIndex("arretNom");
		int latitudeIndex = cursor.getColumnIndex("latitude");
		int longitudeIndex = cursor.getColumnIndex("longitude");
		int nomCourtIndex = cursor.getColumnIndex("nomCourt");
		int nomLongIndex = cursor.getColumnIndex("nomLong");

		List<Arret> arrets = new ArrayList<Arret>();

		while (cursor.moveToNext()) {
			Arret arret = new Arret();
			arret.id = cursor.getString(arretIdIndex);
			arret.favori = new ArretFavori();
			arret.favori.arretId = arret.id;
			arret.favori.ligneId = cursor.getString(ligneIdIndex);
			arret.favori.direction = cursor.getString(directionIndex);
			arret.nom = cursor.getString(arretNomIndex);
			arret.favori.nomArret = arret.nom;
			arret.latitude = cursor.getDouble(latitudeIndex);
			arret.longitude = cursor.getDouble(longitudeIndex);
			arret.favori.nomCourt = cursor.getString(nomCourtIndex);
			arret.favori.nomLong = cursor.getString(nomLongIndex);
			if (!arret.id.equals(favori.arretId) || !arret.favori.ligneId.equals(favori.ligneId)) {
				arret.calculDistance(locationArret);
				if (arret.distance < DISTANCE_MAX_METRE) {
					arrets.add(arret);
				}
			}
		}
		cursor.close();

		Collections.sort(arrets, new Arret.ComparatorDistance());

		for (final Arret arret : arrets) {
			RelativeLayout relativeLayout = (RelativeLayout) mInflater.inflate(R.layout.arretgps, null);
			LinearLayout conteneur = (LinearLayout) relativeLayout.findViewById(R.id.conteneurImage);
			try {
				Field fieldIcon = classDrawable.getDeclaredField("i" + arret.favori.nomCourt.toLowerCase());
				int ressourceImg = fieldIcon.getInt(null);
				ImageView imgView = new ImageView(activity);
				imgView.setImageResource(ressourceImg);
				conteneur.addView(imgView);
			} catch (Exception ignore) {
			}
			TextView arretDirection = (TextView) relativeLayout.findViewById(R.id.arretgps_direction);
			arretDirection.setText(arret.favori.direction);
			TextView nomArret = (TextView) relativeLayout.findViewById(R.id.arretgps_nomArret);
			nomArret.setText(arret.nom);
			TextView distance = (TextView) relativeLayout.findViewById(R.id.arretgps_distance);
			distance.setText(arret.formatDistance());
			relativeLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(activity, DetailArret.class);
					intent.putExtra("favori", arret.favori);
					activity.startActivity(intent);
				}
			});
			detailCorrespondance.addView(relativeLayout);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(R.layout.arret, parent, false);

	}

}
