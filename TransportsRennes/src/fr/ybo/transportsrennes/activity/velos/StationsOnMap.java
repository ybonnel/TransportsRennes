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
package fr.ybo.transportsrennes.activity.velos;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.commun.BaseActivity.BaseMapActivity;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.map.MapItemizedOverlayVelo;
import fr.ybo.transportsrennes.util.FixedMyLocationOverlay;
import fr.ybo.transportsrennes.util.Formatteur;

public class StationsOnMap extends BaseMapActivity {

    /**
     * Called when the activity is first created.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);

        Iterable<Station> stations = (Iterable<Station>) getIntent().getExtras().getSerializable("stations");

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        MapController mc = mapView.getController();

        // Creation du geo point
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = getResources().getDrawable(R.drawable.markee_velo);
        MapItemizedOverlayVelo itemizedoverlay = new MapItemizedOverlayVelo(drawable, this);

        int minLatitude = Integer.MAX_VALUE;
        int maxLatitude = Integer.MIN_VALUE;
        int minLongitude = Integer.MAX_VALUE;
        int maxLongitude = Integer.MIN_VALUE;


        for (Station station : stations) {
            int latitude = (int) (station.latitude * 1.0E6);
            int longitude = (int) (station.longitude * 1.0E6);
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
            int placesTotales = station.bikesavailable + station.slotsavailable;
            OverlayItem overlayitem =
                    new OverlayItem(geoPoint, Formatteur.formatterChaine(station.name), station.bikesavailable + " / " + placesTotales);
            itemizedoverlay.addOverlay(overlayitem, station);
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
