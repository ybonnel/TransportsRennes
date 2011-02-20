
package fr.ybo.itineraires.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItineraireReponse {

    protected String erreur;
    protected Adresse adresseDepart;
    protected Adresse adresseArrivee;
    protected List<Trajet> trajets;

    public String getErreur() {
        return erreur;
    }

    public void setErreur(final String value) {
	    erreur = value;
    }

    public Adresse getAdresseDepart() {
        return adresseDepart;
    }

    public void setAdresseDepart(final Adresse value) {
	    adresseDepart = value;
    }

    public Adresse getAdresseArrivee() {
        return adresseArrivee;
    }

    public void setAdresseArrivee(final Adresse value) {
	    adresseArrivee = value;
    }

    public Collection<Trajet> getTrajets() {
        if (trajets == null) {
            trajets = new ArrayList<Trajet>();
        }
        return trajets;
    }

}
