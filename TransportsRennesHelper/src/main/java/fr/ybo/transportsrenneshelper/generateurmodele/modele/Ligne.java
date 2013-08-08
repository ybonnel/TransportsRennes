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
 * Réprésente une ligne.
 * @author ybonnel
 *
 */
@CsvFile
public class Ligne {
	// CHECKSTYLE:OFF
	@CsvColumn("id")
	public String id;
	@CsvColumn("nom_court")
	public String nomCourt;
	@CsvColumn("nom_long")
	public String nomLong;
	@CsvColumn(value = "ordre", adapter = AdapterInteger.class)
	public int ordre;
}
