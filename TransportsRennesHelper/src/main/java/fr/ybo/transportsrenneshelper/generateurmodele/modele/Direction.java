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
package fr.ybo.transportsrenneshelper.generateurmodele.modele;


import fr.ybonnel.csvengine.adapter.AdapterInteger;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Représente une direction.
 * @author ybonnel
 *
 */
@CsvFile
public class Direction {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "id", adapter = AdapterInteger.class, order = 0)
	public int id;
	@CsvColumn(value = "direction", order = 1)
	public String direction;
}
