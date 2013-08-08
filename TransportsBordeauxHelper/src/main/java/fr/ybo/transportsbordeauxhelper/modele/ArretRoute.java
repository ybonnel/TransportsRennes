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


import fr.ybonnel.csvengine.adapter.AdapterInteger;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Représente l'association Arrêt <-> route.
 * @author ybonnel
 *
 */
@CsvFile
public class ArretRoute {
	// CHECKSTYLE:OFF
	@CsvColumn( value = "arret_id", order = 0)
	public String arretId;
	@CsvColumn( value = "ligne_id", order = 1)
	public String ligneId;
	@CsvColumn( value = "direction_id", adapter = AdapterInteger.class, order = 2)
	public int directionId;
	@CsvColumn( value = "sequence", adapter = AdapterInteger.class, order = 3)
	public int sequence;
	@CsvColumn(value = "macro_direction", adapter = AdapterInteger.class, order = 4)
	public Integer macroDirection = 0;

}
