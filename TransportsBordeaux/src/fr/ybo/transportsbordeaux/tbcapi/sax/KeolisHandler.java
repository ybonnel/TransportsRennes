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
package fr.ybo.transportsbordeaux.tbcapi.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

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
    private static final String FEATURE_MEMBRE = "featureMember";
    /**
     * Réponse de l'API getdistrict.
     */
    private List<ObjetKeolis> objets = new ArrayList<ObjetKeolis>();

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
        if (localName.equals(FEATURE_MEMBRE)) {
            objets.add(currentObjetKeolis);
        } else {
            remplirObjectKeolis(currentObjetKeolis, localName, contenu.toString());
        }
        contenu.setLength(0);
    }

    /**
     * Getter.
     *
     * @return réponse de l'API getdistrict.
     */
    public List<ObjetKeolis> getObjets() {
        return objets;
    }

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
        if (localName.equals(FEATURE_MEMBRE)) {
            currentObjetKeolis = getNewObjetKeolis();
        }
        contenu.setLength(0);
    }

}
