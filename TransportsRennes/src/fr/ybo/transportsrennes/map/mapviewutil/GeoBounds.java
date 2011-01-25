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

package fr.ybo.transportsrennes.map.mapviewutil;

import com.google.android.maps.GeoPoint;

/**
 * Utility Class to handle GeoBounds, which is not available for 1.5SDK.
 * @author Huan Erdao
 */
public class GeoBounds {

	/** North-West geo point of the bound */
	private GeoPoint nw_;
	/** South-East geo point of the bound */
	private GeoPoint se_;

	/**
	 * @param nw North-West geo point of the bound
	 * @param se South-East geo point of the bound
	 */
	public GeoBounds( GeoPoint nw, GeoPoint se ){
		nw_ = nw;
		se_ = se;
	}
	
	/**
	 * @param pt a GeoPoint to be checked
	 * @return true if point is in the bound.
	 */
	public boolean isInBounds( GeoPoint pt ){
		if(pt==null)
			return false;
		return (pt.getLatitudeE6()<=nw_.getLatitudeE6()
				&&pt.getLatitudeE6()>=se_.getLatitudeE6()
				&&pt.getLongitudeE6()>=nw_.getLongitudeE6()
				&&pt.getLongitudeE6()<=se_.getLongitudeE6());
	}

	/**
	 * @return South-East point of the bound
	 */
	public GeoPoint getSouthEast(){
		return se_;
	}

	/**
	 * @return North-West point of the bound
	 */
	public GeoPoint getNorthWest(){
		return nw_;
	}

	/**
	 * @param src a GeoBounds to be checked
	 * @return true if the bound are same
	 */
	public Boolean isEqual(GeoBounds src){
		return (nw_.getLatitudeE6()==src.nw_.getLatitudeE6()&&
				nw_.getLongitudeE6()==src.nw_.getLongitudeE6()&&
				se_.getLatitudeE6()==src.se_.getLatitudeE6()&&
				se_.getLongitudeE6()==src.se_.getLongitudeE6());
	}


}
