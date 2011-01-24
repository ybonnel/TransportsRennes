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

package fr.ybo.transportsrennes.keolis.modele.bus;

import fr.ybo.transportsrennes.util.Formatteur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class représentant une alerte Keolis.
 *
 * @author ybonnel
 *
 */

/**
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class Alert implements Serializable {

	public Alert() {
	}

	public Alert(Alert alert) {
		this.title = alert.title;
		this.starttime = alert.starttime;
		this.endtime = alert.endtime;
		this.majordisturbance = alert.majordisturbance;
		this.detail = alert.detail;
		this.link = alert.link;
	}

	/**
	 * title.
	 */
	public String title;
	/**
	 * starttime.
	 */
	public String starttime;
	/**
	 * endtime.
	 */
	public String endtime;
	/**
	 * lines.
	 */
	public List<String> lines = new ArrayList<String>();

	/**
	 * majordisturbance.
	 */
	public boolean majordisturbance;

	/**
	 * detail.
	 */
	public String detail;

	/**
	 * link.
	 */
	public String link;

	public String getDetailFormatte(Set<String> arrets) {
		StringBuilder lignes = new StringBuilder();
		for (String line : lines) {
			lignes.append(line);
			lignes.append(", ");
		}
		lignes.deleteCharAt(lignes.length() - 1);
		lignes.deleteCharAt(lignes.length() - 1);
		String detailFormatte =
				detail.replaceAll(" &nbsp;", "&nbsp;").replaceAll("&nbsp; ", "&nbsp;").replaceAll(" &nbsp;", "&nbsp;").replaceAll("&nbsp; ", "&nbsp;")
						.replaceAll("&nbsp;&nbsp;", "&nbsp;").replaceAll("&nbsp;", " ");
		StringBuilder resultat = new StringBuilder();
		char carOld = '\0';
		for (char car : detailFormatte.toCharArray()) {
			if (((carOld >= '0' && carOld <= '9') || (carOld >= 'a' && carOld <= 'z') || (carOld == 'é')) && car >= 'A' && car <= 'Z') {
				// Minuscule suivie d'une majuscule, ça doit être un retour à la ligne qui manque.
				resultat.append(".\n");
			}
			resultat.append(car);
			carOld = car;
		}

		String resultatChaine = resultat.toString();
		for (String arretToBold : arrets) {
			resultatChaine = resultatChaine.replaceAll(arretToBold, "<b>" + arretToBold + "</b>");
		}

		// recherche des lignes à mettre en gras.
		String[] champs = resultatChaine.split("\n");
		resultat = new StringBuilder();
		for (String champ : champs) {
			if (champ.startsWith("Ligne")) {
				resultat.append("<br/><b>");
			}
			resultat.append(champ);
			if (champ.startsWith("Ligne")) {
				resultat.append("</b>");
			}
			resultat.append("<br/>");
		}
		return resultat.toString();
	}

	private static final HashSet<Character> caracToDelete = new HashSet<Character>();
	static {
		 caracToDelete.add(' ');
		 caracToDelete.add('0');
		 caracToDelete.add('1');
		 caracToDelete.add('2');
		 caracToDelete.add('3');
		 caracToDelete.add('4');
		 caracToDelete.add('5');
		 caracToDelete.add('6');
		 caracToDelete.add('7');
		 caracToDelete.add('8');
		 caracToDelete.add('9');
	}

	public String getTitleFormate() {
		String titleFormate = title;
		while (caracToDelete.contains(titleFormate.charAt(0))) {
			titleFormate = titleFormate.substring(1);
		}
		return Formatteur.formatterChaine(titleFormate);
	}

}
