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

package fr.ybo.transportsrennes.map.mapviewutil.markerclusterer;

import android.graphics.Point;
import android.os.Handler;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import fr.ybo.transportsrennes.map.mapviewutil.GeoBounds;
import fr.ybo.transportsrennes.map.mapviewutil.GeoItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for Clustering geotagged content.
 * this clustering came from "markerclusterer" which is available as opensource at
 * http://code.google.com/p/gmaps-utility-library/
 * this is android ported version with modification to fit to the application
 *
 * @author Huan Erdao
 */
public class GeoClusterer {

	/**
	 * grid size for clustering(dip).
	 */
	protected int gridSize = 56;

	/**
	 * screen density for multi-resolution
	 * get from contenxt.getResources().getDisplayMetrics().density;
	 */
	protected float screenDensity_ = 1.0f;

	/**
	 * MapView object.
	 */
	protected final MapView mapView_;
	/**
	 * GeoItem ArrayList object to be shown.
	 */
	private final Collection<GeoItem> items_ = new ArrayList<GeoItem>(100);
	/**
	 * GeoItem ArrayList object that are out of viewport to be clustered.
	 */
	private final Collection<GeoItem> leftItems_ = new ArrayList<GeoItem>(100);
	/**
	 * Clustered object list.
	 */
	protected final List<GeoClusterer.GeoCluster> clusters_ = new ArrayList<GeoClusterer.GeoCluster>(100);
	/**
	 * MarkerBitmap object for marker icons.
	 */
	protected final List<MarkerBitmap> markerIconBmps_;
	/**
	 * selected cluster object.
	 */
	private GeoClusterer.GeoCluster selcluster_;
	/**
	 * check counter for tapping all cluster object.
	 */
	private int tapCheckCount_;
	/**
	 * GeoBound to check moves of the map view.
	 */
	private GeoBounds savedBounds_;
	/**
	 * flag for detecting map moves. true if map is moving or zooming.
	 */
	private boolean isMoving_;
	/**
	 * handler to initiate moveend/zoomend event and reset view.
	 */
	private final Handler handler_;

	/**
	 * @param mapView        MapView object.
	 * @param markerIconBmps MarkerBitmap objects for icons.
	 * @param screenDensity  Screen Density.
	 */
	protected GeoClusterer(final MapView mapView, final List<MarkerBitmap> markerIconBmps, final float screenDensity) {
		super();
		mapView_ = mapView;
		markerIconBmps_ = markerIconBmps;
		screenDensity_ = screenDensity;
		handler_ = new Handler();
		isMoving_ = false;
	}

	/**
	 * add item and do clustering.
	 * NOTE: this method will not redraw screen. after adding all items,
	 * you must call redraw() method.
	 *
	 * @param item GeoItem to be clustered.
	 */
	public void addItem(final GeoItem item) {
		// if not in viewport, add to leftItems_
		if (isItemNotInViewport(item)) {
			leftItems_.add(item);
			return;
		}
		// else add to items_;
		items_.add(item);
		final int length = clusters_.size();
		final Projection proj = mapView_.getProjection();
		final Point pos = proj.toPixels(item.getLocation(), null);
		// check existing cluster
		for (int i = length - 1; i >= 0; i--) {
			GeoCluster cluster = clusters_.get(i);
			final GeoPoint gpCenter = cluster.getLocation();
			if (gpCenter == null) {
				continue;
			}
			final Point ptCenter = proj.toPixels(gpCenter, null);
			// find a cluster which contains the marker.
			final int gridSizePx = (int) (gridSize * screenDensity_ + 0.5f);
			if (pos.x >= ptCenter.x - gridSizePx && pos.x <= ptCenter.x + gridSizePx && pos.y >= ptCenter.y - gridSizePx &&
					pos.y <= ptCenter.y + gridSizePx) {
				cluster.addItem(item);
				return;
			}
		}
		// No cluster contain the marker, create a new cluster.
		createCluster(item);
	}

	/**
	 * Create Cluster Object.
	 * override this method, if you want to use custom GeoCluster class.
	 *
	 * @param item GeoItem to be set to cluster.
	 */
	protected void createCluster(final GeoItem item) {
		final GeoClusterer.GeoCluster cluster = new GeoClusterer.GeoCluster(this);
		cluster.addItem(item);
		clusters_.add(cluster);
	}

	/**
	 * redraws clusters
	 */
	void redraw() {
		for (final GeoClusterer.GeoCluster aClusters : clusters_) {
			aClusters.redraw();
		}
	}

