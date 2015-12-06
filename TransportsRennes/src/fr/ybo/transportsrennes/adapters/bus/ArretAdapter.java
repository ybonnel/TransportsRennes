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
package fr.ybo.transportsrennes.adapters.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.bus.DetailArret;
import fr.ybo.transportsrennes.activity.commun.OnClickFavoriGestionnaire;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

/**
 * Adapteur pour les arrêts.
 */
public class ArretAdapter extends CursorAdapter {

    private final Ligne ligne;
    private final ArretFavori favori;
    private final Activity activity;

    private static final double DISTANCE_RECHERCHE_METRE = 1000.0;
    private static final double DEGREE_LATITUDE_EN_METRES = 111192.62;
    private static final double DISTANCE_LAT_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LATITUDE_EN_METRES;
    private static final double DEGREE_LONGITUDE_EN_METRES = 74452.10;
    private static final double DISTANCE_LNG_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LONGITUDE_EN_METRES;
    private static final int DISTANCE_MAX_METRE = 151;

    private final Collection<String> setCorrespondances = new HashSet<String>(20);


    public ArretAdapter(final Activity activity, final Cursor cursor, final Ligne ligne) {
        super(activity, cursor);
        this.ligne = ligne;
        favori = new ArretFavori();
        favori.ligneId = this.ligne.id;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        nameCol = cursor.getColumnIndex("arretName");
        directionCol = cursor.getColumnIndex("direction");
        arretIdCol = cursor.getColumnIndex("_id");
        macroDirectionCol = cursor.getColumnIndex("macroDirection");
        accessibleCol = cursor.getColumnIndex("accessible");
    }

    private final LayoutInflater mInflater;
    private final int nameCol;
    private final int directionCol;
    private final int arretIdCol;
    private final int macroDirectionCol;
    private final int accessibleCol;


