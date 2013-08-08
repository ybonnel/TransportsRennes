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
package fr.ybo.transportsbordeauxhelper.modele;

import fr.ybonnel.csvengine.adapter.AdapterDouble;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Un arrêt.
 * @author ybonnel
 *
 */
@CsvFile
public class Arret {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "id", order = 0)
	public String id;
	@CsvColumn(value = "nom", order = 1)
	public String nom;
	@CsvColumn(value = "latitude", adapter = AdapterDouble.class, order = 2)
	public double latitude;
	@CsvColumn(value = "longitude", adapter = AdapterDouble.class, order = 3)
	public double longitude;
}
