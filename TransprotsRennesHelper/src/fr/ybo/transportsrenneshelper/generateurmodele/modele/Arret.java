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
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterDouble;

@FichierCsv( "arrets.txt")
public class Arret {
	@BaliseCsv("id")
	public String id;
	@BaliseCsv("nom")
	public String nom;
	@BaliseCsv(value = "latitude", adapter = AdapterDouble.class)
	public double latitude;
	@BaliseCsv(value = "longitude", adapter = AdapterDouble.class)
	public double longitude;
}
