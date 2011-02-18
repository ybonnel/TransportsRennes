package fr.ybo.itineraires;


import fr.ybo.itineraires.schema.Adresse;
import fr.ybo.itineraires.schema.ItineraireReponse;
import fr.ybo.itineraires.schema.JointureCorrespondance;
import fr.ybo.itineraires.schema.JointurePieton;
import fr.ybo.itineraires.schema.PortionTrajet;
import fr.ybo.itineraires.schema.PortionTrajetBus;
import fr.ybo.itineraires.schema.Trajet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;

class ItinerairesHandler extends DefaultHandler {

	private static final String ROOT = "ItineraireReponse";
	private static final String ERREUR = "erreur";
	private static final String ADRESSE_DEPART = "adresseDepart";
	private static final String ADRESSE_ARRIVEE = "adresseArrivee";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String TRAJETS = "trajets";
	private static final String PORTIONS = "portions";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private StringBuilder contenu;

	private ItineraireReponse reponse;

	public ItineraireReponse getReponse() {
		return reponse;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		contenu.append(ch, start, length);
	}

	private boolean adresseDepartEnCours = false;
	private boolean adresseArriveeEnCours = false;
	private Trajet trajetEncours = null;
	private PortionTrajet portionEnCours = null;

	private void traiterAdresse(Adresse adresse, String qName) {
		if (qName.equals(LATITUDE)) {
			adresse.latitude = Double.parseDouble(contenu.toString());
		}
		if (qName.equals(LONGITUDE)) {
			adresse.latitude = Double.parseDouble(contenu.toString());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (adresseDepartEnCours) {
			traiterAdresse(reponse.adresseDepart, qName);
			if (qName.equals(ADRESSE_DEPART)) {
				adresseDepartEnCours = false;
			}
		} else if (adresseArriveeEnCours) {
			traiterAdresse(reponse.adresseArrivee, qName);
			if (qName.equals(ADRESSE_ARRIVEE)) {
				adresseArriveeEnCours = false;
			}
		} else if (portionEnCours != null) {
			portionEnCours.remplirAttribut(qName, contenu.toString());
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
		if (qName.equals(ROOT)) {
			reponse = new ItineraireReponse();
		} else if (qName.equals(ADRESSE_DEPART)) {
			reponse.adresseDepart = new Adresse();
		} else if (qName.equals(ADRESSE_ARRIVEE)) {
			reponse.adresseArrivee = new Adresse();
		} else if (qName.equals(TRAJETS)) {
			if (trajetEncours != null) {
				if (portionEnCours != null) {
					trajetEncours.getPortions().add(portionEnCours);
					portionEnCours = null;
				}
				reponse.getTrajets().add(trajetEncours);
			}
			trajetEncours = new Trajet();
		} else if (qName.equals(PORTIONS)) {
			if (portionEnCours != null) {
				trajetEncours.getPortions().add(portionEnCours);
			}
			String clazz = attributes.getValue("xsi:type");
			creerPortion(clazz);
		}
		contenu.setLength(0);
	}

	private void creerPortion(String clazz) {
		if (clazz.equals(JointurePieton.class.getSimpleName())) {
			portionEnCours = new JointurePieton();
		}
		if (clazz.equals(PortionTrajetBus.class.getSimpleName())) {
			portionEnCours = new PortionTrajetBus();
		}
		if (clazz.equals(JointureCorrespondance.class.getSimpleName())) {
			portionEnCours = new JointureCorrespondance();
		}
	}
}
