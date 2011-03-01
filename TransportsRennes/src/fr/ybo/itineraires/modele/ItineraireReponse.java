/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.itineraires.modele;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItineraireReponse implements Serializable {

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

    public List<Trajet> getTrajets() {
        if (trajets == null) {
            trajets = new ArrayList<Trajet>();
        }
        return trajets;
    }

}
