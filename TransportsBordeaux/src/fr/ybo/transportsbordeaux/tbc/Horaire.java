package fr.ybo.transportsbordeaux.tbc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.util.LogYbo;

@FichierCsv("horaires.txt")
public class Horaire {

	@BaliseCsv(value = "arret_id", ordre = 1)
	public String arretId;
	@BaliseCsv(value = "ligne_id", ordre = 2)
	public String ligneId;
	@BaliseCsv(value = "horaire", ordre = 3, adapter = AdapterInteger.class)
	public Integer horaire;
	@BaliseCsv(value = "url", ordre = 6)
	public String url;

	private static final LogYbo LOG_YBO = new LogYbo(Horaire.class);

	public static List<Horaire> getHoraires(Date date, ArretFavori favori) {
		try {
			// Récupération sur la page internet du table d'horaire.
			StringBuilder stringBuilder = new StringBuilder();
			String url = TcbConstantes.getUrlHoraire(favori.ligneId, favori.arretId,
					favori.macroDirection.intValue() == 0, date);
			LOG_YBO.debug(url);
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
					if (ligne.contains("navitia-timetable-detail")) {
						tableEnCours = true;
					}
					if (tableEnCours) {
						stringBuilder.append(ligne);
						if (ligne.contains("</table>")) {
							break;
						}
					}
					ligne = bufReader.readLine();
				}
			} finally {
				bufReader.close();
			}

			// Parsing SAX du tableau d'horaires.
			GetHorairesHandler handler = new GetHorairesHandler(favori);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(stringBuilder.toString().getBytes()), handler);
			return handler.getHoraires();
		} catch (Exception exception) {
			throw new TcbException(exception);
		}
	}

}
