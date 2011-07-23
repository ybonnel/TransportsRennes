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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
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

package fr.ybo.transportsrennes.map.mapviewutil.markerclusterer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Point;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import fr.ybo.transportsrennes.map.mapviewutil.GeoBounds;
import fr.ybo.transportsrennes.map.mapviewutil.GeoItem;

/**
 * Class for Clustering geotagged content. this clustering came from
 * "markerclusterer" which is available as opensource at
 * http://code.google.com/p/gmaps-utility-library/ this is android ported
 * version with modification to fit to the application
 * 
 * @author Huan Erdao
 */
public class GeoClusterer {

	/**
	 * grid size for clustering(dip).
	 */
	protected int gridSize = 56;

	/**
	 * screen density for multi-resolution get from
	 * contenxt.getResources().getDisplayMetrics().density;
	 */
	protected float screenDensity = 1.0f;

	/**
	 * MapView object.
	 */
	protected final MapView mapView;
	/**
	 * GeoItem ArrayList object to be shown.
	 */
	private final Collection<GeoItem> items = new ArrayList<GeoItem>(100);
	/**
	 * GeoItem ArrayList object that are out of viewport to be clustered.
	 */
	private final Collection<GeoItem> leftItems = new ArrayList<GeoItem>(100);
	/**
	 * Clustered object list.
	 */
	protected final List<GeoClusterer.GeoCluster> clusters = new ArrayList<GeoClusterer.GeoCluster>(100);
	/**
	 * MarkerBitmap object for marker icons.
	 */
	protected final List<MarkerBitmap> markerIconBmps;
	/**
	 * selected cluster object.
	 */
	private GeoClusterer.GeoCluster selcluster;
	/**
	 * check counter for tapping all cluster object.
	 */
	private int tapCheckCount;
	/**
	 * GeoBound to check moves of the map view.
	 */
	private GeoBounds savedBounds;
	/**
	 * flag for detecting map moves. true if map is moving or zooming.
	 */
	private boolean isMoving;
	/**
	 * handler to initiate moveend/zoomend event and reset view.
	 */
	private final Handler handler;

	/**
	 * @param mapView
	 *            MapView object.
	 * @param markerIconBmps
	 *            MarkerBitmap objects for icons.
	 * @param screenDensity
	 *            Screen Density.
	 */
	protected GeoClusterer(MapView mapView, List<MarkerBitmap> markerIconBmps, float screenDensity) {
		this.mapView = mapView;
		this.markerIconBmps = markerIconBmps;
		this.screenDensity = screenDensity;
		handler = new Handler();
		isMoving = false;
	}

	/**
	 * add item and do clustering. NOTE: this method will not redraw screen.
	 * after adding all itemsOfCluster, you must call redraw() method.
	 * 
	 * @param item
	 *            GeoItem to be clustered.
	 */
	public void addItem(GeoItem item) {
		// if not in viewport, add to leftItems
		if (isItemNotInViewport(item)) {
			synchronized (leftItems) {
				leftItems.add(item);
			}
			return;
		}
		// else add to itemsOfCluster;
		items.add(item);
		synchronized (clusters) {
			int length = clusters.size();
			Projection proj = mapView.getProjection();
			Point pos = proj.toPixels(item.getLocation(), null);
			// check existing cluster
			for (int i = length - 1; i >= 0; i--) {
				GeoClusterer.GeoCluster cluster = clusters.get(i);
				GeoPoint gpCenter = cluster.getLocation();
				if (gpCenter == null) {
					continue;
				}
				Point ptCenter = proj.toPixels(gpCenter, null);
				// find a cluster which contains the marker.
				int gridSizePx = (int) (gridSize * screenDensity + 0.5f);
				if (pos.x >= ptCenter.x - gridSizePx && pos.x <= ptCenter.x + gridSizePx
						&& pos.y >= ptCenter.y - gridSizePx && pos.y <= ptCenter.y + gridSizePx) {
					cluster.addItem(item);
					return;
				}
			}
		}
		// No cluster contain the marker, create a new cluster.
		createCluster(item);
	}

	/**
	 * Create Cluster Object. override this method, if you want to use custom
	 * GeoCluster class.
	 * 
	 * @param item
	 *            GeoItem to be set to cluster.
	 */
	protected void createCluster(GeoItem item) {
		GeoClusterer.GeoCluster cluster = new GeoClusterer.GeoCluster(this);
		cluster.addItem(item);
		synchronized (clusters) {
			clusters.add(cluster);
		}
	}

	/**
	 * redraws clusters
	 */
	private void redraw() {
		synchronized (clusters) {
			for (GeoClusterer.GeoCluster aClusters : clusters) {
				aClusters.redraw();
			}
		}
	}

