package fr.ybo.itineraires.modele;

import java.util.ArrayList;
import java.util.List;

public class ItineraireReponse {
	private String erreur;
	private List<Adresse> adresses1;
	private List<Adresse> adresses2;

	public String getErreur() {
		return erreur;
	}
	public void setErreur(String erreur) {
		this.erreur = erreur;
	}
	public List<Adresse> getAdresses1() {
		if (adresses1 == null) {
			adresses1 = new ArrayList<Adresse>();
		}
		return adresses1;
	}
	public List<Adresse> getAdresses2() {
		if (adresses2 == null) {
			adresses2 = new ArrayList<Adresse>();
		}
		return adresses2;
	}

}
