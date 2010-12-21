package fr.ybo.transportsrennes.keolis;

import fr.ybo.transportsrennes.keolis.modele.Answer;
import fr.ybo.transportsrennes.keolis.modele.District;
import fr.ybo.transportsrennes.keolis.modele.ParametreUrl;
import fr.ybo.transportsrennes.keolis.modele.bus.*;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.keolis.xml.sax.*;
import fr.ybo.transportsrennes.util.LogYbo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
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
	private static Keolis instance = null;

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
	 * Commande pour récupérer les districts.
	 */
	private static final String COMMANDE_DISTRICS = "getbikedistricts";
	/**
	 * Commande pour récupérer les stations.
	 */
	private static final String COMMANDE_STATIONS = "getbikestations";
	/**
	 * Commande pour récupérer les alerts.
	 */
	private static final String COMMANDE_ALERTS = "getlinesalerts";
	/**
	 * Commande pour récupérer les lignes.
	 */
	private static final String COMMANDE_LINES = "getlines";
	/**
	 * Commande pour récupérer les équipements.
	 */
	private static final String COMMANDE_EQUIPEMENTS = "getequipments";
	/**
	 * Commande pour récupérer les status des équipements.
	 */
	private static final String COMMANDE_EQUIPEMENTS_STATUS = "getequipmentsstatus";
	/**
	 * Commande pour récupérer les stations de métro.
	 */
	private static final String COMMANDE_METRO_STATION = "getmetrostations";
	/**
	 * Commande pour récupérer les status des stations de métros.
	 */
	private static final String COMMANDE_METRO_STATUS = "getmetrostationsstatus";
	/**
	 * Commande pour récupérer les Park relais.
	 */
	private static final String COMMANDE_PARK_RELAI = "getrelayparks";
	/**
	 * Commande pour récupérer les points de vente.
	 */
	private static final String COMMANDE_POS = "getpos";
	/**
	 * Commande pour récupérer les villes.
	 */
	private static final String COMMANDE_VILLE = "getcities";
	/**
	 * Commande pour récupérer les districts d'un ville.
	 */
	private static final String COMMANDE_VILLE_DISTRICT = "getcitydistricts";

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
	}

	/**
	 * @param <ObjetKeolis> type d'objet Keolis.
	 * @param url           url.
	 * @param handler       handler.
	 * @return liste d'objets Keolis.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	@SuppressWarnings("unchecked")
	private <ObjetKeolis> List<ObjetKeolis> appelKeolis(final String url, final KeolisHandler<ObjetKeolis> handler)
			throws ErreurKeolis {
		LOG_YBO.debug("Appel d'une API Keolis sur l'url '" + url + "'");
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(url);
		Answer<?> answer;
		try {
			final HttpResponse reponse = httpClient.execute(httpPost);
			final ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			reponse.getEntity().writeTo(ostream);
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(ostream.toByteArray()), handler);
			answer = handler.getAnswer();
		} catch (final Exception e) {
			throw new ErreurKeolis("Erreur lors de l'appel à getDistricts", e);
		}
		if (!"0".equals(answer.getStatus().getCode())) {
			throw new ErreurKeolis(answer.getStatus().getMessage());
		}
		return (List<ObjetKeolis>) answer.getData();
	}

	/**
	 * Appel les API Keolis pour récupérer les alertes.
	 *
	 * @return les alertes.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Alert> getAlerts() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_ALERTS), new GetAlertsHandler());
	}

	/**
	 * Appel les API Keolis pour récupérer les districts.
	 *
	 * @return la liste des districts.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<District> getDistricts() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_DISTRICS), new GetDistrictHandler());
	}

	/**
	 * @param ville la ville.
	 * @return les districts d'une ville.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<District> getDistrictVilles(final String ville) throws ErreurKeolis {
		final ParametreUrl[] params = {new ParametreUrl("city", ville)};
		return appelKeolis(getUrl(COMMANDE_VILLE_DISTRICT, params), new GetDistrictHandler());
	}

	/**
	 * @return les équipements.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Equipement> getEquipments() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_EQUIPEMENTS), new GetEquipmentsHandler());
	}

	/**
	 * @return les status des équipements.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Status> getEquipmentsStatus() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_EQUIPEMENTS_STATUS), new GetStatusHandler());
	}

	/**
	 * @return les lignes.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Line> getLines() throws ErreurKeolis {
		final GetLinesHandler handler = new GetLinesHandler();
		final List<Line> lines = appelKeolis(getUrl(COMMANDE_LINES), handler);
		try {
			for (final Line line : lines) {
				line.genereUrl(handler.getBaseUrl());
			}
		} catch (final MalformedURLException malformedURLException) {
			throw new ErreurKeolis("Erreur lors de la construction de l'URL d'accés aux picto", malformedURLException);
		}
		return lines;
	}

	/**
	 * @return les stations de métros.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<MetroStation> getMetroStations() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_METRO_STATION), new GetMetroStationHandler());
	}

	/**
	 * @return les status des métros.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Status> getMetroStatus() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_METRO_STATUS), new GetStatusHandler());
	}

	/**
	 * @return les parks relais.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<ParkRelai> getParkRelais() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_PARK_RELAI), new GetParkRelaiHandler());
	}

	/**
	 * @return les points de ventes.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<PointDeVente> getPointDeVente() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_POS), new GetPointDeVenteHandler());
	}

	/**
	 * Appel aux API Keolis pour récupérer les stations.
	 *
	 * @param url url à appeler.
	 * @return la liste des stations.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	private List<Station> getStation(final String url) throws ErreurKeolis {
		return appelKeolis(url, new GetStationHandler());
	}

	/**
	 * Appel aux API Keolis pour récupérer les stations associées à un
	 * districts.
	 *
	 * @param district le districs.
	 * @return la liste des stations.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Station> getStationByDistrict(final District district) throws ErreurKeolis {
		final ParametreUrl[] params = {new ParametreUrl("request", "district"), new ParametreUrl("value", district.getName())};
		return getStation(getUrl(COMMANDE_STATIONS, params));
	}

	/**
	 * Appel aux API Keolis pour récupérer les stations.
	 *
	 * @return la listes des stations.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Station> getStations() throws ErreurKeolis {
		return getStation(getUrl(COMMANDE_STATIONS));
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

			stringBuilder.append("&param[").append(param.getName()).append("]=").append(URLEncoder.encode(param.getValue()));
		}
		return stringBuilder.toString();
	}

	/**
	 * @return les villes.
	 * @throws ErreurKeolis en cas d'erreur lors de l'appel aux API Keolis.
	 */
	public List<Ville> getVilles() throws ErreurKeolis {
		return appelKeolis(getUrl(COMMANDE_VILLE), new GetVilleHandler());
	}

}
