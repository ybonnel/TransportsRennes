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

package fr.ybo.transportsrennes.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Gestion des jours fériés.
 */
public class JoursFeries {

	private static Set<String> joursFeries = null;

	private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	public static boolean isJourFerie(Date date) {
		return getJoursFeries().contains(SIMPLE_DATE_FORMAT.format(date));
	}

	private static Set<String> getJoursFeries() {
		if (joursFeries != null) {
			return joursFeries;
		}
		joursFeries = new HashSet<String>();
		joursFeries.add("25122010");
		joursFeries.add("01012011");
		joursFeries.add("25042011");
		joursFeries.add("01052011");
		joursFeries.add("08052011");
		joursFeries.add("02062011");
		joursFeries.add("13062011");
		joursFeries.add("14072011");
		joursFeries.add("15082011");
		joursFeries.add("01112011");
		joursFeries.add("11112011");
		joursFeries.add("25122011");
		joursFeries.add("01012012");
		joursFeries.add("09042012");
		joursFeries.add("01052012");
		joursFeries.add("08052012");
		joursFeries.add("17052012");
		joursFeries.add("28052012");
		joursFeries.add("14072012");
		joursFeries.add("15082012");
		joursFeries.add("01112012");
		joursFeries.add("11112012");
		joursFeries.add("25122012");
		joursFeries.add("01012013");
		joursFeries.add("01042013");
		joursFeries.add("01052013");
		joursFeries.add("08052013");
		joursFeries.add("09052013");
		joursFeries.add("19052013");
		joursFeries.add("14072013");
		joursFeries.add("15082013");
		joursFeries.add("01112013");
		joursFeries.add("11112013");
		joursFeries.add("25122013");
		return joursFeries;
	}

}
