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

package fr.ybo.transportsbordeaux.modele;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXParseException;

import fr.ybo.transportsbordeaux.tbc.GetAlertesHandler;
import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.tbc.TcbConstantes;
import fr.ybo.transportsbordeaux.tbc.TcbException;

/**
 * Class représentant une alerte Keolis.
 *
 * @author ybonnel
 *
 */

/**
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class Alert implements Serializable {

	public Alert() {
	}

	public Alert(String title, String ligne, String url) {
		super();
		this.title = title;
		this.ligne = ligne;
		this.url = url;
	}

	/**
	 * title.
	 */
	public String title;
	/**
	 * lines.
	 */
	public String ligne;

	/**
	 * detail.
	 */
	public String url;

	public static List<Alert> getAlertes() throws TbcErreurReseaux {
		// Récupération sur la page internet du table d'horaire.
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder contenuPage = new StringBuilder();
		String url = TcbConstantes.URL_ALERTES;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.connect();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			stringBuilder = new StringBuilder();
			boolean tableEnCours = false;
			try {
				String ligne = bufReader.readLine();
				while (ligne != null) {
					contenuPage.append(ligne);
					if (ligne.contains("<tbody")) {
						tableEnCours = true;
					}
					if (tableEnCours) {
						stringBuilder.append(ligne);
						if (ligne.contains("</tbody>")) {
							break;
						}
					}
					ligne = bufReader.readLine();
				}
			} finally {
				bufReader.close();
			}

			if (contenuPage.length() == 0) {
				throw new TbcErreurReseaux();
			}

			// Parsing SAX du tableau d'horaires.
			GetAlertesHandler handler = new GetAlertesHandler();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(stringBuilder.toString().getBytes()), handler);
			return handler.getAlertes();
		} catch (SAXParseException saxParseException) {
			throw new TbcErreurReseaux(saxParseException);
		} catch (TbcErreurReseaux tbcErreurReseaux) {
			throw tbcErreurReseaux;
		} catch (FileNotFoundException erreurReseau) {
			throw new TbcErreurReseaux(erreurReseau);
		} catch (UnknownHostException erreurReseau) {
			throw new TbcErreurReseaux(erreurReseau);
		} catch (Exception exception) {
			throw new TcbException("Erreur lors de la récupération des alertes pour l'url " + url + ", html récupéré : " + contenuPage.toString(), exception);
		}
	}


}
