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


import fr.ybo.itineraires.modele.Adresse;
import fr.ybo.itineraires.modele.EnumCalendrier;
import fr.ybo.itineraires.modele.ItineraireReponse;
import fr.ybo.itineraires.modele.ItineraireRequete;
import fr.ybo.itineraires.util.Chrono;
import fr.ybo.itineraires.util.Key;
import fr.ybo.itineraires.util.RechercheCircuit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("itineraires/")
public class ItineraireBean {

    private static final Logger logger = Logger.getLogger(ItineraireBean.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("xml")
    public static Response calculItineraireXml(@QueryParam("key") String key, @QueryParam("adresseDepart") Adresse adresseDepart,
                                                                                  @QueryParam("adresseArrivee") Adresse adresseArrivee,
                                                                                  @QueryParam("heureDepart") Integer heureDepart,
                                                                                  @QueryParam("calendrier") EnumCalendrier calendrier) {
        fr.ybo.itineraires.schema.ItineraireReponse reponse = calculItineraire(key, adresseDepart, adresseArrivee, heureDepart, calendrier);
        Response.ResponseBuilder responseBuilder;
        responseBuilder = Response.status(Response.Status.OK);
        responseBuilder = responseBuilder.type(MediaType.APPLICATION_XML);
        responseBuilder = responseBuilder.entity(reponse);
        return responseBuilder.build();
    }

    private static fr.ybo.itineraires.schema.ItineraireReponse calculItineraire(String key, Adresse adresseDepart, Adresse adresseArrivee, Integer heureDepart, EnumCalendrier calendrier) {
        ItineraireReponse reponse;
        try {
            Key.isValid(key);
            ItineraireRequete requete = new ItineraireRequete(adresseDepart, adresseArrivee, calendrier, heureDepart);
            RechercheCircuit rechercheCircuit = new RechercheCircuit(requete.getAdresseDepart(), requete.getAdresseArrivee());
            for (Chrono chrono : rechercheCircuit.calculCircuits(requete.getCalendrier(), requete.getHeureDepart())) {
                chrono.spool();
            }
            reponse = new ItineraireReponse();
            reponse.setAdresseDepart(requete.getAdresseDepart());
            reponse.setAdresseArrivee(requete.getAdresseArrivee());
            reponse.getTrajets().addAll(rechercheCircuit.getBestTrajets());
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Erreur lors du calcul d'itineraires", exception);
            reponse = new ItineraireReponse();
            reponse.setErreur(exception.getMessage());
        }
        return ItinerairesConverter.convert(reponse);

    }
}