	/**
	 * check if the item is within current viewport.
	 *
	 * @param item item
	 * @return true if item is within viewport.
	 */
	final boolean isItemNotInViewport(final GeoItem item) {
		savedBounds_ = getCurBounds();
		return !savedBounds_.isInBounds(item.getLocation());
	}

	/**
	 * get current Bound
	 *
	 * @return current GeoBounds
	 */
	protected final GeoBounds getCurBounds() {
		final Projection proj = mapView_.getProjection();
		return new GeoBounds(proj.fromPixels(0, 0), proj.fromPixels(mapView_.getWidth(), mapView_.getHeight()));
	}

	/**
	 * get clusters within current viewport.
	 *
	 * @return clusters within current viewport.
	 */
	List<GeoClusterer.GeoCluster> getClustersInViewport() {
		final GeoBounds curBounds = getCurBounds();
		final List<GeoClusterer.GeoCluster> clusters = new ArrayList<GeoClusterer.GeoCluster>(100);
		for (final GeoClusterer.GeoCluster cluster : clusters_) {
			if (cluster.isInBounds(curBounds)) {
				clusters.add(cluster);
			}
		}
		return clusters;
	}

	/**
	 * add items that were not clustered in last clustering.
	 */
	void addLeftItems() {
		if (leftItems_.size() == 0) {
			return;
		}
		final Collection<GeoItem> currentLeftItems = new ArrayList<GeoItem>(100);
		currentLeftItems.addAll(leftItems_);
		leftItems_.clear();
		for (final GeoItem currentLeftItem : currentLeftItems) {
			addItem(currentLeftItem);
		}
	}

	/**
	 * re-add items for clustering.
	 *
	 * @param items GeoItem list to be clustered.
	 */
	void reAddItems(final List<GeoItem> items) {
		final int len = items.size();
		for (int i = len - 1; i >= 0; i--) {
			addItem(items.get(i));
		}
		addLeftItems();
	}

	/**
	 * reset current viewport.
	 */
	public void resetViewport() {
		final List<GeoClusterer.GeoCluster> clusters = getClustersInViewport();
		final List<GeoItem> tmpItems = new ArrayList<GeoItem>(100);
		int removed = 0;
		for (final GeoClusterer.GeoCluster cluster : clusters) {
			final int oldZoom = cluster.getZoomLevel();
			final int curZoom = mapView_.getZoomLevel();
			// If the cluster zoom level changed then destroy the cluster and collect its markers.
			if (curZoom != oldZoom) {
				tmpItems.addAll(cluster.getItems());
				cluster.clear();
				removed++;
				for (int j = 0; j < clusters_.size(); j++) {
					if (cluster == clusters_.get(j)) {
						clusters_.remove(j);
					}
				}
			}
		}
		reAddItems(tmpItems);
		redraw();
		// Add the markers collected into marker cluster to reset
		if (removed > 0) {
			for (final GeoClusterer.GeoCluster aClusters : clusters_) {
				GeoCluster cluster = aClusters;
				if (cluster.isSelected()) {
					return;
				}
			}
			for (final GeoItem anItems : items_) {
				anItems.setSelect(false);
			}
		}
	}

	/**
	 * clears selected state.
	 */
	void clearSelect() {
		for (final GeoClusterer.GeoCluster aclusters : clusters_) {
			if (selcluster_ == aclusters) {
				aclusters.clearSelect();
			}
		}
	}

