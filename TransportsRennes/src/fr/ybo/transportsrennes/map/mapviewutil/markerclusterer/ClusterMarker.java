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
	protected final GeoClusterer.GeoCluster cluster;
	/**
	 * screen density for multi-resolution
	 * get from contenxt.getResources().getDisplayMetrics().density;
	 */
	private float screenDensity = 1.0f;

	private static final float TXTSIZE = 16.0f;

	/**
	 * Paint object for drawing icon
	 */
	private final Paint paint;
	/**
	 * List of GeoItems within
	 */
	protected final List<GeoItem> geoItems;
	/**
	 * center of the cluster
	 */
	protected final GeoPoint center;
	/**
	 * Bitmap objects for icons
	 */
	protected final List<MarkerBitmap> markerIconBmps;
	/**
	 * icon marker type
	 */
	protected int markerTypes;
	/**
	 * select state for cluster
	 */
	protected boolean selected;
	/**
	 * selected item number in GeoItem List
	 */
	private int selItem;
	/**
	 * Text Offset
	 */
	private int txtHeightOffset;

	/**
	 * @param cluster        a cluster to be rendered for this marker.
	 * @param markerIconBmps icon set for marker.
	 * @param screenDensity  screen density.
	 */
	public ClusterMarker(GeoClusterer.GeoCluster cluster, List<MarkerBitmap> markerIconBmps, float screenDensity) {
		this.cluster = cluster;
		this.markerIconBmps = markerIconBmps;
		center = this.cluster.getLocation();
		geoItems = this.cluster.getItemsOfCluster();
		this.screenDensity = screenDensity;
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(220, 220, 80));
		paint.setTextSize(TXTSIZE * this.screenDensity);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		Paint.FontMetrics metrics = paint.getFontMetrics();
		txtHeightOffset = (int) ((metrics.bottom + metrics.ascent) / 2.0f);
		/* check if we have selected item in cluster */
		selItem = 0;
		for (int i = 0; i < geoItems.size(); i++) {
			if (geoItems.get(i).isSelected()) {
				selItem = i;
				selected = true;
			}
		}
		setMarkerBitmap();
	}

	/**
	 * change icon bitmaps according to the state.
	 */
	protected void setMarkerBitmap() {
		markerTypes = -1;
		for (int i = 0; i < markerIconBmps.size(); i++) {
			if (geoItems.size() < markerIconBmps.get(i).getItemMax()) {
				markerTypes = i;
				paint.setTextSize(markerIconBmps.get(markerTypes).getTextSize() * screenDensity);
				Paint.FontMetrics metrics = paint.getFontMetrics();
				txtHeightOffset = (int) ((metrics.bottom + metrics.ascent) / 2.0f);
				break;
			}
		}
		if (markerTypes < 0) {
			markerTypes = markerIconBmps.size() - 1;
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
		cluster.onNotifyDrawFromMarker();
		Projection proj = mapView.getProjection();
		Point p = proj.toPixels(center, null);
		if (p.x < 0 || p.x > mapView.getWidth() || p.y < 0 || p.y > mapView.getHeight()) {
			return;
		}
		MarkerBitmap mkrBmp = markerIconBmps.get(markerTypes);
		Bitmap bmp = selected ? mkrBmp.getBitmapSelect() : mkrBmp.getBitmapNormal();
		Point grid = mkrBmp.getGrid();
		Point gridReal = new Point((int) (grid.x * screenDensity + 0.5f), (int) (grid.y * screenDensity + 0.5f));
		canvas.drawBitmap(bmp, p.x - gridReal.x, p.y - gridReal.y, paint);
		String caption = String.valueOf(geoItems.size());
		int x = p.x;
		int y = p.y - txtHeightOffset;
		canvas.drawText(caption, x, y, paint);
	}

	/**
	 * check if the marker is selected.
	 *
	 * @return true if selected state.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * clears selected state.
	 */
	public void clearSelect() {
		selected = false;
		if (selItem < geoItems.size()) {
			geoItems.get(selItem).setSelect(false);
		}
		setMarkerBitmap();
	}

}
