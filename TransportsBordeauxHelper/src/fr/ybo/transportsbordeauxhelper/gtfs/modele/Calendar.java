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

package fr.ybo.transportsbordeauxhelper.gtfs.modele;

import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Un calendar GTFS.
 * 
 * @author ybonnel
 * 
 */
@FichierCsv("calendar.txt")
public class Calendar {

	@BaliseCsv(value = "service_id", ordre = 0)
	public String id;
	@BaliseCsv(value = "monday", adapter = AdapterBoolean.class, ordre = 1)
	public boolean lundi;
	@BaliseCsv(value = "tuesday", adapter = AdapterBoolean.class, ordre = 2)
	public boolean mardi;
	@BaliseCsv(value = "wednesday", adapter = AdapterBoolean.class, ordre = 3)
	public boolean mercredi;
	@BaliseCsv(value = "thursday", adapter = AdapterBoolean.class, ordre = 4)
	public boolean jeudi;
	@BaliseCsv(value = "friday", adapter = AdapterBoolean.class, ordre = 5)
	public boolean vendredi;
	@BaliseCsv(value = "saturday", adapter = AdapterBoolean.class, ordre = 6)
	public boolean samedi;
	@BaliseCsv(value = "sunday", adapter = AdapterBoolean.class, ordre = 7)
	public boolean dimanche;
	@BaliseCsv(value = "start_date", ordre = 8)
	public String startDate;
	@BaliseCsv(value = "end_date", ordre = 9)
	public String endDate;
}
