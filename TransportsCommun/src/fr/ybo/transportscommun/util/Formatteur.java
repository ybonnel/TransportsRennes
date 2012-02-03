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

import android.content.Context;
import fr.ybo.transportscommun.R;

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

	public static String formatterCalendar(Context context, int prochainDepart, int now) {
		StringBuilder stringBuilder = new StringBuilder();
		int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append(context.getString(R.string.tropTard));
		} else {
			int heures = tempsEnMinutes / 60;
			int minutes = tempsEnMinutes - heures * 60;
			boolean tempsAjoute = false;
			if (heures > 0) {
				stringBuilder.append(heures);
				stringBuilder.append(' ');
				stringBuilder.append(context.getString(R.string.miniHeures));
				stringBuilder.append(' ');
				tempsAjoute = true;
			}
			if (minutes > 0) {
				if (heures <= 0) {
					stringBuilder.append(minutes);
					stringBuilder.append(' ');
					stringBuilder.append(context.getString(R.string.miniMinutes));
				} else {
					if (minutes < 10) {
						stringBuilder.append('0');
					}
					stringBuilder.append(minutes);
				}
				tempsAjoute = true;
			}
			if (!tempsAjoute) {
				stringBuilder.append("0 ");
				stringBuilder.append(context.getString(R.string.miniMinutes));
			}
		}
		return stringBuilder.toString();
	}
}
