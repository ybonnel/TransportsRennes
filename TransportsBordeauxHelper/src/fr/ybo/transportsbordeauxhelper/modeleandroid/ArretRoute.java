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

package fr.ybo.transportsbordeauxhelper.modeleandroid;


import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("arrets_routes.txt")
public class ArretRoute {
	@BaliseCsv(value = "arret_id", ordre = 1)
	public String arretId;
	@BaliseCsv(value = "ligne_id", ordre = 2)
	public String ligneId;
	@BaliseCsv(value = "macro_direction", adapter = AdapterInteger.class, ordre = 3)
	public Integer macroDirection;
	@BaliseCsv(value = "direction_id", adapter = AdapterInteger.class, ordre = 4)
	public Integer directionId;

}
