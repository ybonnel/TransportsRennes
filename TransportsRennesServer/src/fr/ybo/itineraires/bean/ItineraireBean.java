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
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ItineraireBean extends Application {

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/itineraires", ItineraireServerResource.class);
		return router;
	}


	private static final Logger LOGGER = Logger.getLogger(ItineraireBean.class.getName());

	public static fr.ybo.itineraires.schema.ItineraireReponse calculItineraire(String key, Adresse adresseDepart, Adresse adresseArrivee,
	                                                                           Integer heureDepart, EnumCalendrier calendrier) {
		ItineraireReponse reponse;
		try {
			Key.valid(key);
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
			LOGGER.log(Level.SEVERE, "Erreur lors du calcul d'itineraires", exception);
			reponse = new ItineraireReponse();
			reponse.setErreur(exception.getMessage());
		}
		return ItinerairesConverter.convert(reponse);

	}
}
