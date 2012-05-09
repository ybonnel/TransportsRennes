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
package fr.ybo.transportsrennes.activity.pointsdevente;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ViewById;

import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseMapActivity;
import fr.ybo.transportscommun.util.FixedMyLocationOverlay;
import fr.ybo.transportscommun.util.Formatteur;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;
import fr.ybo.transportsrennes.map.MapItemizedOverlayPos;

@EActivity(R.layout.map)
public class PointsDeVentesOnMap extends BaseMapActivity {

	@Extra("pointsDeVente")
	Iterable<PointDeVente> pointDeVentes;

	@ViewById(R.id.mapview)
	MapView mapView;

	@AfterViews
	void afterViews() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);

        mapView.setBuiltInZoomControls(true);

        MapController mc = mapView.getController();

        // Creation du geo point
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = getResources().getDrawable(R.drawable.markee_pos);
        MapItemizedOverlayPos itemizedoverlay = new MapItemizedOverlayPos(drawable, this);

        int minLatitude = Integer.MAX_VALUE;
        int maxLatitude = Integer.MIN_VALUE;
        int minLongitude = Integer.MAX_VALUE;
        int maxLongitude = Integer.MIN_VALUE;


        for (PointDeVente pointDeVente : pointDeVentes) {
            int latitude = (int) (pointDeVente.latitude * 1.0E6);
            int longitude = (int) (pointDeVente.longitude * 1.0E6);
            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            if (pointDeVente.longitude > -2.0) {
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
            }
            OverlayItem overlayitem = new OverlayItem(geoPoint, Formatteur.formatterChaine(pointDeVente.name), pointDeVente.telephone);
            itemizedoverlay.addOverlay(overlayitem, pointDeVente);
        }
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
