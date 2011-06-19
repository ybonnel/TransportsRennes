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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrenneshelper.keolis;

import fr.ybo.transportsrenneshelper.keolis.modele.Answer;
import fr.ybo.transportsrenneshelper.keolis.modele.MetroStation;
import fr.ybo.transportsrenneshelper.keolis.sax.GetMetroStationHandler;
import fr.ybo.transportsrenneshelper.keolis.sax.KeolisHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Classe d'accés aux API Keolis. Cette classe est une singletton.
 *
 * @author ybonnel
 */
public final class Keolis {

	/**
	 * Instance du singletton.
	 */
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
	 * Commande pour récupérer les stations de metro.
	 */
	private static final String COMMANDE_METRO = "getmetrostations";

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
	 */
	@SuppressWarnings("unchecked")
	private <ObjetKeolis> List<ObjetKeolis> appelKeolis(String url, KeolisHandler<ObjetKeolis> handler) {
		Answer<?> answer;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(connection.getInputStream(), handler);
			answer = handler.getAnswer();
		} catch (Exception e) {
			throw new KeolisException("Erreur lors de l'appel à getDistricts", e);
		}
		if (!"0".equals(answer.getStatus().getCode())) {
			throw new KeolisException(answer.getStatus().getMessage());
		}
		return (List<ObjetKeolis>) answer.getData();
	}

	/**
	 * Appel les API Keolis pour récupérer les stations de metros.
	 *
	 * @return les stations de metro.
	 */
	public Iterable<MetroStation> getMetroStation() {
		return appelKeolis(getUrl(COMMANDE_METRO), new GetMetroStationHandler());
	}

	/**
	 * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
	 * commande à exécuter.
	 *
	 * @param commande commande à exécuter.
	 * @return l'url.
	 */
	private String getUrl(String commande) {
		StringBuilder stringBuilder = new StringBuilder(URL);
		stringBuilder.append("?version=").append(VERSION);
		stringBuilder.append("&key=").append(KEY);
		stringBuilder.append("&cmd=").append(commande);
		return stringBuilder.toString();
	}
}
