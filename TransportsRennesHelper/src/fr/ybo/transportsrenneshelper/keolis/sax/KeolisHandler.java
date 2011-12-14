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
package fr.ybo.transportsrenneshelper.keolis.sax;

import fr.ybo.transportsrenneshelper.keolis.modele.Answer;
import fr.ybo.transportsrenneshelper.keolis.modele.StatusKeolis;
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
	private Answer<ObjetKeolis> answer;

	/**
	 * Objet Keolis courant.
	 */
	private ObjetKeolis currentObjetKeolis;

	/**
	 * StringBuilder servant au parsing xml.
	 */
	private StringBuilder contenu;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		contenu.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (answer != null) {
			if (qName.equals(getBaliseData())) {
				answer.getData().add(currentObjetKeolis);
			} else {
				remplirObjectKeolis(currentObjetKeolis, qName, contenu.toString());
			}
			contenu.setLength(0);
		}
	}

	/**
	 * Getter.
	 *
	 * @return réponse de l'API getdistrict.
	 */
	public Answer<ObjetKeolis> getAnswer() {
		return answer;
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
	protected abstract void remplirObjectKeolis(ObjetKeolis currentObjectKeolis, String baliseName,
			String contenuOfBalise);

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		contenu = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(ANSWER)) {
			answer = new Answer<ObjetKeolis>();
		} else if (qName.equals(STATUS)) {
			answer.setStatus(new StatusKeolis());
			answer.getStatus().setCode(attributes.getValue(attributes.getIndex(CODE)));
			answer.getStatus().setMessage(attributes.getValue(attributes.getIndex(MESSAGE)));
		} else if (qName.equals(getBaliseData())) {
			currentObjetKeolis = getNewObjetKeolis();
		}
		contenu.setLength(0);
	}

}
