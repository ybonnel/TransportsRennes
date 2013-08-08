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
 * Représente un trajet de bus.
 * @author ybonnel
 *
 */
@CsvFile
public class Trajet {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "id", adapter = AdapterInteger.class)
	public int id;
	@CsvColumn(value = "calendrier_id", adapter = AdapterInteger.class)
	public int calendrierId;
	@CsvColumn("ligne_id")
	public String ligneId;
	@CsvColumn(value = "direction_id", adapter = AdapterInteger.class)
	public int directionId;
	@CsvColumn( value = "macro_direction", adapter = AdapterInteger.class, order = 5)
	public Integer macroDirection;
}
