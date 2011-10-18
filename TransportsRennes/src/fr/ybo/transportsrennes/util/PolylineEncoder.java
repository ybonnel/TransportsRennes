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
package fr.ybo.transportsrennes.util;

import java.util.ArrayList;
import java.util.List;

import fr.ybo.opentripplanner.client.modele.EncodedPolylineBean;

public class PolylineEncoder {

	public static List<Coordinate> decode(EncodedPolylineBean polyline) {

		String pointString = polyline.getPoints();

		double lat = 0;
		double lon = 0;

		int strIndex = 0;
		List<Coordinate> points = new ArrayList<Coordinate>();

		while (strIndex < pointString.length()) {

			int[] rLat = decodeSignedNumberWithIndex(pointString, strIndex);
			lat = lat + rLat[0] * 1e-5;
			strIndex = rLat[1];

			int[] rLon = decodeSignedNumberWithIndex(pointString, strIndex);
			lon = lon + rLon[0] * 1e-5;
			strIndex = rLon[1];

			points.add(new Coordinate(lat, lon));
		}

		return points;
	}

	public static String encodeSignedNumber(int num) {
		int sgn_num = num << 1;
		if (num < 0) {
			sgn_num = ~(sgn_num);
		}
		return (encodeNumber(sgn_num));
	}

	public static int decodeSignedNumber(String value) {
		int[] r = decodeSignedNumberWithIndex(value, 0);
		return r[0];
	}

	private static int[] decodeSignedNumberWithIndex(String value, int index) {
		int[] r = decodeNumberWithIndex(value, index);
		int sgn_num = r[0];
		if ((sgn_num & 0x01) > 0) {
			sgn_num = ~(sgn_num);
		}
		r[0] = sgn_num >> 1;
		return r;
	}

	private static String encodeNumber(int num) {

        StringBuilder encodeString = new StringBuilder();

		while (num >= 0x20) {
			int nextValue = (0x20 | (num & 0x1f)) + 63;
			encodeString.append((char) (nextValue));
			num >>= 5;
		}

		num += 63;
		encodeString.append((char) (num));

		return encodeString.toString();
	}

	public static int decodeNumber(String value) {
		int[] r = decodeNumberWithIndex(value, 0);
		return r[0];
	}

	private static int[] decodeNumberWithIndex(String value, int index) {

		if (value.length() == 0)
			throw new IllegalArgumentException("string is empty");

		int num = 0;
		int v;
		int shift = 0;

		do {
			v = value.charAt(index++) - 63;
			num |= (v & 0x1f) << shift;
			shift += 5;
		} while (v >= 0x20);

		return new int[] { num, index };
	}
}
