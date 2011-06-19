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
package fr.ybo.transportsrennes.util;

public final class Formatteur {

	private Formatteur() {
	}

	public static String formatterChaine(String chaine) {
		StringBuilder nomLongFormateBuilder = new StringBuilder();
		for (String champ : chaine.replaceAll("/", "-").split(" ")) {
			for (String champ2 : champ.split("\\(")) {
				if (champ2.length() > 0) {
					nomLongFormateBuilder.append(champ2.substring(0, 1).toUpperCase());
					nomLongFormateBuilder.append(champ2.substring(1, champ2.length()).toLowerCase());
				}
				nomLongFormateBuilder.append('(');
			}
			// on enleve le dernier tiret.
			nomLongFormateBuilder.deleteCharAt(nomLongFormateBuilder.length() - 1);
			nomLongFormateBuilder.append(' ');
		}
		// on enleve le dernier espace.
		nomLongFormateBuilder.deleteCharAt(nomLongFormateBuilder.length() - 1);
		String nomLongFormate = nomLongFormateBuilder.toString().replaceAll("\\|", "");
		while (nomLongFormate.contains("  ")) {
			nomLongFormate = nomLongFormate.replaceAll("  ", " ");
		}
		while (nomLongFormate.length() > 0 && nomLongFormate.charAt(0) == ' ') {
			nomLongFormate = nomLongFormate.substring(1);
		}
		return nomLongFormate;
	}
}
