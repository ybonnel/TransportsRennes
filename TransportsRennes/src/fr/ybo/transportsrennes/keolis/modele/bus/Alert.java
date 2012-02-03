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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import fr.ybo.transportscommun.util.Formatteur;

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
        title = alert.title;
        starttime = alert.starttime;
        endtime = alert.endtime;
        majordisturbance = alert.majordisturbance;
        detail = alert.detail;
        link = alert.link;
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
    public final List<String> lines = new ArrayList<String>(4);

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

    public String getDetailFormatte(Iterable<String> arrets) {
		if (detail == null) {
			return "";
		}
        String detailFormatte =
                detail.replaceAll(" &nbsp;", "&nbsp;").replaceAll("&nbsp; ", "&nbsp;").replaceAll(" &nbsp;", "&nbsp;").replaceAll("&nbsp; ", "&nbsp;")
                        .replaceAll("&nbsp;&nbsp;", "&nbsp;").replaceAll("&nbsp;", " ");
        StringBuilder resultat = new StringBuilder();
        char carOld = '\0';
        for (char car : detailFormatte.toCharArray()) {
            //noinspection OverlyComplexBooleanExpression
            if ((carOld >= '0' && carOld <= '9' || carOld >= 'a' && carOld <= 'z' || carOld == 'é') && car >= 'A' && car <= 'Z') {
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
        StringBuilder stringBuilder = new StringBuilder();
        for (String champ : champs) {
            if (champ.startsWith("Ligne")) {
                stringBuilder.append("<br/><b>");
            }
            stringBuilder.append(champ);
            if (champ.startsWith("Ligne")) {
                stringBuilder.append("</b>");
            }
            stringBuilder.append("<br/>");
        }
        return stringBuilder.toString();
    }

    private static final Collection<Character> CARAC_TO_DELETE = new HashSet<Character>(11);

    static {
        CARAC_TO_DELETE.add(' ');
        CARAC_TO_DELETE.add('0');
        CARAC_TO_DELETE.add('1');
        CARAC_TO_DELETE.add('2');
        CARAC_TO_DELETE.add('3');
        CARAC_TO_DELETE.add('4');
        CARAC_TO_DELETE.add('5');
        CARAC_TO_DELETE.add('6');
        CARAC_TO_DELETE.add('7');
        CARAC_TO_DELETE.add('8');
        CARAC_TO_DELETE.add('9');
    }

    public CharSequence getTitleFormate() {
        String titleFormate = title;
        while (titleFormate.length() > 0 && CARAC_TO_DELETE.contains(titleFormate.charAt(0))) {
            titleFormate = titleFormate.substring(1);
            if (titleFormate.startsWith("TTZ")) {
                titleFormate = titleFormate.substring(3);
            } else if (titleFormate.startsWith("kl")) {
                titleFormate = titleFormate.substring(2);
			} else if (titleFormate.startsWith("SDN")) {
				titleFormate = titleFormate.substring(3);
            }
        }
        return Formatteur.formatterChaine(titleFormate);
    }

}
