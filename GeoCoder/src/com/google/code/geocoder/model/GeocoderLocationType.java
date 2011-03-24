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

package com.google.code.geocoder.model;

/**
 * Type de géo-localisation.
 * 
 * @author ybonnel
 * 
 */
public enum GeocoderLocationType {
	/**
	 * Indicates that the returned result is approximate.
	 */
	APPROXIMATE,
	/**
	 * Indicates that the returned result is the geometric center of a result
	 * such as a polyline (for example, a street) or polygon (region).
	 */
	GEOMETRIC_CENTER,
	/**
	 * Indicates that the returned result reflects an approximation (usually on
	 * a road) interpolated between two precise points (such as intersections).
	 * Interpolated results are generally returned when rooftop geocodes are
	 * unavailable for a street address.
	 */
	RANGE_INTERPOLATED,
	/**
	 * Indicates that the returned result reflects a precise geocode.
	 */
	ROOFTOP;
}