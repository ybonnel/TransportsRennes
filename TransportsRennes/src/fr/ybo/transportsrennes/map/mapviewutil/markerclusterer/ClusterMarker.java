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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Typeface;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import fr.ybo.transportsrennes.map.mapviewutil.GeoItem;

import java.util.List;

/**
 * Overlay extended class to display Clustered Marker.
 *
 * @author Huan Erdao
 */
public class ClusterMarker extends Overlay {

	/**
	 * cluster object
	 */
	protected final GeoClusterer.GeoCluster cluster_;
	/**
	 * screen density for multi-resolution
	 * get from contenxt.getResources().getDisplayMetrics().density;
	 */
	protected float screenDensity_ = 1.0f;

	protected final float TXTSIZE = 16.0f;

	/**
	 * Paint object for drawing icon
	 */
	protected final Paint paint_;
	/**
	 * List of GeoItems within
	 */
	protected final List<GeoItem> GeoItems_;
	/**
	 * center of the cluster
	 */
	protected final GeoPoint center_;
	/**
	 * Bitmap objects for icons
	 */
	protected final List<MarkerBitmap> markerIconBmps_;
	/**
	 * icon marker type
	 */
	protected int markerTypes = 0;
	/**
	 * select state for cluster
	 */
	protected boolean isSelected_ = false;
	/**
	 * selected item number in GeoItem List
	 */
	protected int selItem_;
	/**
	 * Text Offset
	 */
	protected int txtHeightOffset_;

	/**
	 * @param cluster        a cluster to be rendered for this marker
	 * @param markerIconBmps icon set for marker
	 */
	public ClusterMarker(GeoClusterer.GeoCluster cluster, List<MarkerBitmap> markerIconBmps, float screenDensity) {
		cluster_ = cluster;
		markerIconBmps_ = markerIconBmps;
		center_ = cluster_.getLocation();
		GeoItems_ = cluster_.getItems();
		screenDensity_ = screenDensity;
		paint_ = new Paint();
		paint_.setStyle(Paint.Style.STROKE);
		paint_.setAntiAlias(true);
		paint_.setColor(Color.WHITE);
		paint_.setTextSize(TXTSIZE * screenDensity_);
		paint_.setTextAlign(Paint.Align.CENTER);
		paint_.setTypeface(Typeface.DEFAULT_BOLD);
		FontMetrics metrics = paint_.getFontMetrics();
		txtHeightOffset_ = (int) ((metrics.bottom + metrics.ascent) / 2.0f);
		/* check if we have selected item in cluster */
		selItem_ = 0;
		for (int i = 0; i < GeoItems_.size(); i++) {
			if (GeoItems_.get(i).isSelected()) {
				selItem_ = i;
				isSelected_ = true;
			}
		}
		setMarkerBitmap();
	}

	/**
	 * change icon bitmaps according to the state.
	 */
	protected void setMarkerBitmap() {
		markerTypes = -1;
		for (int i = 0; i < markerIconBmps_.size(); i++) {
			if (GeoItems_.size() < markerIconBmps_.get(i).getItemMax()) {
				markerTypes = i;
				paint_.setTextSize(markerIconBmps_.get(markerTypes).getTextSize() * screenDensity_);
				FontMetrics metrics = paint_.getFontMetrics();
				txtHeightOffset_ = (int) ((metrics.bottom + metrics.ascent) / 2.0f);
				break;
			}
		}
		if (markerTypes < 0) {
			markerTypes = markerIconBmps_.size() - 1;
		}
	}

	/**
	 * draw icon.
	 *
	 * @param canvas  Canvas object.
	 * @param mapView MapView object.
	 * @param shadow  shadow flag.
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		cluster_.onNotifyDrawFromMarker();
		Projection proj = mapView.getProjection();
		Point p = proj.toPixels(center_, null);
		if (p.x < 0 || p.x > mapView.getWidth() || p.y < 0 || p.y > mapView.getHeight()) {
			return;
		}
		MarkerBitmap mkrBmp = markerIconBmps_.get(markerTypes);
		Bitmap bmp = isSelected_ ? mkrBmp.getBitmapSelect() : mkrBmp.getBitmapNormal();
		Point grid = mkrBmp.getGrid();
		Point gridReal = new Point((int) (grid.x * screenDensity_ + 0.5f), (int) (grid.y * screenDensity_ + 0.5f));
		canvas.drawBitmap(bmp, p.x - gridReal.x, p.y - gridReal.y, paint_);
		String caption = String.valueOf(GeoItems_.size());
		int x = p.x;
		int y = p.y - txtHeightOffset_;
		canvas.drawText(caption, x, y, paint_);
	}

	/**
	 * check if the marker is selected.
	 *
	 * @return true if selected state.
	 */
	public boolean isSelected() {
		return isSelected_;
	}

	/**
	 * clears selected state.
	 */
	public void clearSelect() {
		isSelected_ = false;
		if (selItem_ < GeoItems_.size()) {
			GeoItems_.get(selItem_).setSelect(false);
		}
		setMarkerBitmap();
	}

	/**
	 * get center location of the marker.
	 *
	 * @return GeoPoint object of current marker center.
	 */
	public GeoPoint getLocation() {
		return center_;
	}

	/**
	 * get selected item's location. null if nothing is selected.
	 *
	 * @return GeoPoint object for selected item. null if nothing selected.
	 */
	public GeoPoint getSelectedItemLocation() {
		if (selItem_ < GeoItems_.size()) {
			return GeoItems_.get(selItem_).getLocation();
		}
		return null;
	}

}
