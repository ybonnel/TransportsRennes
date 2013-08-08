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
package fr.ybo.transportsbordeauxhelper.gtfs.modele;


import fr.ybonnel.csvengine.adapter.AdapterBoolean;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Un calendar GTFS.
 * 
 * @author ybonnel
 * 
 */
@CsvFile
public class Calendar {

	@CsvColumn(value = "service_id", order = 0)
	public String id;
	@CsvColumn(value = "monday", adapter = AdapterBoolean.class, order = 1)
	public boolean lundi;
	@CsvColumn(value = "tuesday", adapter = AdapterBoolean.class, order = 2)
	public boolean mardi;
	@CsvColumn(value = "wednesday", adapter = AdapterBoolean.class, order = 3)
	public boolean mercredi;
	@CsvColumn(value = "thursday", adapter = AdapterBoolean.class, order = 4)
	public boolean jeudi;
	@CsvColumn(value = "friday", adapter = AdapterBoolean.class, order = 5)
	public boolean vendredi;
	@CsvColumn(value = "saturday", adapter = AdapterBoolean.class, order = 6)
	public boolean samedi;
	@CsvColumn(value = "sunday", adapter = AdapterBoolean.class, order = 7)
	public boolean dimanche;
	@CsvColumn(value = "start_date", order = 8)
	public String startDate;
	@CsvColumn(value = "end_date", order = 9)
	public String endDate;

	public Calendar() {

	}

	/**
	 * Constructeur par copie.
	 * @param calendar
	 */
	public Calendar(Calendar calendar) {
		lundi = calendar.lundi;
		mardi = calendar.mardi;
		mercredi = calendar.mercredi;
		jeudi = calendar.jeudi;
		vendredi = calendar.vendredi;
		samedi = calendar.samedi;
		dimanche = calendar.dimanche;
		startDate = calendar.startDate;
		endDate = calendar.endDate;
	}

	/**
	 * Merge de deux calendar.
	 * @param calendar
	 */
	public void merge(Calendar calendar) {
		lundi = lundi || calendar.lundi;
		mardi = mardi || calendar.mardi;
		mercredi = mercredi || calendar.mercredi;
		jeudi = jeudi || calendar.jeudi;
		vendredi = vendredi || calendar.vendredi;
		samedi = samedi || calendar.samedi;
		dimanche = dimanche || calendar.dimanche;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		Calendar calendar = (Calendar) obj;

		return dimanche == calendar.dimanche && jeudi == calendar.jeudi && lundi == calendar.lundi
				&& mardi == calendar.mardi && mercredi == calendar.mercredi && samedi == calendar.samedi
				&& vendredi == calendar.vendredi;

	}

	@Override
	public int hashCode() {
		int result = lundi ? 1 : 0;
		result = 31 * result + (mardi ? 1 : 0);
		result = 31 * result + (mercredi ? 1 : 0);
		result = 31 * result + (jeudi ? 1 : 0);
		result = 31 * result + (vendredi ? 1 : 0);
		result = 31 * result + (samedi ? 1 : 0);
		result = 31 * result + (dimanche ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Calendar{" + "id='" + id + '\'' + ", lundi=" + lundi + ", mardi=" + mardi + ", mercredi=" + mercredi
				+ ", jeudi=" + jeudi + ", vendredi=" + vendredi + ", samedi=" + samedi + ", dimanche=" + dimanche + '}';
	}
}
