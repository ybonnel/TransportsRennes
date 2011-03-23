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

package fr.ybo.transportsrenneshelper.gtfs.modele;

import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("calendar.txt")
public class Calendar {

	@BaliseCsv(value = "service_id", ordre = 0)
	public String id;
	@BaliseCsv(value = "monday", adapter = AdapterBoolean.class, ordre = 1)
	public boolean lundi;
	@BaliseCsv(value = "tuesday", adapter = AdapterBoolean.class, ordre = 2)
	public boolean mardi;
	@BaliseCsv(value = "wednesday", adapter = AdapterBoolean.class, ordre = 3)
	public boolean mercredi;
	@BaliseCsv(value = "thursday", adapter = AdapterBoolean.class, ordre = 4)
	public boolean jeudi;
	@BaliseCsv(value = "friday", adapter = AdapterBoolean.class, ordre = 5)
	public boolean vendredi;
	@BaliseCsv(value = "saturday", adapter = AdapterBoolean.class, ordre = 6)
	public boolean samedi;
	@BaliseCsv(value = "sunday", adapter = AdapterBoolean.class, ordre = 7)
	public boolean dimanche;
	@BaliseCsv(value = "start_date", ordre = 8)
	public String startDate;
	@BaliseCsv(value = "end_date", ordre = 9)
	public String endDate;

	public Calendar() {

	}

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

		return dimanche == calendar.dimanche && jeudi == calendar.jeudi && lundi == calendar.lundi && mardi == calendar.mardi &&
				mercredi == calendar.mercredi && samedi == calendar.samedi && vendredi == calendar.vendredi;

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
		return "Calendar{" + "id='" + id + '\'' + ", lundi=" + lundi + ", mardi=" + mardi + ", mercredi=" + mercredi + ", jeudi=" + jeudi +
				", vendredi=" + vendredi + ", samedi=" + samedi + ", dimanche=" + dimanche + '}';
	}
}
