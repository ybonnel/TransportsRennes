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

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import fr.ybo.itineraires.modele.*;
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


	private static final Bounds RENNES = new Bounds(47.7168983, 48.3641809, -2.3997704, -1.1775976);

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
		String adresse1 = req.getParameter("adresse1");
		String adresse2 = req.getParameter("adresse2");
		EnumCalendrier calendrier = getCalendrier(req.getParameter("calendrier"));
		int time = getTime(req.getParameter("time"));
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		ItineraireReponse reponse = new ItineraireReponse();
		if (adresse1 == null || adresse2 == null) {
			reponse.setErreur("Il faut spécifier l'adresse1 et l'adresse2");
		} else {
			geoCoderAdresses(adresse1, adresse2, reponse);
			if (reponse.getAdresses1().size() == 1 && reponse.getAdresses2().size() == 1) {
				// Calcul des cricuits
				RechercheCircuit rechercheCircuit = new RechercheCircuit(reponse.getAdresses1().get(0), reponse.getAdresses2().get(0));
				rechercheCircuit.calculCircuits(calendrier, time);
				reponse.getTrajets().addAll(rechercheCircuit.getBestTrajets());
			}
		}
		resp.getWriter().println("OK");
	}

	private void geoCoderAdresses(String adresse1, String adresse2, ItineraireReponse reponse) {
		final Geocoder geocoder = new Geocoder();
		logger.info("Geocodage de la première adresse");
		GeocoderRequest geocoderRequest1 =
				new GeocoderRequestBuilder().setAddress(adresse1).setLanguage("fr").setBounds(RENNES.getLatLngBounds()).getGeocoderRequest();
		GeocodeResponse geocoderResponse1 = geocoder.geocode(geocoderRequest1);
		logger.info("Geocodage de la première adresse terminée");
		logger.info("Geocodage de la deuxième adresse");
		GeocoderRequest geocoderRequest2 =
				new GeocoderRequestBuilder().setAddress(adresse2).setLanguage("fr").setBounds(RENNES.getLatLngBounds()).getGeocoderRequest();
		GeocodeResponse geocoderResponse2 = geocoder.geocode(geocoderRequest2);
		logger.info("Geocodage de la deuxième adresse terminée");
		for (GeocoderResult result : geocoderResponse1.getResults()) {
			reponse.getAdresses1().add(new Adresse(result.getFormattedAddress(), result.getGeometry().getLocation().getLat().doubleValue(),
					result.getGeometry().getLocation().getLng().doubleValue()));
		}
		for (GeocoderResult result : geocoderResponse2.getResults()) {
			reponse.getAdresses2().add(new Adresse(result.getFormattedAddress(), result.getGeometry().getLocation().getLat().doubleValue(),
					result.getGeometry().getLocation().getLng().doubleValue()));
		}
	}
}