    private static class ViewHolder {
        private TextView nomArret;
        private TextView directionArret;
        private ImageView isFavori;
        private ImageView correspondance;
        private LinearLayout detailCorrespondance;
        private ImageView iconeHandicap;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.arret, parent, false);
        final ViewHolder holder = new ViewHolder();
        holder.nomArret = (TextView) view.findViewById(R.id.nomArret);
        holder.directionArret = (TextView) view.findViewById(R.id.directionArret);
        holder.isFavori = (ImageView) view.findViewById(R.id.isfavori);
        holder.correspondance = (ImageView) view.findViewById(R.id.imageCorrespondance);
        holder.detailCorrespondance = (LinearLayout) view.findViewById(R.id.detailCorrespondance);
        holder.iconeHandicap = (ImageView) view.findViewById(R.id.iconeHandicap);
        view.setTag(holder);
        return view;

    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final String name = cursor.getString(nameCol);
        final String direction = cursor.getString(directionCol);
        final boolean accessible = (cursor.getInt(accessibleCol) == 1);
        favori.arretId = cursor.getString(arretIdCol);
        favori.macroDirection = cursor.getInt(macroDirectionCol);
        final String arretId = favori.arretId;
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.nomArret.setText(name);
        holder.directionArret.setText(context.getString(R.string.vers) + ' ' + direction);
        holder.isFavori.setImageResource(
                TransportsRennesApplication.getDataBaseHelper().selectSingle(favori) == null ? android.R.drawable.btn_star_big_off :
                        android.R.drawable.btn_star_big_on);
        holder.isFavori.setOnClickListener(new OnClickFavoriGestionnaire(ligne, favori.arretId, name, direction,
                activity, favori.macroDirection));
        if (setCorrespondances.contains(arretId)) {
            correspondancesWithDetail(holder, arretId);
        } else {
            correspondancesNoDetail(holder);
        }
        holder.correspondance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View myView) {
                if (setCorrespondances.contains(arretId)) {
                    setCorrespondances.remove(arretId);
                    correspondancesNoDetail(holder);
                } else {
                    setCorrespondances.add(arretId);
                    correspondancesWithDetail(holder, arretId);
                }
            }
        });
        holder.iconeHandicap.setVisibility(accessible ? View.VISIBLE : View.GONE);
    }

    private static void correspondancesNoDetail(final ViewHolder holder) {
        holder.detailCorrespondance.removeAllViews();
        holder.detailCorrespondance.setVisibility(View.INVISIBLE);
        holder.correspondance.setImageResource(R.drawable.arrow_right_float);
    }

    private void correspondancesWithDetail(final ViewHolder holder, final String arretId) {
        holder.detailCorrespondance.setVisibility(View.VISIBLE);
        holder.detailCorrespondance.removeAllViews();
        construireCorrespondance(holder.detailCorrespondance, arretId);
        holder.correspondance.setImageResource(R.drawable.arrow_down_float);
    }

    private final Map<String, List<Arret>> mapDetailCorrespondances = new HashMap<String, List<Arret>>();

    private void construireRelativeLayouts(final Iterable<Arret> arrets, final ViewGroup detailCorrespondance) {
        for (final Arret arret : arrets) {
            final RelativeLayout relativeLayout = getRelativeLayout();
            final RelativeLayoutHolder holder = (RelativeLayoutHolder) relativeLayout.getTag();
            holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(arret.favori.nomCourt));
            holder.arretDirection.setText(arret.favori.direction);
            holder.nomArret.setText(arret.nom);
            holder.distance.setText(arret.formatDistance());
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Intent intent = new Intent(activity, DetailArret.class);
                    intent.putExtra("favori", arret.favori);
                    activity.startActivity(intent);
                }
            });
            detailCorrespondance.addView(relativeLayout);
        }
    }

    private void construireCorrespondance(final ViewGroup detailCorrespondance, final String arretId) {
        if (mapDetailCorrespondances.containsKey(arretId)) {
            construireRelativeLayouts(mapDetailCorrespondances.get(arretId), detailCorrespondance);
        } else {
            /* Recuperation de l'arretCourant */
            Arret arretCourant = new Arret();
            arretCourant.id = arretId;
            arretCourant = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretCourant);
            final Location locationArret = new Location("myProvider");
            locationArret.setLatitude(arretCourant.latitude);
            locationArret.setLongitude(arretCourant.longitude);

            /** Construction requête. */

            /** Paramètres de la requête */
            final double minLatitude = arretCourant.latitude - DISTANCE_LAT_IN_DEGREE;
            final double maxLatitude = arretCourant.latitude + DISTANCE_LAT_IN_DEGREE;
            final double minLongitude = arretCourant.longitude - DISTANCE_LNG_IN_DEGREE;
            final double maxLongitude = arretCourant.longitude + DISTANCE_LNG_IN_DEGREE;
            final List<String> selectionArgs = new ArrayList<String>(4);
            selectionArgs.add(String.valueOf(minLatitude));
            selectionArgs.add(String.valueOf(maxLatitude));
            selectionArgs.add(String.valueOf(minLongitude));
            selectionArgs.add(String.valueOf(maxLongitude));

            final Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery("SELECT Arret.id as arretId, ArretRoute.ligneId as ligneId, Direction.direction as direction," + " Arret.nom as arretNom, Arret.latitude as latitude, Arret.longitude as longitude," + " Ligne.nomCourt as nomCourt, Ligne.nomLong as nomLong, ArretRoute.macroDirection as macroDirection " + "FROM Arret, ArretRoute, Direction, Ligne " + "WHERE Arret.id = ArretRoute.arretId and Direction.id = ArretRoute.directionId AND Ligne.id = ArretRoute.ligneId" + " AND Arret.latitude > :minLatitude AND Arret.latitude < :maxLatitude" + " AND Arret.longitude > :minLongitude AND Arret.longitude < :maxLongitude", selectionArgs);

            /** Recuperation des index dans le cussor */
            final int arretIdIndex = cursor.getColumnIndex("arretId");
            final int ligneIdIndex = cursor.getColumnIndex("ligneId");
            final int directionIndex = cursor.getColumnIndex("direction");
            final int arretNomIndex = cursor.getColumnIndex("arretNom");
            final int latitudeIndex = cursor.getColumnIndex("latitude");
            final int longitudeIndex = cursor.getColumnIndex("longitude");
            final int nomCourtIndex = cursor.getColumnIndex("nomCourt");
            final int nomLongIndex = cursor.getColumnIndex("nomLong");
            final int macroDirectionIndex = cursor.getColumnIndex("macroDirection");

            final List<Arret> arrets = new ArrayList<Arret>(cursor.getCount());

            while (cursor.moveToNext()) {
                final Arret arret = new Arret();
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
                arret.favori.macroDirection = cursor.getInt(macroDirectionIndex);
                if (!arret.id.equals(arretId) || !arret.favori.ligneId.equals(favori.ligneId)) {
                    arret.calculDistance(locationArret);
                    if (arret.distance < DISTANCE_MAX_METRE) {
                        arrets.add(arret);
                    }
                }
            }
            cursor.close();
            Collections.sort(arrets, new Arret.ComparatorDistance());
            mapDetailCorrespondances.put(arretId, arrets);
            construireRelativeLayouts(arrets, detailCorrespondance);
        }
    }


    private static class RelativeLayoutHolder {
        private ImageView iconeLigne;
        private TextView arretDirection;
        private TextView nomArret;
        private TextView distance;
    }

    private RelativeLayout getRelativeLayout() {
        final RelativeLayout relativeLayout = (RelativeLayout) mInflater.inflate(R.layout.arretgps, null);
        final RelativeLayoutHolder holder = new RelativeLayoutHolder();
        holder.iconeLigne = (ImageView) relativeLayout.findViewById(R.id.iconeLigne);
        holder.arretDirection = (TextView) relativeLayout.findViewById(R.id.arretgps_direction);
        holder.nomArret = (TextView) relativeLayout.findViewById(R.id.arretgps_nomArret);
        holder.distance = (TextView) relativeLayout.findViewById(R.id.arretgps_distance);
        relativeLayout.setTag(holder);
        return relativeLayout;
    }

}
