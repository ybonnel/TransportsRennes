package fr.ybo.transportsbordeauxhelper.modeletcb.sax;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ybo.transportsbordeauxhelper.modeletcb.Arret;

public class GetArretsHandler extends DefaultHandler {

	private static final String OPTION = "option";

	private StringBuilder contenu;
	private Arret arretCourant;
	private List<Arret> arrets;

	public List<Arret> getArrets() {
		return arrets;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		contenu.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (OPTION.equals(qName) && arretCourant != null) {
			arretCourant.nom = contenu.toString();
			arrets.add(arretCourant);
			arretCourant = null;
		}
		contenu.setLength(0);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		contenu = new StringBuilder();
		arrets = new ArrayList<Arret>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(OPTION)) {
			String value = attributes.getValue("value");
			if (!"0".equals(value)) {
				arretCourant = new Arret();
				arretCourant.identifant = value;
			}
		}
		contenu.setLength(0);
	}
}
