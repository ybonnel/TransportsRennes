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

package fr.ybo.itineraires.modele;

import fr.ybo.gtfs.modele.Calendrier;

import java.util.Calendar;

public enum EnumCalendrier {
	LUNDI(1) {
		@Override
		public boolean isCalendrierValide(Calendrier calendrier) {
			return calendrier.lundi;
		}

		@Override
		public EnumCalendrier veille() {
			return DIMANCHE;
		}
	},
	MARDI(2) {
		@Override
		public boolean isCalendrierValide(Calendrier calendrier) {
			return calendrier.mardi;
		}

		@Override
		public EnumCalendrier veille() {
			return LUNDI;
		}
	},
	MERCREDI(3) {
		@Override
		public boolean isCalendrierValide(Calendrier calendrier) {
			return calendrier.mercredi;
		}

		@Override
		public EnumCalendrier veille() {
			return MARDI;
		}
	},
	JEUDI(4) {
		@Override
		public boolean isCalendrierValide(Calendrier calendrier) {
			return calendrier.jeudi;
		}

		@Override
		public EnumCalendrier veille() {
			return MERCREDI;
		}
	},
	VENDREDI(5) {
		@Override
		public boolean isCalendrierValide(Calendrier calendrier) {
			return calendrier.vendredi;
		}

		@Override
		public EnumCalendrier veille() {
			return JEUDI;
		}
	},
	SAMEDI(6) {
		@Override
		public boolean isCalendrierValide(Calendrier calendrier) {
			return calendrier.samedi;
		}

		@Override
		public EnumCalendrier veille() {
			return VENDREDI;
		}
	},
	DIMANCHE(7) {
		@Override
		public boolean isCalendrierValide(Calendrier calendrier) {
			return calendrier.dimanche;
		}

		@Override
		public EnumCalendrier veille() {
			return SAMEDI;
		}
	};


	private final int numCalendrier;

	private EnumCalendrier(int numCalendrier) {
		this.numCalendrier = numCalendrier;
	}

	public abstract boolean isCalendrierValide(Calendrier calendrier);

	public abstract EnumCalendrier veille();

	public static EnumCalendrier fromNumCalendrier(Integer numCalendrier) {
        if (numCalendrier == null) {
            return null;
        }
		for (EnumCalendrier value : values()) {
			if (value.numCalendrier == numCalendrier) {
				return value;
			}
		}
		return null;
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
