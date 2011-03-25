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

package fr.ybo.gtfs.modele;


import java.io.Serializable;

import fr.ybo.gtfs.csv.annotation.BaliseCsv;
import fr.ybo.gtfs.csv.annotation.FichierCsv;
import fr.ybo.gtfs.csv.moteur.adapter.AdapterBoolean;
import fr.ybo.gtfs.csv.moteur.adapter.AdapterInteger;

@SuppressWarnings("serial")
@FichierCsv("calendriers.txt")
public class Calendrier implements Serializable {
	@BaliseCsv(value = "id", adapter = AdapterInteger.class)
	public Integer id;
	@BaliseCsv(value = "lundi", adapter = AdapterBoolean.class)
	public Boolean lundi;
	@BaliseCsv(value = "mardi", adapter = AdapterBoolean.class)
	public Boolean mardi;
	@BaliseCsv(value = "mercredi", adapter = AdapterBoolean.class)
	public Boolean mercredi;
	@BaliseCsv(value = "jeudi", adapter = AdapterBoolean.class)
	public Boolean jeudi;
	@BaliseCsv(value = "vendredi", adapter = AdapterBoolean.class)
	public Boolean vendredi;
	@BaliseCsv(value = "samedi", adapter = AdapterBoolean.class)
	public Boolean samedi;
	@BaliseCsv(value = "dimanche", adapter = AdapterBoolean.class)
	public Boolean dimanche;
}
