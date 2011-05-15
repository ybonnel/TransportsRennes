package fr.ybo.transportsbordeaux.tbc;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ybo.transportsbordeaux.util.StringUtils;

public class GetTrajetHandler extends DefaultHandler {

	private static final String BALISE_TR = "tr";
	private static final String BALISE_TD = "td";
	private static final String ATTRIBUT_ID = "id";
	
	private List<PortionTrajet> trajet;

	public GetTrajetHandler() {
		super();
	}

	private PortionTrajet portionCourante;
	
	public List<PortionTrajet> getTrajet() {
		return trajet;
	}

	/**
	 * StringBuilder servant au parsing xml.
	 */
	private StringBuilder contenu;
	private boolean isId = false;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		contenu.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		trajet = new ArrayList<PortionTrajet>();
		contenu = new StringBuilder();
	}
	
	@Override
	public void endElement(String pUri, String pLocalName, String pQName)
			throws SAXException {
		super.endElement(pUri, pLocalName, pQName);
		if (portionCourante != null && pQName.equals(BALISE_TR)) {
			trajet.add(portionCourante);
			portionCourante = null;
		}
		if (portionCourante != null && pQName.equals(BALISE_TD) && !isId) {
			portionCourante.horaire = StringUtils.doubleTrim(contenu.toString());
		}
		if (portionCourante != null && pQName.equals(BALISE_TD) && isId) {
			portionCourante.arret = StringUtils.doubleTrim(contenu.toString());
			isId = false;
		}
		contenu.setLength(0);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(BALISE_TR)) {
			portionCourante = new PortionTrajet();
		}
		if (qName.equals(BALISE_TD) && attributes.getValue(ATTRIBUT_ID) != null) {
			isId = true;
		}
		contenu.setLength(0);

	}
}
