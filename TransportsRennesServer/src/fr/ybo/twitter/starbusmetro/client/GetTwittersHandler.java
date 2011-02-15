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

package fr.ybo.twitter.starbusmetro.client;


import fr.ybo.twitter.starbusmetro.modele.MessageTwitter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class GetTwittersHandler extends DefaultHandler {

	private static final String MESSAGES = "messages";
	private static final String MESSAGE = "message";
	private static final String DATE_CREATION = "dateCreation";
	private static final String CONTENU = "contenu";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@SuppressWarnings({"StringBufferField"})
	private StringBuilder contenu;

	private MessageTwitter messageCourant;

	private ArrayList<MessageTwitter> messages;

	public ArrayList<MessageTwitter> getMessages() {
		return messages;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		contenu.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (messageCourant != null) {
			if (qName.equals(DATE_CREATION)) {
				try {
					messageCourant.dateCreation = SDF.parse(contenu.toString());
				} catch (ParseException ignore) {
					messageCourant.dateCreation = new Date();
				}
			} else if (qName.equals(CONTENU)) {
				messageCourant.texte = contenu.toString();
			} else if (qName.equals(MESSAGE)) {
				messages.add(messageCourant);
			}
			contenu.setLength(0);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		contenu = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(MESSAGES)) {
			messages = new ArrayList<MessageTwitter>(20);
		} else if (qName.equals(MESSAGE)) {
			messageCourant = new MessageTwitter();
		}
		contenu.setLength(0);
	}
}
