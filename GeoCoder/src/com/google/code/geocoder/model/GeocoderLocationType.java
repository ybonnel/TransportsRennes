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
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public enum GeocoderLocationType {
	APPROXIMATE,
	GEOMETRIC_CENTER,
	RANGE_INTERPOLATED,
	ROOFTOP;

	public String value() {
		return name();
	}

	public static GeocoderLocationType fromValue(String v) {
		return valueOf(v);
	}
}