	/**
	 * check if the item is within current viewport.
	 * 
	 * @param item
	 *            item
	 * @return true if item is within viewport.
	 */
	private boolean isItemNotInViewport(GeoItem item) {
		savedBounds = getCurBounds();
		return !savedBounds.isInBounds(item.getLocation());
	}

	/**
	 * get current Bound
	 * 
	 * @return current GeoBounds
	 */
	protected GeoBounds getCurBounds() {
		Projection proj = mapView.getProjection();
		return new GeoBounds(proj.fromPixels(0, 0), proj.fromPixels(mapView.getWidth(), mapView.getHeight()));
	}

	/**
	 * get clusters within current viewport.
	 * 
	 * @return clusters within current viewport.
	 */
	private Iterable<GeoClusterer.GeoCluster> getClustersInViewport() {
		GeoBounds curBounds = getCurBounds();
		Collection<GeoClusterer.GeoCluster> clustersRetour = new ArrayList<GeoClusterer.GeoCluster>(100);
		synchronized (clusters) {
			for (GeoClusterer.GeoCluster cluster : clusters) {
				if (cluster.isInBounds(curBounds)) {
					clustersRetour.add(cluster);
				}
			}
		}
		return clustersRetour;
	}

	/**
	 * add itemsOfCluster that were not clustered in last clustering.
	 */
	private void addLeftItems() {
		Collection<GeoItem> currentLeftItems = new ArrayList<GeoItem>(100);
		synchronized (leftItems) {
			if (leftItems.isEmpty()) {
				return;
			}
			currentLeftItems.addAll(leftItems);
			leftItems.clear();
		}
		for (GeoItem currentLeftItem : currentLeftItems) {
			addItem(currentLeftItem);
		}
	}

	/**
	 * re-add itemsOfCluster for clustering.
	 * 
	 * @param items
	 *            GeoItem list to be clustered.
	 */
	private void reAddItems(List<GeoItem> items) {
		int len = items.size();
		for (int i = len - 1; i >= 0; i--) {
			addItem(items.get(i));
		}
		addLeftItems();
	}

	/**
	 * reset current viewport.
	 */
	public void resetViewport() {
		List<GeoItem> tmpItems = new ArrayList<GeoItem>(100);
		int removed = 0;
		for (GeoClusterer.GeoCluster cluster : getClustersInViewport()) {
			int oldZoom = cluster.getZoomLevel();
			int curZoom = mapView.getZoomLevel();
			// If the cluster zoom level changed then destroy the cluster and
			// collect its markers.
			if (curZoom != oldZoom) {
				tmpItems.addAll(cluster.getItemsOfCluster());
				cluster.clear();
				removed++;
				synchronized (clusters) {
					for (int j = 0; j < clusters.size(); j++) {
						if (cluster == clusters.get(j)) {
							clusters.remove(j);
						}
					}
				}
			}
		}
		reAddItems(tmpItems);
		redraw();
		// Add the markers collected into marker cluster to reset
		if (removed > 0) {
			synchronized (clusters) {
				for (GeoClusterer.GeoCluster aClusters : clusters) {
					if (aClusters.isSelected()) {
						return;
					}
				}
			}
			for (GeoItem anItems : items) {
				anItems.setSelect(false);
			}
		}
	}

	/**
	 * clears selected state.
	 */
	private void clearSelect() {
		synchronized (clusters) {
			for (GeoClusterer.GeoCluster aclusters : clusters) {
				if (selcluster == aclusters) {
					aclusters.clearSelect();
				}
			}
		}
	}

