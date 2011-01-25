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


package fr.ybo.transportsrennes.map.mapviewutil;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.maps.GeoPoint;

/**
 * Utility Class to handle GeoItem for ClusterMarker
 * @author Huan Erdao
 */
public class GeoItem implements Parcelable {
	
	/** id of item. */
	protected long id_;
	/** item location in GeoPoint. */
	protected GeoPoint location_;
	/** selection state flag. true if selected.*/
	protected boolean isSelected_;
	
	/**
	 * @param id item id.
	 * @param latitudeE6 latitude of the item in microdegrees (degrees * 1E6).
	 * @param longitudeE6 longitude of the item in microdegrees (degrees * 1E6).
	 */
	public GeoItem(long id, int latitudeE6, int longitudeE6 ) {
		id_ = id;
		location_ = new GeoPoint(latitudeE6, longitudeE6);
		isSelected_ = false;
	}

	/**
	 * @param src source GeoItem
	 */
	public GeoItem(GeoItem src) {
		id_ = src.id_;
		location_ = new GeoPoint(src.location_.getLatitudeE6(),src.location_.getLongitudeE6());
		isSelected_ = src.isSelected_;
	}

	/**
	 * @param src source Parcel
	 */
	public GeoItem(Parcel src) {
		id_ = src.readLong();
		location_ = new GeoPoint(src.readInt(), src.readInt());
		isSelected_ = src.readInt() == 0 ? false : true;
	}

	/* describeContents */
	public int describeContents() {
		return 0;
	}

	/**
	 * getId
	 * @return id of the item.
	 */
	public long getId() {
		return id_;
	}
	
	/**
	 * setId
	 * @param id of the item.
	 */
	public void setId(long id) {
		id_ = id;;
	}

	/**
	 * getLocation
	 * @return GeoPoint of the item.
	 */
	public GeoPoint getLocation() {
		return location_;
	}

	/**
	 * isSelected
	 * @return true if the item is in selected state.
	 */
	public boolean isSelected() {
		return isSelected_;
	}

	/**
	 * setSelect
	 * @param flg flag to be set.
	 */
	public void setSelect(boolean flg) {
		isSelected_ = flg;
	}
	
	/**
	 * Parcelable.Creator
	 */
	public static final Creator<GeoItem> CREATOR =
		new Creator<GeoItem>() {
		public GeoItem createFromParcel(Parcel in) {
			return new GeoItem(in);
		}
		public GeoItem[] newArray(int size) {
			return new GeoItem[size];
		}
	};

	/**
	 * writeToParcel
	 * @param parcel Parcel to be written.
	 * @param flags flag.
	 */
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id_);
		parcel.writeInt(location_.getLatitudeE6());
		parcel.writeInt(location_.getLongitudeE6());
		int flg = isSelected_ ? 1 : 0;
		parcel.writeInt(flg);
   }

}