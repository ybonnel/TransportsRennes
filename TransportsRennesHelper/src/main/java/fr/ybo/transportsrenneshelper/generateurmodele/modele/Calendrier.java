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
 * Un calendrier.
 * @author ybonnel
 *
 */
@CsvFile
public class Calendrier {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "id", adapter = AdapterInteger.class, order = 0)
	public int id;
	@CsvColumn(value = "lundi", adapter = AdapterBoolean.class, order = 1)
	public boolean lundi;
	@CsvColumn(value = "mardi", adapter = AdapterBoolean.class, order = 2)
	public boolean mardi;
	@CsvColumn(value = "mercredi", adapter = AdapterBoolean.class, order = 3)
	public boolean mercredi;
	@CsvColumn(value = "jeudi", adapter = AdapterBoolean.class, order = 4)
	public boolean jeudi;
	@CsvColumn(value = "vendredi", adapter = AdapterBoolean.class, order = 5)
	public boolean vendredi;
	@CsvColumn(value = "samedi", adapter = AdapterBoolean.class, order = 6)
	public boolean samedi;
	@CsvColumn(value = "dimanche", adapter = AdapterBoolean.class, order = 7)
	public boolean dimanche;
	@CsvColumn(value = "dateDebut", order = 8)
	public String dateDebut;
	@CsvColumn(value = "dateFin", order = 9)
	public String dateFin;

	public boolean isSemaine() {
		return (lundi && mardi && mercredi && jeudi && vendredi && samedi && !dimanche);
	}

	public boolean isDimanche() {
		return (!lundi && !mardi && !mercredi && !jeudi && !vendredi && !samedi && dimanche);
	}
}
