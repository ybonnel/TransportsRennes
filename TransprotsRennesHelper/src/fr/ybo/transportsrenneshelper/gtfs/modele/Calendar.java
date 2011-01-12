/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

	}

	public Calendar(Calendar calendar) {
		this.lundi = calendar.lundi;
		this.mardi = calendar.mardi;
		this.mercredi = calendar.mercredi;
		this.jeudi = calendar.jeudi;
		this.vendredi = calendar.vendredi;
		this.samedi = calendar.samedi;
		this.dimanche = calendar.dimanche;
	}

	public void merge(Calendar calendar) {
		this.lundi = this.lundi || calendar.lundi;
		this.mardi = this.mardi || calendar.mardi;
		this.mercredi = this.mercredi || calendar.mercredi;
		this.jeudi = this.jeudi || calendar.jeudi;
		this.vendredi = this.vendredi || calendar.vendredi;
		this.samedi = this.samedi || calendar.samedi;
		this.dimanche = this.dimanche || calendar.dimanche;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Calendar)) {
			return false;
		}

		Calendar calendar = (Calendar) o;

		if (dimanche != calendar.dimanche) {
			return false;
		}
		if (jeudi != calendar.jeudi) {
			return false;
		}
		if (lundi != calendar.lundi) {
			return false;
		}
		if (mardi != calendar.mardi) {
			return false;
		}
		if (mercredi != calendar.mercredi) {
			return false;
		}
		if (samedi != calendar.samedi) {
			return false;
		}
		if (vendredi != calendar.vendredi) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = (lundi ? 1 : 0);
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
