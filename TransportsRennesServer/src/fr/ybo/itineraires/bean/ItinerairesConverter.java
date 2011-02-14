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

package fr.ybo.itineraires.bean;

import fr.ybo.itineraires.modele.PortionTrajet;
import fr.ybo.itineraires.schema.ItineraireReponse;
import fr.ybo.itineraires.schema.Trajet;

/**
 * Auteur: ybonnel
 * Date: 14/02/11
 * Time: 22:50
 */
public class ItinerairesConverter {

    public static ItineraireReponse convert(fr.ybo.itineraires.modele.ItineraireReponse reponse) {
        ItineraireReponse reponseXml = new ItineraireReponse();
        reponseXml.setErreur(reponse.getErreur());
        if (reponse.getAdresseDepart() != null) {
            reponseXml.setAdresseDepart(reponse.getAdresseDepart().convert());
        }
        if (reponse.getAdresseArrivee() != null) {
            reponseXml.setAdresseArrivee(reponse.getAdresseArrivee().convert());
        }
        for (fr.ybo.itineraires.modele.Trajet trajet : reponse.getTrajets()) {
            reponseXml.getTrajets().add(convert(trajet));
        }
        return reponseXml;
    }

    protected static Trajet convert(fr.ybo.itineraires.modele.Trajet trajet) {
        if (trajet == null) {
            return null;
        }
        Trajet trajetXml = new Trajet();
        for (PortionTrajet portion : trajet.getPortionsTrajet()) {
            trajetXml.getPortions().add(portion.convert());
        }
        return trajetXml;
    }
}
