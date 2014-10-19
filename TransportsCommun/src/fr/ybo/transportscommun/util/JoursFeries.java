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
package fr.ybo.transportscommun.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Gestion des jours fériés.
 */
public class JoursFeries {

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT_1ER_MAI = new SimpleDateFormat("ddMM");

	private JoursFeries() {
	}

	public static boolean is1erMai(Date date) {
		return "0105".equals(SIMPLE_DATE_FORMAT_1ER_MAI.format(date));
	}

}
