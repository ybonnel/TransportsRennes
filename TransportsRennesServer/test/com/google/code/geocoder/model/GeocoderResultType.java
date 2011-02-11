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
public enum GeocoderResultType {

	STREET_ADDRESS("street_address"),
	ROUTE("route"),
	INTERSECTION("intersection"),
	POLITICAL("political"),
	COUNTRY("country"),
	ADMINISTRATIVE_AREA_LEVEL_1("administrative_area_level_1"),
	ADMINISTRATIVE_AREA_LEVEL_2("administrative_area_level_2"),
	ADMINISTRATIVE_AREA_LEVEL_3("administrative_area_level_3"),
	COLLOQUIAL_AREA("colloquial_area"),
	LOCALITY("locality"),
	SUBLOCALITY("sublocality"),
	NEIGHBORHOOD("neighborhood"),
	PREMISE("premise"),
	SUBPREMISE("subpremise"),
	POSTAL_CODE("postal_code"),
	NATURAL_FEATURE("natural_feature"),
	AIRPORT("airport"),
	PARK("park"),
	POINT_OF_INTEREST("point_of_interest"),
	POST_BOX("post_box"),
	STREET_NUMBER("street_number"),
	FLOOR("floor"),
	ROOM("room");

	private final String value;

	GeocoderResultType(final String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static GeocoderResultType fromValue(final String v) {
		for (GeocoderResultType c : GeocoderResultType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}