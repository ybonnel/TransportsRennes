package fr.ybo.transportsbordeaux.tbc;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GetHorairesHandler extends DefaultHandler {

	private static final String BALISE_A = "a";
	private static final String BALISE_ABBR = "abbr";
	private static final String ATTRIBUT_HREF = "href";
	private static final String ATTRIBUT_TITLE = "title";
	private static final String ATTRIBUT_CLASS = "class";
	
	private String ligneId;
	private String arretId;
	private boolean erreur = false;

	public boolean isErreur() {
		return erreur;
	}

	public GetHorairesHandler(String ligneId, String arretId) {
		super();
		this.ligneId = ligneId;
		this.arretId = arretId;
	}

	private Horaire horaireCourante;
	private int lastHoraire = 0;
	
	private List<Horaire> horaires;

	public List<Horaire> getHoraires() {
		return horaires;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		horaires = new ArrayList<Horaire>();
	}
	
	@Override
	public void endElement(String pUri, String pLocalName, String pQName)
			throws SAXException {
		super.endElement(pUri, pLocalName, pQName);
		if (horaireCourante != null && pQName.equals(BALISE_A)) {
			if (horaireCourante.horaire != null) {
				horaires.add(horaireCourante);
			}
			horaireCourante = null;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(BALISE_A)) {
			horaireCourante = new Horaire();
			horaireCourante.arretId = arretId;
			horaireCourante.ligneId = ligneId;
			horaireCourante.url = attributes.getValue(ATTRIBUT_HREF);
		}
		if (horaireCourante != null && qName.equals(BALISE_ABBR)
				&& attributes.getValue(ATTRIBUT_CLASS) == null) {
			String horaireChaine = attributes.getValue(ATTRIBUT_TITLE);
			String[] horaireChaineSplit = horaireChaine.split("h");
			if (horaireChaineSplit.length == 2) {
				horaireCourante.horaire = (Integer.parseInt(horaireChaineSplit[0]) * 60)
						+ Integer.parseInt(horaireChaineSplit[1]);
				if (horaireCourante.horaire < lastHoraire && horaireCourante.horaire < (24 * 60)) {
					horaireCourante.horaire += 24 * 60;
				}
				lastHoraire = horaireCourante.horaire;
			} else {
				erreur = true;
			}
		}

	}
}
