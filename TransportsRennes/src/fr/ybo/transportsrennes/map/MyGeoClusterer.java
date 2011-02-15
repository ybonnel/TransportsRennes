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

/*
 * Copyright (C) 2009 Huan Erdao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.ybo.transportsrennes.map;

import android.app.Activity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;
import fr.ybo.transportsrennes.map.mapviewutil.GeoItem;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.GeoClusterer;
import fr.ybo.transportsrennes.map.mapviewutil.markerclusterer.MarkerBitmap;

import java.util.List;

public class MyGeoClusterer<Objet extends ObjetWithDistance> extends GeoClusterer {

	private final Activity activity;
	private final String paramName;
	private final Class<? extends Activity> intentClass;

	public MyGeoClusterer(
			final Activity activity, final MapView mapView, final List<MarkerBitmap> markerIconBmps, final float screenDensity, final String paramName,
	                      final Class<? extends Activity> intentClass) {
		super(mapView, markerIconBmps, screenDensity);
		gridSize = 70;
		this.activity = activity;
		this.paramName = paramName;
		this.intentClass = intentClass;
	}

	@Override
	public void createCluster(final GeoItem item) {
		final MyGeoClusterer.MyGeoCluster cluster = new MyGeoClusterer.MyGeoCluster(this);
		cluster.addItem(item);
		clusters_.add(cluster);
	}

	public class MyGeoCluster extends GeoClusterer.GeoCluster {
		private final MyGeoClusterer<?> clusterer;

		public MyGeoCluster(final MyGeoClusterer<?> clusterer) {
			super(clusterer);
			this.clusterer = clusterer;
		}

		@Override
		public void redraw() {
			if (!isInBounds(clusterer.getCurBounds())) {
				return;
			}
			if (clusterMarker_ == null) {
				clusterMarker_ = new MyClusterMarker<Objet>(this, markerIconBmps_, screenDensity_, activity, paramName, intentClass);
				final List<Overlay> mapOverlays = mapView_.getOverlays();
				mapOverlays.add(clusterMarker_);
			}
		}
	}
}
