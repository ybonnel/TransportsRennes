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
import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;
import fr.ybo.transportsrennes.map.mapviewutil.GeoItem;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.ClusterMarker;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.GeoClusterer;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.MarkerBitmap;

import java.util.ArrayList;
import java.util.List;

class MyClusterMarker<Objet extends ObjetWithDistance> extends ClusterMarker {
	/**
	 * check time object for tapping.
	 */
	private long tapCheckTime;

	private final Activity activity;
	private final String paramName;
	private final Class<? extends Activity> intentClass;

	MyClusterMarker(GeoClusterer.GeoCluster cluster, List<MarkerBitmap> markerIconBmps, float screenDensity, Activity activity, String paramName,
	                Class<? extends Activity> intentClass) {
		super(cluster, markerIconBmps, screenDensity);
		this.activity = activity;
		this.paramName = paramName;
		this.intentClass = intentClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean onTap(GeoPoint p, MapView mapView) {

		Projection pro = mapView.getProjection();
		Point ct = pro.toPixels(center, null);
		Point pt = pro.toPixels(p, null);
		/* check if this marker was tapped */
		MarkerBitmap bmp = markerIconBmps.get(markerTypes);
		Point grid = bmp.getGrid();
		Point bmpSize = bmp.getSize();
		//noinspection OverlyComplexBooleanExpression
		if (pt.x > ct.x - grid.x && pt.x < ct.x + bmpSize.x - grid.x && pt.y > ct.y - grid.y && pt.y < ct.y + bmpSize.y - grid.y) {
			if (selected) {
				/* double tap */
				long curTime = SystemClock.uptimeMillis();
				if (curTime < tapCheckTime + 1500) { // if within 1sec
					if (geoItems.size() > 100) {
						Toast.makeText(activity, "Plus de 100 arrêts sur ce point veuillez zoomer avant de les afficher.", Toast.LENGTH_SHORT).show();
					} else {
						ArrayList<Objet> objets = new ArrayList<Objet>(20);
						for (GeoItem item : geoItems) {
							objets.add(((MyGeoItem<Objet>) item).getObjet());
						}
						Intent intent = new Intent(activity, intentClass);
						intent.putExtra(paramName, objets);
						activity.startActivity(intent);
					}
				}
				tapCheckTime = SystemClock.uptimeMillis();
				return true;
			}
			selected = true;
			setMarkerBitmap();
			cluster.onTapCalledFromMarker(true);
			tapCheckTime = SystemClock.uptimeMillis();
			return true;
		}
		cluster.onTapCalledFromMarker(false);
		return false;
	}
}
