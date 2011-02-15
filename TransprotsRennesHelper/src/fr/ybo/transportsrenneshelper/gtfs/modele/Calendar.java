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

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterBoolean;

@FichierCsv("calendar.txt")
public class Calendar {

	@BaliseCsv("service_id")
	public String id;
	@BaliseCsv(value = "monday", adapter = AdapterBoolean.class)
	public boolean lundi;
	@BaliseCsv(value = "tuesday", adapter = AdapterBoolean.class)
	public boolean mardi;
	@BaliseCsv(value = "wednesday", adapter = AdapterBoolean.class)
	public boolean mercredi;
	@BaliseCsv(value = "thursday", adapter = AdapterBoolean.class)
	public boolean jeudi;
	@BaliseCsv(value = "friday", adapter = AdapterBoolean.class)
	public boolean vendredi;
	@BaliseCsv(value = "saturday", adapter = AdapterBoolean.class)
	public boolean samedi;
	@BaliseCsv(value = "sunday", adapter = AdapterBoolean.class)
	public boolean dimanche;

	public Calendar() {
		super();

	}

	public Calendar(final Calendar calendar) {
		super();
		lundi = calendar.lundi;
		mardi = calendar.mardi;
		mercredi = calendar.mercredi;
		jeudi = calendar.jeudi;
		vendredi = calendar.vendredi;
		samedi = calendar.samedi;
		dimanche = calendar.dimanche;
	}

	public void merge(final Calendar calendar) {
		lundi = lundi || calendar.lundi;
		mardi = mardi || calendar.mardi;
		mercredi = mercredi || calendar.mercredi;
		jeudi = jeudi || calendar.jeudi;
		vendredi = vendredi || calendar.vendredi;
		samedi = samedi || calendar.samedi;
		dimanche = dimanche || calendar.dimanche;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		final Calendar calendar = (Calendar) obj;

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
