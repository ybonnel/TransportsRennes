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
package fr.ybo.transportsbordeaux.util;

public class StringUtils {

	public static String doubleTrim(String string) {
		String retour = string;
		while (retour.charAt(0) == ' ') {
			retour = retour.substring(1);
		}
		while (retour.charAt(retour.length() - 1) == ' ') {
			retour = retour.substring(0, retour.length() - 2);
		}
		return retour;
	}

}
