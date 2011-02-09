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

package fr.ybo.twitter.starbusmetro.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import fr.ybo.twitter.starbusmetro.modele.MessageTwitter;

public class GetTwitters {

	private static GetTwitters instance = null;

	private GetTwitters() {
	}

	synchronized public static GetTwitters getInstance() {
		if (instance == null) {
			instance = new GetTwitters();
		}
		return instance;
	}

	public ArrayList<MessageTwitter> getMessages() throws IOException,
			ParserConfigurationException, SAXException {
		GetTwittersHandler handler = new GetTwittersHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		HttpURLConnection connection = (HttpURLConnection) new URL(
				"http://transports-rennes.appspot.com/twitterstarbusmetro")
				.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.connect();
		/*
		 * BufferedReader bufReader = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream(), "utf-8")); String
		 * ligne; while ((ligne = bufReader.readLine()) != null) {
		 * System.out.println(ligne); }
		 */
		parser.parse(connection.getInputStream(), handler);
		return handler.getMessages();
	}

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException {
		System.out.println(getInstance().getMessages());
	}
}
