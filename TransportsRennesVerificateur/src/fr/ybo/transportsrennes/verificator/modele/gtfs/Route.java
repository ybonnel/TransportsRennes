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
package fr.ybo.transportsrennes.verificator.modele.gtfs;

import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Une route.
 * @author ybonnel
 *
 */
@FichierCsv
public class Route {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "route_id", ordre = 0, obligatoire = true)
	private String id;
	@BaliseCsv(value = "agency_id", ordre = 1, obligatoire = true)
	private String agencyId;
	@BaliseCsv(value = "route_short_name", ordre = 2, obligatoire = true)
	private String nomCourt;
	@BaliseCsv(value = "route_long_name", ordre = 3, obligatoire = true)
	private String nomLong;
	@BaliseCsv(value = "route_desc", ordre = 4)
	private String description;
	@BaliseCsv(value = "route_type", ordre = 5, obligatoire = true)
	private String type;
	@BaliseCsv(value = "route_url", ordre = 6)
	private String url;
	@BaliseCsv(value = "route_color", ordre = 7)
	private String color;
	@BaliseCsv(value = "route_text_color", ordre = 8)
	private String textColor;

	public String getId() {
		return id;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public String getNomCourt() {
		return nomCourt;
	}

	public String getNomLong() {
		return nomLong;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getColor() {
		return color;
	}

	public String getTextColor() {
		return textColor;
	}

}
