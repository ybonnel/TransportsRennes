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
package fr.ybo.transportsbordeaux.tbcapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.xml.sax.SAXException;

import fr.ybo.transportsbordeaux.database.modele.Parking;
import fr.ybo.transportsbordeaux.tbcapi.modele.Station;
import fr.ybo.transportsbordeaux.tbcapi.sax.GetParkingHandler;
import fr.ybo.transportsbordeaux.tbcapi.sax.GetStationHandler;
import fr.ybo.transportsbordeaux.tbcapi.sax.KeolisHandler;
import fr.ybo.transportsbordeaux.util.HttpUtils;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.LogYbo;

/**
 * Classe d'accés aux API Keolis. Cette classe est une singletton.
 *
 * @author ybonnel
 */
public final class Keolis {

    private static final LogYbo LOG_YBO = new LogYbo(Keolis.class);

    /**
     * URL d'accés au API Keolis.
     */
    private static final String URL = "http://data.lacub.fr/wfs?";

    /**
     * Clé de l'application.
     */
    private static final String KEY = "RAPJ1LVSXN";
    /**
     * Commande pour récupérer les stations.
     */
    private static final String COUCHE_PARKINGS = "CI_PARK_P";

	/**
	 * Commande pour les vélos
	 */
	private static final String COUCHE_VELO = "CI_VCUB_P";

    /**
     * Constructeur privé.
     */
    private Keolis() {
    }

    /**
     * @param <ObjetKeolis> type d'objet Keolis.
     * @param url           url.
     * @param handler       handler.
     * @return liste d'objets Keolis.
     * @throws ErreurReseau en cas d'erreur réseau.
     */
    private static <ObjetKeolis> List<ObjetKeolis> appelKeolis(final String url, final KeolisHandler<ObjetKeolis> handler)
			throws ErreurReseau {
        LOG_YBO.debug("Appel d'une API Keolis sur l'url '" + url + '\'');
        final long startTime = System.nanoTime() / 1000;
        final HttpClient httpClient = HttpUtils.getHttpClient();
        final HttpUriRequest httpPost = new HttpPost(url);
        final List<ObjetKeolis> answer;
        try {
            final HttpResponse reponse = httpClient.execute(httpPost);
            final ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			if (reponse == null || reponse.getEntity() == null) {
				throw new ErreurReseau("Erreur lors de la récupération de la réponse http");
			}
            reponse.getEntity().writeTo(ostream);
            final String contenu = new String(ostream.toByteArray(), "ISO-8859-1");
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser parser = factory.newSAXParser();
            parser.parse(new ByteArrayInputStream(contenu.getBytes("UTF-8")), handler);
            answer = handler.getObjets();
        } catch (final IOException socketException) {
			throw new ErreurReseau(socketException);
        } catch (final SAXException saxException) {
			throw new ErreurReseau(saxException);
        } catch (final ParserConfigurationException exception) {
            throw new KeolisException(exception);
        }
        if (answer == null) {
			throw new ErreurReseau("Erreur dans la réponse données par Keolis.");
        }
        final long elapsedTime = System.nanoTime() / 1000 - startTime;
        LOG_YBO.debug("Réponse de Keolis en " + elapsedTime + "µs");
        return answer;
    }

    /**
     * @return les parks relais.
     * @throws KeolisException  en cas d'erreur lors de l'appel aux API Keolis.
     * @throws ErreurReseau erreur réseaux.
     */
	public static List<Parking> getParkings() throws KeolisException, ErreurReseau {
        return appelKeolis(getUrl(COUCHE_PARKINGS), new GetParkingHandler());
    }

	public static List<Station> getStationsVcub() throws KeolisException, ErreurReseau {
		return appelKeolis(getUrl(COUCHE_VELO), new GetStationHandler());
	}

    /**
     * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
     * commande à exécuter.
     *
     * @param couche couche à requêter.
     * @return l'url.
     */
    private static String getUrl(final String couche) {
        return URL + "key=" + KEY + "&request=getfeature&service=wfs&version=1.1.0" + "&typename=" + couche + "&srsname=epsg:4326";
    }

	// http://data.lacub.fr/wfs?key=RAPJ1LVSXN&request=getfeature&service=wfs&version=1.1.0&typename=CI_VCUB_P&srsname=epsg:4326

}
