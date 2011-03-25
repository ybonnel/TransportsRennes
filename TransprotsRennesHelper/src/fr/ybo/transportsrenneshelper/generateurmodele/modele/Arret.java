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

import fr.ybo.moteurcsv.adapter.AdapterDouble;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Un arrêt.
 * @author ybonnel
 *
 */
@FichierCsv("arrets.txt")
public class Arret {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "id", ordre = 0)
	public String id;
	@BaliseCsv(value = "nom", ordre = 1)
	public String nom;
	@BaliseCsv(value = "latitude", adapter = AdapterDouble.class, ordre = 2)
	public double latitude;
	@BaliseCsv(value = "longitude", adapter = AdapterDouble.class, ordre = 3)
	public double longitude;
}
