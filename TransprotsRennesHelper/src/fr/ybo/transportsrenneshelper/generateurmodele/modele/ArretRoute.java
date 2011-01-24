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

package fr.ybo.transportsrenneshelper.generateurmodele.modele;


import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterInteger;

@FichierCsv("arrets_routes.txt")
public class ArretRoute {
	@BaliseCsv("arret_id")
	public String arretId;
	@BaliseCsv("ligne_id")
	public String ligneId;
	@BaliseCsv( value = "direction_id", adapter = AdapterInteger.class)
	public int directionId;
	@BaliseCsv( value = "sequence", adapter = AdapterInteger.class)
	public int sequence;
	@BaliseCsv( value = "accessbile", adapter = AdapterBoolean.class)
	public Boolean accessible;

}
