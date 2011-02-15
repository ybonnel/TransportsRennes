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
 * @author Huan Erdao
 */
public class MarkerBitmap {
	
	/** bitmap object for normal state icon */
	protected final Bitmap iconBmpNormal_;
	/** bitmap object for select state icon */
	protected final Bitmap iconBmpSelect_;
	/** offset grid of icon in Point.
	 * if you are using symmetric icon image, it should be half size of width&height.
	 * adjust this parameter to offset the axis of the image. */
	protected Point iconGrid_ = new Point();
	/** icon size in Point. x = width, y = height */
	protected final Point iconSize_ = new Point();
	/** maximum item size for the marker.
	 * for the last MarkerBitmap element within list, this will be ignored.
	 */
	protected final int itemSizeMax_;
	/** text size for icon */
	protected final int textSize_;

	/**
	 * NOTE: srcNrm & srcSel must be same bitmap size.
	 * @param srcNrm	source Bitmap object for normal state
	 * @param srcSel	source Bitmap object for select state
	 * @param grid		grid point to be offset
	 * @param textSize	text size for icon
	 * @param maxSize	icon size threshold
	 */
	public MarkerBitmap( final Bitmap srcNrm, final Bitmap srcSel, final Point grid, final int textSize, final int maxSize ) {
		super();
		iconBmpNormal_ = srcNrm;
		iconBmpSelect_ = srcSel;
		iconGrid_ = grid;
		textSize_ = textSize;
		itemSizeMax_ = maxSize;
		iconSize_.x = srcNrm.getWidth();
		iconSize_.y = srcNrm.getHeight();
	}

	/**
	 * @return bitmap object for normal state icon
	 */
	public final Bitmap getBitmapNormal(){
		return iconBmpNormal_;
	}

	/**
	 * @return bitmap object for select state icon
	 */
	public final Bitmap getBitmapSelect(){
		return iconBmpSelect_;
	}

	/**
	 * @return get offset grid
	 */
	public final Point getGrid(){
		return iconGrid_;
	}

	/**
	 * @return text size
	 */
	public final int getTextSize(){
		return textSize_;
	}

	/**
	 * @return icon size threshold
	 */
	public final int getItemMax(){
		return itemSizeMax_;
	}
	
	/**
	 * returns icon size in Point. x = width, y = height.
	 * @return get bitmap size in Point
	 */
	public final Point getSize(){
		return iconSize_;
	}
	
}
