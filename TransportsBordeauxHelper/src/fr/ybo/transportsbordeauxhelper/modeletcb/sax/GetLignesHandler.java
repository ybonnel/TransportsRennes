package fr.ybo.transportsbordeauxhelper.modeletcb.sax;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ybo.transportsbordeauxhelper.modeletcb.Ligne;

public class GetLignesHandler extends DefaultHandler {

	private static final String OPTGROUP = "optgroup";

	private StringBuilder contenu;
	private Ligne ligneCourante;

	private String typeCourant = null;

	private List<Ligne> lignes;

	public List<Ligne> getLignes() {
		return lignes;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		contenu.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (ligneCourante != null) {
			ligneCourante.nom = contenu.toString();
			lignes.add(ligneCourante);
			ligneCourante = null;
		}
		contenu.setLength(0);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		contenu = new StringBuilder();
		lignes = new ArrayList<Ligne>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(OPTGROUP)) {
			typeCourant = attributes.getValue("label");
		} else if (qName.equals("option") && typeCourant != null) {
			ligneCourante = new Ligne();
			ligneCourante.type = typeCourant;
			ligneCourante.identifiant = attributes.getValue("value");
		}
		contenu.setLength(0);
	}
}
