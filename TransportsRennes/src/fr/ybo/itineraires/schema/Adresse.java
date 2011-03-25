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

package fr.ybo.itineraires.schema;


public class Adresse {

    protected double latitude;
    protected double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double value) {
	    latitude = value;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double value) {
	    longitude = value;
    }

}
