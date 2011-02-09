package fr.ybo.itineraires.modele;

import fr.ybo.gtfs.modele.Arret;

public class Circuit {
	private Arret arretDepart;
	private Arret arretArrivee;
	public Circuit(Arret arretDepart, Arret arretArrivee) {
		super();
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
	}
	
	public boolean rechercheTrajetBus() {
		return true;
	}
}
