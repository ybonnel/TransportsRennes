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

package fr.ybo.itineraires.modele;

public class Adresse {
	private Double latitude;
	private Double longitude;

	public Adresse(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		if (latitude != null) {
			stringBuilder.append("<latitude>");
			stringBuilder.append(latitude);
			stringBuilder.append("</latitude>");
		}
		if (longitude != null) {
			stringBuilder.append("<longitude>");
			stringBuilder.append(longitude);
			stringBuilder.append("</longitude>");
		}
		return stringBuilder.toString();
	}

}