	/**
	 * Hooking draw event from ClusterMarker to detect zoom/move event. hope
	 * there will be event notification for android equivalent to javascriptin
	 * the future....
	 */
	private void onNotifyDrawFromCluster() {
		// ignore if it is already recognized as moving state
		if (isMoving) {
			return;
		}
		GeoBounds curBnd = getCurBounds();
		// checking bounds if it is moving or not.
		if (!savedBounds.isEqual(curBnd)) {
			isMoving = true;
			savedBounds = curBnd;
			Timer timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					GeoBounds curBnd = getCurBounds();
					// if there is no more moving, reset the viewport
					if (savedBounds.isEqual(curBnd)) {
						isMoving = false;
						cancel();
						handler.post(new Runnable() {
							public void run() {
								resetViewport();
							}
						});
					}
					savedBounds = curBnd;
				}
			}, 500, 500);
		}
	}

	/**
	 * onTap call from Cluster layer. this method will be called number of times
	 * equals to size of clusters. check isTapped to know which cluster was
	 * tapped.
	 * 
	 * @param caller
	 *            cluster object called this.
	 * @param isTapped
	 *            if true, tapped.
	 */
	private void onTapCalledFromCluster(GeoClusterer.GeoCluster caller, boolean isTapped) {
		// if tapped, set selcluster to caller
		if (isTapped) {
			if (selcluster == caller) {
				return;
			}
			clearSelect();
			selcluster = caller;
		} else {
			tapCheckCount++;
			if (tapCheckCount == clusters.size()) {
				tapCheckCount = 0;
			}
		}
	}

	/**
	 * GeoCluster class. contains single marker object(ClusterMarker). mostly
	 * wraps methods in ClusterMarker.
	 */
	public class GeoCluster {
		/**
		 * GeoClusterer object
		 */
		private final GeoClusterer clusterer;
		/**
		 * center of cluster
		 */
		GeoPoint center;
		/**
		 * list of GeoItem within cluster
		 */
		private List<GeoItem> itemsOfCluster = new ArrayList<GeoItem>(100);
		/**
		 * ClusterMarker object
		 */
		protected ClusterMarker clusterMarker;
		/**
		 * zoomlevel at the point Cluster was made
		 */
		private final int zoom;

		/**
		 * @param clusterer
		 *            GeoClusterer object.
		 */
		public GeoCluster(GeoClusterer clusterer) {
			this.clusterer = clusterer;
			clusterMarker = null;
			zoom = mapView.getZoomLevel();
		}

		/**
		 * add item to cluster object
		 * 
		 * @param item
		 *            GeoItem object to be added.
		 */
		public void addItem(GeoItem item) {
			if (center == null) {
				center = item.getLocation();
			}
			itemsOfCluster.add(item);
		}

		/**
		 * get center of the cluster.
		 * 
		 * @return center of the cluster in GeoPoint.
		 */
		public GeoPoint getLocation() {
			return center;
		}

		/**
		 * clears selected state.
		 */
		public void clearSelect() {
			clusterMarker.clearSelect();
		}

		/**
		 * check if the cluster is selected.
		 * 
		 * @return true if selected.
		 */
		public boolean isSelected() {
			return clusterMarker != null && clusterMarker.isSelected();
		}

		/**
		 * get zoomlevel.
		 * 
		 * @return zoom level of the cluster.
		 */
		public int getZoomLevel() {
			return zoom;
		}

		/**
		 * get list of GeoItem.
		 * 
		 * @return list of GeoItem within cluster.
		 */
		public List<GeoItem> getItemsOfCluster() {
			return itemsOfCluster;
		}

		/**
		 * Hooking Overlay.draw event to detect if it is moving/zooming. calls
		 * GeoCluster.onNotifyDraw.
		 */
		public void onNotifyDrawFromMarker() {
			clusterer.onNotifyDrawFromCluster();
		}

		/**
		 * Hooking Tap event from ClusterMarker layer.
		 * 
		 * @param flg
		 *            true if the tap event was captured, else false.
		 */
		public void onTapCalledFromMarker(boolean flg) {
			clusterer.onTapCalledFromCluster(this, flg);
		}

		/**
		 * clears cluster object.
		 */
		public void clear() {
			if (clusterMarker != null) {
				List<Overlay> mapOverlays = mapView.getOverlays();
				if (mapOverlays.contains(clusterMarker)) {
					mapOverlays.remove(clusterMarker);
				}
				clusterMarker = null;
			}
			itemsOfCluster = null;
		}

		/**
		 * redraw cluster. if needed create ClusterMarker object.
		 */
		public void redraw() {
			if (!isInBounds(clusterer.getCurBounds())) {
				return;
			}
			if (clusterMarker == null) {
				clusterMarker = new ClusterMarker(this, markerIconBmps, screenDensity);
				List<Overlay> mapOverlays = mapView.getOverlays();
				mapOverlays.add(clusterMarker);
			}
		}

		/**
		 * check if the GeoBounds are within cluster.
		 * 
		 * @param bounds
		 *            bounds
		 * @return true if bounds are within this cluster size.
		 */
		protected boolean isInBounds(GeoBounds bounds) {
			if (center == null) {
				return false;
			}
			Projection pro = mapView.getProjection();
			Point nw = pro.toPixels(bounds.getNorthWest(), null);
			Point se = pro.toPixels(bounds.getSouthEast(), null);
			Point centxy = pro.toPixels(center, null);
			int gridSizePx = (int) (gridSize * screenDensity + 0.5f);
			if (zoom != mapView.getZoomLevel()) {
				int diff = mapView.getZoomLevel() - zoom;
				gridSizePx = (int) (Math.pow(2, diff) * gridSizePx);
			}
			boolean inViewport = true;
			if (nw.x != se.x && (centxy.x + gridSizePx < nw.x || centxy.x - gridSizePx > se.x)) {
				inViewport = false;
			}
			if (inViewport && (centxy.y + gridSizePx < nw.y || centxy.y - gridSizePx > se.y)) {
				inViewport = false;
			}
			return inViewport;
		}
	}
}
