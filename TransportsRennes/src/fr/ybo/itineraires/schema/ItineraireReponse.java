package fr.ybo.itineraires.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"serial"})
public class ItineraireReponse implements Serializable {

	public String erreur;
	public Adresse adresseDepart;
	public Adresse adresseArrivee;
	private ArrayList<Trajet> trajets;


	public Collection<Trajet> getTrajets() {
		if (trajets == null) {
			trajets = new ArrayList<Trajet>(3);
		}
		return trajets;
	}

}
