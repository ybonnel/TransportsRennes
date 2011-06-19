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

import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("agency.txt")
public class Agency {
	@BaliseCsv(value = "agency_name", ordre = 1)
	public String agencyName;
	@BaliseCsv(value = "agency_url", ordre = 2)
	public String agencyUrl;
	@BaliseCsv(value = "agency_timezone", ordre = 3)
	public String agencyTimezone;
	@BaliseCsv(value = "agency_lang", ordre = 4)
	public String agencyLang;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agencyLang == null) ? 0 : agencyLang.hashCode());
		result = prime * result + ((agencyName == null) ? 0 : agencyName.hashCode());
		result = prime * result + ((agencyTimezone == null) ? 0 : agencyTimezone.hashCode());
		result = prime * result + ((agencyUrl == null) ? 0 : agencyUrl.hashCode());
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
		Agency other = (Agency) obj;
		if (agencyLang == null) {
			if (other.agencyLang != null)
				return false;
		} else if (!agencyLang.equals(other.agencyLang))
			return false;
		if (agencyName == null) {
			if (other.agencyName != null)
				return false;
		} else if (!agencyName.equals(other.agencyName))
			return false;
		if (agencyTimezone == null) {
			if (other.agencyTimezone != null)
				return false;
		} else if (!agencyTimezone.equals(other.agencyTimezone))
			return false;
		if (agencyUrl == null) {
			if (other.agencyUrl != null)
				return false;
		} else if (!agencyUrl.equals(other.agencyUrl))
			return false;
		return true;
	}

}
