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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.ybo.transportsrennes.map.mapviewutil.markerclusterer;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Utility Class to handle MarkerBitmap
 * it handles grid offset to display on the map with offset
 *
 * @author Huan Erdao
 */
public class MarkerBitmap {

	/**
	 * bitmap object for normal state icon
	 */
	private final Bitmap iconBmpNormal;
	/**
	 * bitmap object for select state icon
	 */
	private final Bitmap iconBmpSelect;
	/**
	 * offset grid of icon in Point.
	 * if you are using symmetric icon image, it should be half size of width&height.
	 * adjust this parameter to offset the axis of the image.
	 */
    private Point iconGrid = new Point();
	/**
	 * icon size in Point. x = width, y = height
	 */
	private final Point iconSize = new Point();
	/**
	 * maximum item size for the marker.
	 * for the last MarkerBitmap element within list, this will be ignored.
	 */
	private final int itemSizeMax;
	/**
	 * text size for icon
	 */
	private final int textSize;

	/**
	 * NOTE: srcNrm & srcSel must be same bitmap size.
	 *
	 * @param srcNrm   source Bitmap object for normal state
	 * @param srcSel   source Bitmap object for select state
	 * @param grid     grid point to be offset
	 * @param textSize text size for icon
	 * @param maxSize  icon size threshold
	 */
	public MarkerBitmap(Bitmap srcNrm, Bitmap srcSel, Point grid, int textSize, int maxSize) {
		iconBmpNormal = srcNrm;
		iconBmpSelect = srcSel;
		iconGrid = grid;
		this.textSize = textSize;
		itemSizeMax = maxSize;
		iconSize.x = srcNrm.getWidth();
		iconSize.y = srcNrm.getHeight();
	}

	/**
	 * @return bitmap object for normal state icon
	 */
	public Bitmap getBitmapNormal() {
		return iconBmpNormal;
	}

	/**
	 * @return bitmap object for select state icon
	 */
	public Bitmap getBitmapSelect() {
		return iconBmpSelect;
	}

	/**
	 * @return get offset grid
	 */
	public Point getGrid() {
		return iconGrid;
	}

	/**
	 * @return text size
	 */
	public int getTextSize() {
		return textSize;
	}

	/**
	 * @return icon size threshold
	 */
	public int getItemMax() {
		return itemSizeMax;
	}

	/**
	 * returns icon size in Point. x = width, y = height.
	 *
	 * @return get bitmap size in Point
	 */
	public Point getSize() {
		return iconSize;
	}

}