	/**
	 * Hooking draw event from ClusterMarker to detect zoom/move event.
	 * hope there will be event notification for android equivalent to
	 * javascriptin the future....
	 */
	void onNotifyDrawFromCluster() {
		// ignore if it is already recognized as moving state
		if (isMoving_) {
			return;
		}
		final GeoBounds curBnd = getCurBounds();
		// checking bounds if it is moving or not.
		if (!savedBounds_.isEqual(curBnd)) {
			isMoving_ = true;
			savedBounds_ = curBnd;
			final Timer timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					final GeoBounds curBnd = getCurBounds();
					// if there is no more moving, reset the viewport
					if (savedBounds_.isEqual(curBnd)) {
						isMoving_ = false;
						cancel();
						handler_.post(new Runnable() {
							public void run() {
								resetViewport();
							}
						});
					}
					savedBounds_ = curBnd;
				}
			}, 500, 500);
		}
	}

	/**
	 * onTap call from Cluster layer.
	 * this method will be called number of times equals to size of clusters.
	 * check isTapped to know which cluster was tapped.
	 *
	 * @param caller   cluster object called this.
	 * @param isTapped if true, tapped.
	 */
	void onTapCalledFromCluster(final GeoClusterer.GeoCluster caller, final boolean isTapped) {
		// if tapped, set selcluster_ to caller
		if (isTapped) {
			if (selcluster_ == caller) {
				return;
			}
			clearSelect();
			selcluster_ = caller;
		} else {
			tapCheckCount_++;
			if (tapCheckCount_ == clusters_.size()) {
				tapCheckCount_ = 0;
			}
		}
	}

	/**
	 * GeoCluster class.
	 * contains single marker object(ClusterMarker). mostly wraps methods in ClusterMarker.
	 */
	public class GeoCluster {
		/**
		 * GeoClusterer object
		 */
		private final GeoClusterer clusterer_;
		/**
		 * center of cluster
		 */
		GeoPoint center_;
		/**
		 * list of GeoItem within cluster
		 */
		private List<GeoItem> items_ = new ArrayList<GeoItem>(100);
		/**
		 * ClusterMarker object
		 */
		protected ClusterMarker clusterMarker_;
		/**
		 * zoomlevel at the point Cluster was made
		 */
		private final int zoom_;

		/**
		 * @param clusterer GeoClusterer object.
		 */
		public GeoCluster(final GeoClusterer clusterer) {
			super();
			clusterer_ = clusterer;
			clusterMarker_ = null;
			zoom_ = mapView_.getZoomLevel();
		}

		/**
		 * add item to cluster object
		 *
		 * @param item GeoItem object to be added.
		 */
		public void addItem(final GeoItem item) {
			if (center_ == null) {
				center_ = item.getLocation();
			}
			items_.add(item);
		}

		/**
		 * get center of the cluster.
		 *
		 * @return center of the cluster in GeoPoint.
		 */
		public GeoPoint getLocation() {
			return center_;
		}

		/**
		 * clears selected state.
		 */
		public void clearSelect() {
			clusterMarker_.clearSelect();
		}

		/**
		 * check if the cluster is selected.
		 *
		 * @return true if selected.
		 */
		public boolean isSelected() {
			return clusterMarker_ != null && clusterMarker_.isSelected();
		}

		/**
		 * get zoomlevel.
		 *
		 * @return zoom level of the cluster.
		 */
		public int getZoomLevel() {
			return zoom_;
		}

		/**
		 * get list of GeoItem.
		 *
		 * @return list of GeoItem within cluster.
		 */
		public List<GeoItem> getItems() {
			return items_;
		}

		/**
		 * Hooking Overlay.draw event to detect if it is moving/zooming.
		 * calls GeoCluster.onNotifyDraw.
		 */
		public void onNotifyDrawFromMarker() {
			clusterer_.onNotifyDrawFromCluster();
		}

		/**
		 * Hooking Tap event from ClusterMarker layer.
		 *
		 * @param flg true if the tap event was captured, else false.
		 */
		public void onTapCalledFromMarker(final boolean flg) {
			clusterer_.onTapCalledFromCluster(this, flg);
		}

		/**
		 * clears cluster object.
		 */
		public void clear() {
			if (clusterMarker_ != null) {
				final List<Overlay> mapOverlays = mapView_.getOverlays();
				if (mapOverlays.contains(clusterMarker_)) {
					mapOverlays.remove(clusterMarker_);
				}
				clusterMarker_ = null;
			}
			items_ = null;
		}

		/**
		 * redraw cluster. if needed create ClusterMarker object.
		 */
		public void redraw() {
			if (!isInBounds(clusterer_.getCurBounds())) {
				return;
			}
			if (clusterMarker_ == null) {
				clusterMarker_ = new ClusterMarker(this, markerIconBmps_, screenDensity_);
				final List<Overlay> mapOverlays = mapView_.getOverlays();
				mapOverlays.add(clusterMarker_);
			}
		}

		/**
		 * check if the GeoBounds are within cluster.
		 *
		 * @param bounds bounds
		 * @return true if bounds are within this cluster size.
		 */
		protected boolean isInBounds(final GeoBounds bounds) {
			if (center_ == null) {
				return false;
			}
			final Projection pro = mapView_.getProjection();
			final Point nw = pro.toPixels(bounds.getNorthWest(), null);
			final Point se = pro.toPixels(bounds.getSouthEast(), null);
			final Point centxy = pro.toPixels(center_, null);
			int gridSizePx = (int) (gridSize * screenDensity_ + 0.5f);
			if (zoom_ != mapView_.getZoomLevel()) {
				final int diff = mapView_.getZoomLevel() - zoom_;
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
