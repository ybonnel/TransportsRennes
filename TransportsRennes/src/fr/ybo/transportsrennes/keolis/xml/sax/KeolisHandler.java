/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.Answer;
import fr.ybo.transportsrennes.keolis.modele.StatusKeolis;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler pour les appels à Keolis.
 *
 * @param <ObjetKeolis>
 * @author ybonnel
 */
public abstract class KeolisHandler<ObjetKeolis> extends DefaultHandler {

	/**
	 * Nom de la balise answer.
	 */
	private static final String ANSWER = "answer";

	/**
	 * Nom de la balise status.
	 */
	private static final String STATUS = "status";
	/**
	 * Nom de la balise code.
	 */
	private static final String CODE = "code";
	/**
	 * Nom de la balise message.
	 */
	private static final String MESSAGE = "message";

	/**
	 * Réponse de l'API getdistrict.
	 */
	private Answer<ObjetKeolis> answer = null;

	/**
	 * Objet Keolis courant.
	 */
	private ObjetKeolis currentObjetKeolis;

	/**
	 * StringBuilder servant au parsing xml.
	 */
	private StringBuilder contenu = null;

	@Override
	public final void characters(final char[] ch, final int start, final int length) throws SAXException {
		super.characters(ch, start, length);
		this.contenu.append(ch, start, length);
	}

	@Override
	public final void endElement(final String uri, final String localName, final String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (this.answer != null) {
			if (localName.equals(getBaliseData())) {
				this.answer.getData().add(this.currentObjetKeolis);
			} else {
				remplirObjectKeolis(this.currentObjetKeolis, localName, this.contenu.toString());
			}
			surchargeEndElement(localName);
			this.contenu.setLength(0);
		}
	}

	/**
	 * Getter.
	 *
	 * @return réponse de l'API getdistrict.
	 */
	public final Answer<ObjetKeolis> getAnswer() {
		return this.answer;
	}

	/**
	 * Méthode à implémenter donnant le nom de la balise englobante.
	 *
	 * @return le nom de la balise englobante.
	 */
	protected abstract String getBaliseData();

	/**
	 * Méthode à implémenter créant un nouvel objet Keolis.
	 *
	 * @return nouvel objet Keolis.
	 */
	protected abstract ObjetKeolis getNewObjetKeolis();

	/**
	 * Méthode à implémenter remplissant le contenu d'un objet Keolis.
	 *
	 * @param currentObjectKeolis objet Keolis courant.
	 * @param baliseName          nom de la balise.
	 * @param contenuOfBalise     contenu de la balise.
	 */
	protected abstract void remplirObjectKeolis(ObjetKeolis currentObjectKeolis, String baliseName, String contenuOfBalise);

	@Override
	public final void startDocument() throws SAXException {
		super.startDocument();
		this.contenu = new StringBuilder();
	}

	@Override
	public final void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equals(ANSWER)) {
			this.answer = new Answer<ObjetKeolis>();
		} else if (localName.equals(STATUS)) {
			this.answer.setStatus(new StatusKeolis());
			this.answer.getStatus().setCode(attributes.getValue(attributes.getIndex(CODE)));
			this.answer.getStatus().setMessage(attributes.getValue(attributes.getIndex(MESSAGE)));
		} else if (localName.equals(getBaliseData())) {
			this.currentObjetKeolis = getNewObjetKeolis();
		}
		this.contenu.setLength(0);
	}

	/**
	 * Méthode permettant de surcharger le comportement sur endElement.
	 *
	 * @param localName nom de la balise.
	 */
	@SuppressWarnings("unused")
	public void surchargeEndElement(String localName) {
	}

}
