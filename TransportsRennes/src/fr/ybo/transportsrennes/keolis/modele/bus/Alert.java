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
	private String title;
	/**
	 * starttime.
	 */
	private String starttime;
	/**
	 * endtime.
	 */
	private String endtime;
	/**
	 * lines.
	 */
	private List<String> lines;

	/**
	 * majordisturbance.
	 */
	private boolean majordisturbance;

	/**
	 * detail.
	 */
	private String detail;

	/**
	 * link.
	 */
	private String link;

	/**
	 * @return the detail
	 */
	public final String getDetail() {
		return detail;
	}

	public String getDetailFormatte(Set<String> arrets) {
		StringBuilder lignes = new StringBuilder();
		for (String line : lines) {
			lignes.append(line);
			lignes.append(", ");
		}
		lignes.deleteCharAt(lignes.length() - 1);
		lignes.deleteCharAt(lignes.length() - 1);
		String detailFormatte = detail.replaceAll(" &nbsp;", "&nbsp;").replaceAll("&nbsp; ", "&nbsp;").replaceAll(" &nbsp;", "&nbsp;")
				.replaceAll("&nbsp; ", "&nbsp;").replaceAll("&nbsp;&nbsp;", "&nbsp;")
				.replaceAll("&nbsp;", " " + lignes.toString() + " ");
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
			resultatChaine = resultatChaine.replaceAll(arretToBold, "<b>"
					+ arretToBold + "</b>");
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

	/**
	 * @return the endtime
	 */
	public final String getEndtime() {
		return endtime;
	}

	/**
	 * @return the lines
	 */
	public final List<String> getLines() {
		if (lines == null) {
			lines = new ArrayList<String>();
		}
		return lines;
	}

	/**
	 * @return the link
	 */
	public final String getLink() {
		return link;
	}

	/**
	 * @return the starttime
	 */
	public final String getStarttime() {
		return starttime;
	}

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @return the majordisturbance
	 */
	public final boolean isMajordisturbance() {
		return majordisturbance;
	}

	/**
	 * @param pDetail the detail to set
	 */
	public final void setDetail(final String pDetail) {
		detail = pDetail;
	}

	/**
	 * @param pEndtime the endtime to set
	 */
	public final void setEndtime(final String pEndtime) {
		endtime = pEndtime;
	}

	/**
	 * @param pLink the link to set
	 */
	public final void setLink(final String pLink) {
		link = pLink;
	}

	/**
	 * @param pMajordisturbance the majordisturbance to set
	 */
	public final void setMajordisturbance(final boolean pMajordisturbance) {
		majordisturbance = pMajordisturbance;
	}

	/**
	 * @param pStarttime the starttime to set
	 */
	public final void setStarttime(final String pStarttime) {
		starttime = pStarttime;
	}

	/**
	 * @param pTitle the title to set
	 */
	public final void setTitle(final String pTitle) {
		title = pTitle;
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

	/**
	 * @return le titre de l'alert formatté.
	 */
	@Override
	public final String toString() {
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
