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

package fr.ybo.transportsrennes.keolis;

import fr.ybo.transportsrennes.keolis.modele.Answer;
import fr.ybo.transportsrennes.keolis.modele.ParametreUrl;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.keolis.xml.sax.GetAlertsHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetParkRelaiHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetPointDeVenteHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetStationHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.KeolisHandler;
import fr.ybo.transportsrennes.util.LogYbo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Classe d'accés aux API Keolis. Cette classe est une singletton.
 *
 * @author ybonnel
 */
public final class Keolis {

	private static final LogYbo LOG_YBO = new LogYbo(Keolis.class);

	/**
	 * Instance du singletton.
	 */
	@SuppressWarnings({"StaticNonFinalField"})
	private static Keolis instance;

	/**
	 * URL d'accés au API Keolis.
	 */
	private static final String URL = "http://data.keolis-rennes.com/xml/";

	/**
	 * Version.
	 */
	private static final String VERSION = "2.0";

	/**
	 * Clé de l'application.
	 */
	private static final String KEY = "G7JE45LI1RK3W1P";
	/**
	 * Commande pour récupérer les stations.
	 */
	private static final String COMMANDE_STATIONS = "getbikestations";
	/**
	 * Commande pour récupérer les alerts.
	 */
	private static final String COMMANDE_ALERTS = "getlinesalerts";
	/**
	 * Commande pour récupérer les Park relais.
	 */
	private static final String COMMANDE_PARK_RELAI = "getrelayparks";
	/**
	 * Commande pour récupérer les points de vente.
	 */
	private static final String COMMANDE_POS = "getpos";

	/**
	 * Retourne l'instance du singletton.
	 *
	 * @return l'instance du singletton.
	 */
	public static synchronized Keolis getInstance() {
		if (instance == null) {
			instance = new Keolis();
		}
		return instance;
	}

	/**
	 * Constructeur privé.
	 */
	private Keolis() {
		super();
	}

	/**
	 * @param <ObjetKeolis> type d'objet Keolis.
	 * @param url           url.
	 * @param handler       handler.
	 * @return liste d'objets Keolis.
	 * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
	 */
	@SuppressWarnings("unchecked")
	private <ObjetKeolis> List<ObjetKeolis> appelKeolis(final String url, final KeolisHandler<ObjetKeolis> handler) {
		LOG_YBO.debug("Appel d'une API Keolis sur l'url '" + url + "'");
		final long startTime = System.nanoTime() / 1000;
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpUriRequest httpPost = new HttpPost(url);
		final Answer<?> answer;
		try {
			final HttpResponse reponse = httpClient.execute(httpPost);
			final ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			reponse.getEntity().writeTo(ostream);
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(ostream.toByteArray()), handler);
			answer = handler.getAnswer();
		} catch (Exception e) {
			throw new KeolisException("Erreur lors de l'appel à getDistricts", e);
		}
		if (!"0".equals(answer.getStatus().getCode())) {
			throw new KeolisException(answer.getStatus().getMessage());
		}
		final long elapsedTime = System.nanoTime() / 1000 - startTime;
		LOG_YBO.debug("Réponse de Keolis en " + elapsedTime + "µs");
		return (List<ObjetKeolis>) answer.getData();
	}

	/**
	 * Appel les API Keolis pour récupérer les alertes.
	 *
	 * @return les alertes.
	 * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public Iterable<Alert> getAlerts() {
		return appelKeolis(getUrl(COMMANDE_ALERTS), new GetAlertsHandler());
	}

	/**
	 * Appel aux API Keolis pour récupérer les stations.
	 *
	 * @param url url à appeler.
	 * @return la liste des stations.
	 * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
	 */
	private List<Station> getStation(final String url) {
		return appelKeolis(url, new GetStationHandler());
	}

	/**
	 * Appel aux API Keolis pour récupérer une station à partir de son number.
	 *
	 * @param number numéro de la station.
	 * @return la station.
	 */
	Station getStationByNumber(final String number) {
		final ParametreUrl[] params = {new ParametreUrl("station", "number"), new ParametreUrl("value", number)};
		final List<Station> stations = getStation(getUrl(COMMANDE_STATIONS, params));
		if (stations.isEmpty()) {
			return null;
		}
		return stations.get(0);
	}

	/**
	 * Appel aux API Keolis pour récupérer les stations à partir de leurs numéros.
	 *
	 * @param numbers numéros des stations.
	 * @return la station.
	 */
	public Collection<Station> getStationByNumbers(final Collection<String> numbers) {
		final Collection<Station> stations = new ArrayList<Station>(5);
		if (numbers.size() <= 2) {
			for (final String number : numbers) {
				stations.add(getStationByNumber(number));
			}
		} else {
			for (final Station station : getStations()) {
				if (numbers.contains(station.number)) {
					stations.add(station);
				}
			}
		}
		return stations;
	}

	/**
	 * Appel aux API Keolis pour récupérer les stations.
	 *
	 * @return la listes des stations.
	 * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Station> getStations() {
		return getStation(getUrl(COMMANDE_STATIONS));
	}

	/**
	 * @return les parks relais.
	 * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<ParkRelai> getParkRelais() throws KeolisException {
		return appelKeolis(getUrl(COMMANDE_PARK_RELAI), new GetParkRelaiHandler());
	}

	/**
	 * @return les points de ventes.
	 * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<PointDeVente> getPointDeVente() throws KeolisException {
		return appelKeolis(getUrl(COMMANDE_POS), new GetPointDeVenteHandler());
	}

	/**
	 * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
	 * commande à exécuter.
	 *
	 * @param commande commande à exécuter.
	 * @return l'url.
	 */
	private String getUrl(final String commande) {
		final StringBuilder stringBuilder = new StringBuilder(URL);
		stringBuilder.append("?version=").append(VERSION);
		stringBuilder.append("&key=").append(KEY);
		stringBuilder.append("&cmd=").append(commande);
		return stringBuilder.toString();
	}

	/**
	 * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
	 * commande à exécuter et d'un paramètre.
	 *
	 * @param commande commande à exécuter.
	 * @param params   liste de paramètres de l'url.
	 * @return l'url.
	 */
	private String getUrl(final String commande, final ParametreUrl[] params) {
		final StringBuilder stringBuilder = new StringBuilder(getUrl(commande));
		for (final ParametreUrl param : params) {

			try {
				stringBuilder.append("&param[").append(param.getName()).append("]=").append(URLEncoder.encode(param.getValue(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				throw new KeolisException("Erreur lors de la construction de l'URL", e);
			}
		}
		return stringBuilder.toString();
	}

}
