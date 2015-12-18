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

import java.util.regex.Pattern;

import fr.ybo.transportscommun.R;

public final class Formatteur {

	private Formatteur() {
	}

	private static Pattern SLASH = Pattern.compile("/");
	private static Pattern PIPE = Pattern.compile("\\|");
	private static Pattern DOUBLE_SPACE = Pattern.compile("  ");

	public static CharSequence formatterChaine(final String chaine) {
		final StringBuilder nomLongFormateBuilder = new StringBuilder();
		for (final String champ : SLASH.matcher(chaine).replaceAll("-").split(" ")) {
			for (final String champ2 : champ.split("\\(")) {
				if (!champ2.isEmpty()) {
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
		String nomLongFormate = PIPE.matcher(nomLongFormateBuilder).replaceAll("");
		while (nomLongFormate.contains("  ")) {
			nomLongFormate = DOUBLE_SPACE.matcher(nomLongFormate).replaceAll(" ");
		}
		while (!nomLongFormate.isEmpty() && nomLongFormate.charAt(0) == ' ') {
			nomLongFormate = nomLongFormate.substring(1);
		}
		return nomLongFormate;
	}

	public static CharSequence formatterCalendar(final Context context, final int prochainDepart, final int now) {
		final StringBuilder stringBuilder = new StringBuilder();
		final int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append(context.getString(R.string.tropTard));
		} else {
			final int heures = tempsEnMinutes / 60;
			final int minutes = tempsEnMinutes - heures * 60;
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
