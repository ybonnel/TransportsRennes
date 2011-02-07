package fr.ybo.transportsrennes.twitter;


import fr.ybo.transportsrennes.keolis.modele.Answer;
import fr.ybo.transportsrennes.keolis.modele.StatusKeolis;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetTwittersHandler extends DefaultHandler {

	private static final String MESSAGES = "messages";
	private static final String MESSAGE = "message";
	private static final String DATE_CREATION = "dateCreation";
	private static final String CONTENU = "contenu";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private StringBuilder contenu = null;

	private MessageTwitter messageCourant = null;

	private ArrayList<MessageTwitter> messages = null;

	public ArrayList<MessageTwitter> getMessages() {
		return messages;
	}

	@Override
	public final void characters(final char[] ch, final int start, final int length) throws SAXException {
		super.characters(ch, start, length);
		this.contenu.append(ch, start, length);
	}

	@Override
	public final void endElement(final String uri, final String localName, final String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (this.messageCourant != null) {
			if (name.equals(DATE_CREATION)) {
				try {
					this.messageCourant.dateCreation = SDF.parse(contenu.toString());
				} catch (ParseException e) {
					this.messageCourant.dateCreation = new Date();
				}
			} else if (name.equals(CONTENU)) {
				this.messageCourant.texte = contenu.toString();
			} else if (name.equals(MESSAGE)) {
				this.messages.add(this.messageCourant);
			}
			this.contenu.setLength(0);
		}
	}
	@Override
	public final void startDocument() throws SAXException {
		super.startDocument();
		this.contenu = new StringBuilder();
	}

	@Override
	public final void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (name.equals(MESSAGES)) {
			this.messages = new ArrayList<MessageTwitter>();
		} else if (name.equals(MESSAGE)) {
			this.messageCourant = new MessageTwitter();
		}
		this.contenu.setLength(0);
	}
}
