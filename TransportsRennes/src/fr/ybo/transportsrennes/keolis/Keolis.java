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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.xml.sax.SAXException;

import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.keolis.modele.Answer;
import fr.ybo.transportsrennes.keolis.modele.ParametreUrl;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.keolis.modele.bus.Departure;
import fr.ybo.transportsrennes.keolis.modele.bus.DepartureMetro;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;
import fr.ybo.transportsrennes.keolis.modele.bus.ResultDeparture;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.keolis.xml.sax.GetAlertsHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetDeparturesHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetDeparturesMetroHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetParkRelaiHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetPointDeVenteHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetStationHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.KeolisHandler;
import fr.ybo.transportsrennes.util.HttpUtils;

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

	private static final String COMMANDE_DEPARTURE = "getbusnextdepartures";

    private static final String COMMANDE_DEPARTURE_METRO = "getmetronextdepartures";

	private static final String VERSION_DEPARTURE = "2.2";

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
     * @throws ErreurReseau    en cas d'erreur réseau.
     * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
     */
    private static <ObjetKeolis> List<ObjetKeolis> appelKeolis(final String url, final KeolisHandler<ObjetKeolis> handler)
            throws ErreurReseau {
        LOG_YBO.debug("Appel d'une API Keolis sur l'url '" + url + '\'');
        final long startTime = System.nanoTime() / 1000;
        final HttpClient httpClient = HttpUtils.getHttpClient();
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
        } catch (final IOException socketException) {
            throw new ErreurReseau(socketException);
        } catch (final SAXException saxException) {
            throw new ErreurReseau(saxException);
        } catch (final ParserConfigurationException exception) {
            throw new KeolisException("Erreur lors de l'appel à l'API keolis", exception);
        }
        if (answer == null || answer.getStatus() == null || !"0".equals(answer.getStatus().getCode())) {
            throw new ErreurReseau();
        }
        final long elapsedTime = System.nanoTime() / 1000 - startTime;
        LOG_YBO.debug("Réponse de Keolis en " + elapsedTime + "µs");
        return (List<ObjetKeolis>) answer.getData();
    }

    /**
     * Appel les API Keolis pour récupérer les alertes.
     *
     * @return les alertes.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public static Iterable<Alert> getAlerts() throws ErreurReseau {
        return appelKeolis(getUrl(COMMANDE_ALERTS), new GetAlertsHandler());
    }

    /**
     * Appel aux API Keolis pour récupérer les stations.
     *
     * @param url url à appeler.
     * @return la liste des stations.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    private static List<Station> getStation(final String url) throws ErreurReseau {
        return appelKeolis(url, new GetStationHandler());
    }

    /**
     * Appel aux API Keolis pour récupérer une station à partir de son number.
     *
     * @param number numéro de la station.
     * @return la station.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    private static Station getStationByNumber(final String number) throws ErreurReseau {
        final ParametreUrl[] params = {new ParametreUrl("station", "number"), new ParametreUrl("value", number)};
        final List<Station> stations = getStation(getUrl(params));
        if (stations.isEmpty()) {
            return null;
        }
        return stations.get(0);
    }

    /**
     * Appel aux API Keolis pour récupérer les stations à partir de leurs
     * numéros.
     *
     * @param numbers numéros des stations.
     * @return la station.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public static Collection<Station> getStationByNumbers(final Collection<String> numbers) throws ErreurReseau {
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
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public static List<Station> getStations() throws ErreurReseau {
        return getStation(getUrl(COMMANDE_STATIONS));
    }

    /**
     * @return les parks relais.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public static List<ParkRelai> getParkRelais() throws ErreurReseau {
        return appelKeolis(getUrl(COMMANDE_PARK_RELAI), new GetParkRelaiHandler());
    }

    /**
     * @return les points de ventes.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public static List<PointDeVente> getPointDeVente() throws ErreurReseau {
        return appelKeolis(getUrl(COMMANDE_POS), new GetPointDeVenteHandler());
    }

	public static ResultDeparture getDepartues(final ArretFavori favori) throws ErreurReseau {
        if ("a".equals(favori.nomCourt)) {
            return getDeparturesForMetro(favori);
        }
		final ParametreUrl[] params =
				{ new ParametreUrl("mode", "stopline"), new ParametreUrl("route][", favori.ligneId),
						new ParametreUrl("direction][", Integer.toString(favori.macroDirection)),
						new ParametreUrl("stop][", favori.arretId) };

		final GetDeparturesHandler handler = new GetDeparturesHandler();
		final List<Departure> departures = appelKeolis(getUrl(COMMANDE_DEPARTURE, params, VERSION_DEPARTURE), handler);
		return new ResultDeparture(departures, handler.getDateApi());
	}

    private static ResultDeparture getDeparturesForMetro(final ArretFavori favori) throws ErreurReseau {
        final String arretId  = favori.arretId.substring(0, favori.arretId.length() - 1);
        final ParametreUrl[] params =
                { new ParametreUrl("mode", "station"), new ParametreUrl("station", arretId) };

        final GetDeparturesMetroHandler handler = new GetDeparturesMetroHandler(favori.macroDirection + 1);
        final List<DepartureMetro> departuresMetro = appelKeolis(getUrl(COMMANDE_DEPARTURE_METRO, params, VERSION_DEPARTURE), handler);

        final List<Departure> departures = new ArrayList<Departure>();

        if (!departuresMetro.isEmpty()) {
            final DepartureMetro departureMetro = departuresMetro.get(0);
            if (departureMetro.getTime1() != null) {
                final Departure departure = new Departure();
                departure.setAccurate(true);
                departure.setHeadSign(favori.direction);
                departure.setTime(departureMetro.getTime1());
                departures.add(departure);
            }
            if (departureMetro.getTime2() != null) {
                final Departure departure = new Departure();
                departure.setAccurate(true);
                departure.setHeadSign(favori.direction);
                departure.setTime(departureMetro.getTime2());
                departures.add(departure);
            }
        }
        return new ResultDeparture(departures, Calendar.getInstance());
    }

    /**
     * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
     * commande à exécuter.
     *
     * @param commande commande à exécuter.
     * @return l'url.
     */
    private static String getUrl(final String commande) {
		return getUrl(commande, VERSION);
	}

	/**
	 * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
	 * commande à exécuter.
	 * 
	 * @param commande
	 *            commande à exécuter.
	 * @return l'url.
	 */
	private static String getUrl(final String commande, final String version) {
        return URL + "?version=" + version + "&key=" + KEY + "&cmd=" + commande;
    }

    /**
     * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
     * commande à exécuter et d'un paramètre.
     *
     * @param params   liste de paramètres de l'url.
     * @return l'url.
     */
    private static String getUrl(final ParametreUrl[] params) {
		return getUrl(COMMANDE_STATIONS, params, VERSION);
	}

	/**
	 * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
	 * commande à exécuter et d'un paramètre.
	 * 
	 * @param commande
	 *            commande à exécuter.
	 * @param params
	 *            liste de paramètres de l'url.
	 * @return l'url.
	 */
	private static String getUrl(final String commande, final ParametreUrl[] params, final String version) {
		final StringBuilder stringBuilder = new StringBuilder(getUrl(commande, version));
        for (final ParametreUrl param : params) {

            try {
                stringBuilder.append("&param[").append(param.getName()).append("]=").append(URLEncoder.encode(param.getValue(), "utf-8"));
            } catch (final UnsupportedEncodingException e) {
                throw new KeolisException("Erreur lors de la construction de l'URL", e);
            }
        }
        return stringBuilder.toString();
    }

}
