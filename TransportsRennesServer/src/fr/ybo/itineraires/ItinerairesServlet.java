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

package fr.ybo.itineraires;

import fr.ybo.itineraires.modele.Adresse;
import fr.ybo.itineraires.modele.EnumCalendrier;
import fr.ybo.itineraires.modele.ItineraireReponse;
import fr.ybo.itineraires.modele.Trajet;
import fr.ybo.itineraires.util.RechercheCircuit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class ItinerairesServlet extends HttpServlet {


	private static final Logger logger = Logger.getLogger(ItinerairesServlet.class.getName());

	private EnumCalendrier getCalendrier(String calendrierRequete) {
		EnumCalendrier calendrier = null;
		if (calendrierRequete != null) {
			calendrier = EnumCalendrier.fromNumCalendrier(Integer.parseInt(calendrierRequete));
		}
		if (calendrier == null) {
			Calendar calendar = Calendar.getInstance();
			calendrier = EnumCalendrier.fromFieldCalendar(calendar.get(Calendar.DAY_OF_WEEK));
		}
		return calendrier;
	}

	private int getTime(String timeRequete) {
		if (timeRequete != null) {
			return Integer.parseInt(timeRequete);
		} else {
			Calendar calendar = Calendar.getInstance();
			return ((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE));
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		EnumCalendrier calendrier = getCalendrier(req.getParameter("calendrier"));
		int time = getTime(req.getParameter("time"));
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		ItineraireReponse reponse = new ItineraireReponse();
		remplirAdresses(req, reponse);
		// Calcul des cricuits
		RechercheCircuit rechercheCircuit = new RechercheCircuit(reponse.getAdresse1(), reponse.getAdresse2());
		rechercheCircuit.calculCircuits(calendrier, time);
		reponse.getTrajets().addAll(rechercheCircuit.getBestTrajets());
		logger.info("Résultat de la recherche de trajets :");
		for (Trajet trajet : reponse.getTrajets()) {
			logger.info(trajet.toString());
		}
		resp.getWriter().println("OK");
	}

	public void remplirAdresses(HttpServletRequest req, ItineraireReponse reponse) {
		Double latitude1 = Double.parseDouble(req.getParameter("latitude1"));
		Double longitude1 = Double.parseDouble(req.getParameter("longitude1"));
		Double latitude2 = Double.parseDouble(req.getParameter("latitude2"));
		Double longitude2 = Double.parseDouble(req.getParameter("longitude2"));
		reponse.setAdresse1(new Adresse(latitude1, longitude1));
		reponse.setAdresse2(new Adresse(latitude2, longitude2));
	}
}
