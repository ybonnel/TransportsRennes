package fr.ybo.transportsbordeauxhelper.modeletcb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsbordeauxhelper.modeletcb.sax.GetHorairesHandler;

@FichierCsv("horaires.txt")
public class Horaire {

	public String arretId;
	public String ligneId;
	public Integer horaire;
	public Boolean forward;
	public Boolean backward;
	public String url;
	
	private static List<Horaire> getHoraires(String date, ArretLigne arretLigne, boolean forward) {
		try {
			// Récupération sur la page internet du table d'horaire.
			HttpURLConnection connection = (HttpURLConnection) new URL(
					TcbConstantes.getUrlHoraire(arretLigne.ligneId,
							arretLigne.arretId, forward, date)).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			boolean tableEnCours = false;
			try {
				String ligne = bufReader.readLine();
				while (ligne != null) {
					if (ligne.contains("navitia-timetable-detail")) {
						tableEnCours = true;
					}
					if (tableEnCours) {
						stringBuilder.append(ligne);
						if (ligne.contains("<\\/table>")) {
							break;
						}
					}
					ligne = bufReader.readLine();
				}
			} finally {
				bufReader.close();
			}
			
			// Parsing SAX du tableau d'horaires.
			GetHorairesHandler handler = new GetHorairesHandler(arretLigne, forward);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(stringBuilder.toString().getBytes()), handler);
			return handler.getHoraires();
		} catch (Exception exception) {
			throw new TcbException(exception);
		}
	}

	public static List<Horaire> getHoraires(String date, ArretLigne arretLigne) {
		List<Horaire> horaires = new ArrayList<Horaire>();
		if (arretLigne.forward) {
			horaires.addAll(getHoraires(date, arretLigne, true));
		}
		if (arretLigne.backward) {
			horaires.addAll(getHoraires(date, arretLigne, false));
		}
		return new ArrayList<Horaire>();
	}

}
