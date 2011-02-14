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

import fr.ybo.itineraires.bean.ItineraireException;

import java.util.logging.Logger;


public class Adresse {
    private static final Logger logger = Logger.getLogger(Adresse.class.getName());
	private Double latitude;
	private Double longitude;

	public Adresse(String value) {
		if (value == null) {
            logger.severe("Pour construire une adresse il faut une valeur, format : 'latitude|longitude'");
			throw new ItineraireException("Pour construire une adresse il faut une valeur, format : 'latitude|longitude'");
		}
		String[] champs = value.split("\\|");
		if (champs.length != 2) {
            logger.severe("Pour construire une adresse il faut une valeur, format : 'latitude|longitude'");
			throw new ItineraireException("Pour construire une adresse il faut une valeur, format : 'latitude|longitude'");
		}
		try {
			latitude = Double.parseDouble(champs[0]);
			longitude = Double.parseDouble(champs[1]);
		} catch (NumberFormatException exception) {
            logger.severe("Pour construire une adresse il faut une valeur, format : 'latitude|longitude'");
			throw new ItineraireException("Pour construire une adresse il faut une valeur, format : 'latitude|longitude'", exception);
		}
	}

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

    public fr.ybo.itineraires.schema.Adresse convert() {
        fr.ybo.itineraires.schema.Adresse adresseXml = new fr.ybo.itineraires.schema.Adresse();
        adresseXml.setLatitude(latitude);
        adresseXml.setLongitude(longitude);
        return adresseXml;
    }

}
