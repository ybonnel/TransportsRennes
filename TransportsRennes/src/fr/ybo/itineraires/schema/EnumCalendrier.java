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

package fr.ybo.itineraires.schema;

import java.util.Calendar;

public enum EnumCalendrier {
	LUNDI(1),
	MARDI(2),
	MERCREDI(3),
	JEUDI(4),
	VENDREDI(5),
	SAMEDI(6),
	DIMANCHE(7);

	private final int numCalendrier;

	@SuppressWarnings({"WeakerAccess"})
	EnumCalendrier(int numCalendrier) {
		this.numCalendrier = numCalendrier;
	}

	public int getNumCalendrier() {
		return numCalendrier;
	}

	public static EnumCalendrier fromFieldCalendar(int field) {
		switch (field) {
			case Calendar.MONDAY:
				return LUNDI;
			case Calendar.TUESDAY:
				return MARDI;
			case Calendar.WEDNESDAY:
				return MERCREDI;
			case Calendar.THURSDAY:
				return JEUDI;
			case Calendar.FRIDAY:
				return VENDREDI;
			case Calendar.SATURDAY:
				return SAMEDI;
			case Calendar.SUNDAY:
				return DIMANCHE;
			default:
				return null;
		}
	}
}
