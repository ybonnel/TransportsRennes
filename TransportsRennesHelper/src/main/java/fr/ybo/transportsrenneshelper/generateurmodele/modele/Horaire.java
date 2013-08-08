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


import fr.ybonnel.csvengine.adapter.AdapterBoolean;
import fr.ybonnel.csvengine.adapter.AdapterInteger;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Horaire.
 * @author ybonnel
 *
 */
@CsvFile
public class Horaire {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "arret_id", order = 0)
	public String arretId;
	@CsvColumn(value = "trajet_id", adapter = AdapterInteger.class, order = 1)
	public int trajetId;
	@CsvColumn(value = "heure_depart", adapter = AdapterInteger.class, order = 2)
	public int heureDepart;
	@CsvColumn(value = "stop_sequence", adapter = AdapterInteger.class, order = 3)
	public int stopSequence;
	@CsvColumn(value = "terminus", adapter = AdapterBoolean.class, order = 4)
	public boolean terminus;

	public Trajet trajet;
	
	public int nbOctets() {
		return arretId.length() + 1 + Integer.toString(trajetId).length() + 1 + Integer.toString(heureDepart).length()
				+ Integer.toString(stopSequence).length() + 3;
	}
}
