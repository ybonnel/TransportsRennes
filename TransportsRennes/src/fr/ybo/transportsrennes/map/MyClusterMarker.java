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

package fr.ybo.transportsrennes.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.SystemClock;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import fr.ybo.transportsrennes.ListArretByPosition;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.map.mapviewutil.GeoItem;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.ClusterMarker;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.GeoClusterer;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.MarkerBitmap;

import java.util.ArrayList;
import java.util.List;

public class MyClusterMarker extends ClusterMarker {
	/**
	 * check time object for tapping.
	 */
	private long tapCheckTime_;

	private Activity activity;

	public MyClusterMarker(GeoClusterer.GeoCluster cluster, List<MarkerBitmap> markerIconBmps, float screenDensity, Activity activity) {
		super(cluster, markerIconBmps, screenDensity);
		this.activity = activity;
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {

		Projection pro = mapView.getProjection();
		Point ct = pro.toPixels(center_, null);
		Point pt = pro.toPixels(p, null);
		/* check if this marker was tapped */
		MarkerBitmap bmp = markerIconBmps_.get(markerTypes);
		Point grid = bmp.getGrid();
		Point bmpSize = bmp.getSize();
		if (pt.x > ct.x - grid.x && pt.x < ct.x + (bmpSize.x - grid.x) && pt.y > ct.y - grid.y && pt.y < ct.y + (bmpSize.y - grid.y)) {
			if (isSelected_) {
				/* double tap */
				long curTime = SystemClock.uptimeMillis();
				if (curTime < tapCheckTime_ + 1500) { // if within 1sec
					if (GeoItems_.size() > 100) {
						Toast.makeText(activity, "Plus de 100 arrêts sur ce point veuillez zoomer avant de les afficher.", Toast.LENGTH_SHORT).show();
					} else {
						ArrayList<Arret> arrets = new ArrayList<Arret>();
						for (GeoItem item : GeoItems_) {
							arrets.add(((MyGeoItem) item).getArret());
						}
						Intent intent = new Intent(activity, ListArretByPosition.class);
						intent.putExtra("arrets", arrets);
						activity.startActivity(intent);
					}
				}
				tapCheckTime_ = SystemClock.uptimeMillis();
				return true;
			}
			isSelected_ = true;
			setMarkerBitmap();
			cluster_.onTapCalledFromMarker(true);
			tapCheckTime_ = SystemClock.uptimeMillis();
			return true;
		}
		cluster_.onTapCalledFromMarker(false);
		return false;
	}
}
