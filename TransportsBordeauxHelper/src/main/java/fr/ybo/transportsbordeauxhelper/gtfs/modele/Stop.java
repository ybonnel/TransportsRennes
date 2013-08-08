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
package fr.ybo.transportsbordeauxhelper.gtfs.modele;

import fr.ybonnel.csvengine.adapter.AdapterDouble;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Un stop GTFS.
 * @author ybonnel
 *
 */
@CsvFile
public class Stop {
	// stop_id,stop_name,stop_lat,stop_lon,stop_desc,zone_id,stop_url,stop_code
	@CsvColumn(value = "stop_id", order = 0)
	public String id;
	@CsvColumn(value = "stop_name", order = 1)
	public String nom;
	@CsvColumn(value = "stop_lat", adapter = AdapterDouble.class, order = 2)
	public double latitude;
	@CsvColumn(value = "stop_lon", adapter = AdapterDouble.class, order = 3)
	public double longitude;
	@CsvColumn(value = "stop_desc", order = 4)
	public String stopDesc;
	@CsvColumn(value = "zone_id", order = 5)
	public String zoneId;
	@CsvColumn(value = "stop_url", order = 6)
	public String stopUrl;
	@CsvColumn(value = "stop_code", order = 7)
	public String stopCode;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stop other = (Stop) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Stop{" + "id='" + id + '\'' + ", nom='" + nom + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + '}';
	}
}
