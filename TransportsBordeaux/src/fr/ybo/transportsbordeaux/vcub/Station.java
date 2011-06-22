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
package fr.ybo.transportsbordeaux.vcub;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.tbc.TcbException;
import fr.ybo.transportsbordeaux.util.ObjetWithDistance;

@SuppressWarnings("serial")
public class Station extends ObjetWithDistance implements Serializable {
	public int id;
	public String name;
	public String address;
	public double longitude;
	public double latitude;
	public int availableBikes;
	public int freeSlots;
	public boolean isOpen;
	public boolean isPayment;

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	private static final String URL_PLAN = "http://www.vcub.fr/stations/plan";

	private static final Pattern PATTERN_MARKERS = Pattern.compile(".*\"markers\": \\[(.*?)\\].*");
	private static final Pattern PATTERN_VCUB = Pattern
			.compile("'latitude': '([^']*)', 'longitude': '([^']*)'[^#]*#(\\d*) - ([^<]*)</div>[^<]*"
					+ "<div class='gmap-adresse'>([^<]*)</div>"
					+ "(<div class='gmap-velos'>.*?<strong>(\\d+)</strong>.*?<strong>(\\d+)</strong>([^<]*</td>"
					+ "<td><acronym title='Carte Bancaire'>CB</acronym></td>)?)?");

	public static List<Station> recupererStations() throws TbcErreurReseaux {
		try {
			StringBuilder result = new StringBuilder();
			URL url = new URL(URL_PLAN);
			String line;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			if (200 != conn.getResponseCode()) {
				conn.disconnect();
				throw new TbcErreurReseaux("Erreur lors du contact de la page des vcub, code = " + conn.getResponseCode());
			}
			// Get response
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
			String toParse = result.toString();
			Matcher m = PATTERN_MARKERS.matcher(toParse);
			m.find();
			toParse = m.group(1);
			toParse = toParse.replaceAll("\\\\x3c", "<");
			toParse = toParse.replaceAll("\\\\x3e", ">");
			toParse = toParse.replaceAll("(\\\\')|(\\\\)?\"", "'");

			m = PATTERN_VCUB.matcher(toParse);
			List<Station> stations = new ArrayList<Station>();
			while (m.find()) {
				Station s = new Station();
				s.id = Integer.parseInt(m.group(3));
				s.name = m.group(4);
				s.address = m.group(5).toLowerCase();
				s.latitude = Double.parseDouble(m.group(1));
				s.longitude = Double.parseDouble(m.group(2));
				if (m.group(7) != null) {
					s.availableBikes = Integer.parseInt(m.group(7));
					s.freeSlots = Integer.parseInt(m.group(8));
					s.isPayment = (m.group(9) != null);
					s.isOpen = true;
				} else {
					s.isOpen = false;
				}
				stations.add(s);
			}
			return stations;
		} catch (TbcErreurReseaux erreurReseau) {
			throw erreurReseau;
		} catch (SocketException exceptionReseaux) {
			throw new TbcErreurReseaux(exceptionReseaux);
		} catch (SocketTimeoutException exceptionReseaux) {
			throw new TbcErreurReseaux(exceptionReseaux);
		} catch (UnknownHostException exceptionReseaux) {
			throw new TbcErreurReseaux(exceptionReseaux);
		} catch (Exception e) {
			throw new TcbException(e);
		}
	}
}
