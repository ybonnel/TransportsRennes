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
 *
 * @author Huan Erdao
 */
public class GeoItem implements Parcelable {

    /**
     * id of item.
     */
    private final long id;
    /**
     * item location in GeoPoint.
     */
    private final GeoPoint location;
    /**
     * selection state flag. true if selected.
     */
    private boolean isSelected;

    /**
     * @param id          item id.
     * @param latitudeE6  latitude of the item in microdegrees (degrees * 1E6).
     * @param longitudeE6 longitude of the item in microdegrees (degrees * 1E6).
     */
    protected GeoItem(long id, int latitudeE6, int longitudeE6) {
        this.id = id;
        location = new GeoPoint(latitudeE6, longitudeE6);
        isSelected = false;
    }

    /* describeContents */
    public int describeContents() {
        return 0;
    }

    /**
     * getLocation
     *
     * @return GeoPoint of the item.
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * selected
     *
     * @return true if the item is in selected state.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * setSelect
     *
     * @param flg flag to be set.
     */
    public void setSelect(boolean flg) {
        isSelected = flg;
    }

    /**
     * writeToParcel
     *
     * @param parcel Parcel to be written.
     * @param flags  flag.
     */
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeInt(location.getLatitudeE6());
        parcel.writeInt(location.getLongitudeE6());
        int flg = isSelected ? 1 : 0;
        parcel.writeInt(flg);
    }

}
