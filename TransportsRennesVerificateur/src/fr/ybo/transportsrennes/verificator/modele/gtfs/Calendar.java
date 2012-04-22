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

import java.util.Date;

import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterDate;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.moteurcsv.annotation.Param;

/**
 * Un calendar GTFS.
 * 
 * @author ybonnel
 * 
 */
@FichierCsv
public class Calendar {

	// CHECKSTYLE:OFF
	@BaliseCsv(value = "service_id", ordre = 0, obligatoire = true)
	private String id;
	@BaliseCsv(value = "monday", adapter = AdapterBoolean.class, ordre = 1, obligatoire = true)
	private boolean lundi;
	@BaliseCsv(value = "tuesday", adapter = AdapterBoolean.class, ordre = 2, obligatoire = true)
	private boolean mardi;
	@BaliseCsv(value = "wednesday", adapter = AdapterBoolean.class, ordre = 3, obligatoire = true)
	private boolean mercredi;
	@BaliseCsv(value = "thursday", adapter = AdapterBoolean.class, ordre = 4, obligatoire = true)
	private boolean jeudi;
	@BaliseCsv(value = "friday", adapter = AdapterBoolean.class, ordre = 5, obligatoire = true)
	private boolean vendredi;
	@BaliseCsv(value = "saturday", adapter = AdapterBoolean.class, ordre = 6, obligatoire = true)
	private boolean samedi;
	@BaliseCsv(value = "sunday", adapter = AdapterBoolean.class, ordre = 7, obligatoire = true)
	private boolean dimanche;
	@BaliseCsv(value = "start_date", ordre = 8, adapter = AdapterDate.class, obligatoire = true, params = { @Param(
			name = AdapterDate.PARAM_FORMAT, value = "yyyyMMdd") })
	private Date startDate;
	@BaliseCsv(value = "end_date", ordre = 9, adapter = AdapterDate.class, obligatoire = true, params = { @Param(
			name = AdapterDate.PARAM_FORMAT, value = "yyyyMMdd") })
	private Date endDate;

	public Calendar() {

	}

	public String getId() {
		return id;
	}

	public boolean isLundi() {
		return lundi;
	}

	public boolean isMardi() {
		return mardi;
	}

	public boolean isMercredi() {
		return mercredi;
	}

	public boolean isJeudi() {
		return jeudi;
	}

	public boolean isVendredi() {
		return vendredi;
	}

	public boolean isSamedi() {
		return samedi;
	}

	public boolean isDimanche() {
		return dimanche;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

}
