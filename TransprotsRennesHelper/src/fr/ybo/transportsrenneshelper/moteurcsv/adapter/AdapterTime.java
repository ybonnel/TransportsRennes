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

package fr.ybo.transportsrenneshelper.moteurcsv.adapter;

public class AdapterTime implements AdapterCsv<Integer> {

	public Integer parse(final String chaine) {
		if (chaine == null) {
			return null;
		}
		final String[] champs = chaine.split(":");
		if (champs.length != 3) {
			return null;
		}
		return Integer.parseInt(champs[0]) * 60 + Integer.parseInt(champs[1]);
	}

	public String toString(Integer integer) {
		if (integer == null) {
			return null;
		}
		StringBuilder retour = new StringBuilder();
		int heures;
		int minutes;
		heures = integer / 60;
		minutes = integer - heures * 60;
		if (heures < 10) {
			retour.append('0');
		}
		retour.append(heures);
		retour.append(':');
		if (minutes < 10) {
			retour.append('0');
		}
		retour.append(minutes);
		retour.append(":00");
		return retour.toString();
	}

}
