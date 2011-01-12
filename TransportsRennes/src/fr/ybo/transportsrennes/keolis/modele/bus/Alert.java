package fr.ybo.transportsrennes.keolis.modele.bus;

import fr.ybo.transportsrennes.util.Formatteur;

import java.io.Serializable;
import java.util.ArrayList;
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
						.replaceAll("&nbsp;&nbsp;", "&nbsp;").replaceAll("&nbsp;", " " + lignes.toString() + " ");
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

	public String getTitleFormate() {
		String titleFormate = title;
		for (String ligneConcernee : lines) {
			titleFormate = titleFormate.replaceAll(ligneConcernee, "");
		}
		if (titleFormate.startsWith(" ")) {
			titleFormate = titleFormate.substring(1);
		}
		return Formatteur.formatterChaine(titleFormate);
	}

}
