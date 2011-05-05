package fr.ybo.transportsbordeaux.vcub;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ybo.transportsbordeaux.tbc.TcbException;

public class Station {
	public int id;
	public String name;
	public String address;
	public double longitude;
	public double latitude;
	public int availableBikes;
	public int freeSlots;
	public boolean isOpen;
	public boolean isPayment;

	private static final String URL_PLAN = "http://www.vcub.fr/stations/plan";

	private static final Pattern PATTERN_MARKERS = Pattern.compile(".*\"markers\": \\[(.*?)\\].*");
	private static final Pattern PATTERN_VCUB = Pattern
			.compile("'latitude': '([^']*)', 'longitude': '([^']*)'[^#]*#(\\d*) - ([^<]*)</div>[^<]*"
					+ "<div class='gmap-adresse'>([^<]*)</div>"
					+ "(<div class='gmap-velos'>.*?<strong>(\\d+)</strong>.*?<strong>(\\d+)</strong>([^<]*</td>"
					+ "<td><acronym title='Carte Bancaire'>CB</acronym></td>)?)?");

	public static List<Station> recupererStations() {
		try {
			StringBuilder result = new StringBuilder();
			URL url = new URL(URL_PLAN);
			String line;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.41 Safari/534.7");
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			if (200 != conn.getResponseCode()) {
				conn.disconnect();
				throw new TcbException("Erreur lors du contact de la page des vcub, code = " + conn.getResponseCode());
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
		} catch (Exception e) {
			throw new TcbException(e);
		}
	}
}
