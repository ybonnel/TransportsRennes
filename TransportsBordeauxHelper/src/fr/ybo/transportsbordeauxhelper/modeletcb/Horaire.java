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

import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsbordeauxhelper.modeletcb.sax.GetHorairesHandler;

@FichierCsv("horaires.txt")
public class Horaire {

	@BaliseCsv(value = "arret_id", ordre = 1)
	public String arretId;
	@BaliseCsv(value = "ligne_id", ordre = 2)
	public String ligneId;
	@BaliseCsv(value = "horaire", ordre = 3, adapter = AdapterInteger.class)
	public Integer horaire;
	@BaliseCsv(value = "forward", ordre = 4, adapter = AdapterBoolean.class)
	public Boolean forward;
	@BaliseCsv(value = "backward", ordre = 5, adapter = AdapterBoolean.class)
	public Boolean backward;
	@BaliseCsv(value = "url", ordre = 6)
	public String url;
	@BaliseCsv(value = "calendrier_id", ordre = 7, adapter = AdapterInteger.class)
	public Integer calendrierId;

	private static List<Horaire> getHoraires(String date, ArretLigne arretLigne, boolean forward,
			Calendrier calendrier, List<String> urlsEnErreur) {
		try {
			// Récupération sur la page internet du table d'horaire.
			boolean hasErreur = true;
			StringBuilder stringBuilder = new StringBuilder();
			String url = TcbConstantes.getUrlHoraire(arretLigne.ligneId, arretLigne.arretId, forward, date);
			while (hasErreur) {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
					connection.setRequestMethod("GET");
					connection.setDoOutput(true);
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
					hasErreur = false;
				} catch (Exception exception) {
					exception.printStackTrace();
					Thread.sleep(10000);
				}
			}

			// Parsing SAX du tableau d'horaires.
			GetHorairesHandler handler = new GetHorairesHandler(arretLigne, forward, calendrier.id);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(stringBuilder.toString().getBytes()), handler);
			if (handler.isErreur()) {
				urlsEnErreur.add(url);
			}
			return handler.getHoraires();
		} catch (Exception exception) {
			throw new TcbException(exception);
		}
	}

	public static List<Horaire> getHoraires(String date, ArretLigne arretLigne, Calendrier calendrier,
			List<String> urlsEnErreur) {
		List<Horaire> horaires = new ArrayList<Horaire>();
		if (arretLigne.forward) {
			horaires.addAll(getHoraires(date, arretLigne, true, calendrier, urlsEnErreur));
		}
		if (arretLigne.backward) {
			horaires.addAll(getHoraires(date, arretLigne, false, calendrier, urlsEnErreur));
		}
		return horaires;
	}

}
