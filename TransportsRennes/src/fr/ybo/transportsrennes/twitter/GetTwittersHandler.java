package fr.ybo.transportsrennes.twitter;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

class GetTwittersHandler extends DefaultHandler {

	private static final String MESSAGES = "messages";
	private static final String MESSAGE = "message";
	private static final String DATE_CREATION = "dateCreation";
	private static final String CONTENU = "contenu";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private StringBuilder contenu;

	private MessageTwitter messageCourant;

	private Collection<MessageTwitter> messages;

	public Collection<MessageTwitter> getMessages() {
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
