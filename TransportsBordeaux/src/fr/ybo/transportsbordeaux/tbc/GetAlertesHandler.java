package fr.ybo.transportsbordeaux.tbc;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ybo.transportsbordeaux.modele.Alert;
import fr.ybo.transportsbordeaux.util.StringUtils;

public class GetAlertesHandler extends DefaultHandler {

	private static final String BALISE_TR = "tr";
	private static final String BALISE_TD = "td";
	private static final String BALISE_A = "a";
	private static final String TD_LIGNE = "views-field views-field-title-1";
	private static final String ATTRIBUT_HREF = "href";
	private static final String ATTRIBUT_CLASS = "class";
	

	public GetAlertesHandler() {
		super();
	}

	private Alert alertCourante;
	
	private List<Alert> alertes;

	public List<Alert> getAlertes() {
		return alertes;
	}

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
	public void startDocument() throws SAXException {
		super.startDocument();
		alertes = new ArrayList<Alert>();
		contenu = new StringBuilder();
	}
	
	private boolean ligneEncours = false;

	@Override
	public void endElement(String pUri, String pLocalName, String qName)
			throws SAXException {
		super.endElement(pUri, pLocalName, qName);
		if (qName.equals(BALISE_TR)) {
			alertes.add(alertCourante);
			alertCourante = null;
		}
		if (ligneEncours && qName.equals(BALISE_TD)) {
			String ligne = StringUtils.doubleTrim(contenu.toString());
			alertCourante.ligne = ligne;
			ligneEncours = false;
		}
		if (qName.equals(BALISE_A)) {
			alertCourante.title = contenu.toString();
		}
		contenu.setLength(0);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(BALISE_TR)) {
			alertCourante = new Alert();
		}
		if (qName.equals(BALISE_TD) && TD_LIGNE.equals(attributes.getValue(ATTRIBUT_CLASS))) {
			ligneEncours = true;
		}
		if (qName.equals(BALISE_A)) {
			alertCourante.url = attributes.getValue(ATTRIBUT_HREF);
		}
		contenu.setLength(0);

	}
}
