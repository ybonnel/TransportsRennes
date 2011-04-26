package fr.ybo.transportsbordeauxhelper.modeletcb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.ybo.transportsbordeauxhelper.modeletcb.sax.GetDirectionsHandler;
import fr.ybo.transportsbordeauxhelper.modeletcb.sax.GetLignesHandler;

public class Ligne {
	
	public String identifiant;
	public String type;
	public String nom;
	public String directionForward;
	public String directionBackward;
	
	protected void remplirDirections() {

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(TcbConstantes.TCB_DIRECTION.replaceAll("\\{stop_id\\}", identifiant)).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String ligne = bufReader.readLine();
			while (ligne != null) {
				stringBuilder.append(ligne.replaceAll("id=\"\"", ""));
				ligne = bufReader.readLine();
			}
			
			GetDirectionsHandler handler = new GetDirectionsHandler();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(stringBuilder.toString().getBytes()), handler);
			directionBackward = handler.getDirectionBackward();
			directionForward = handler.getDirectionForward();
			
		} catch (Exception exception) {
			throw new TcbException(exception);
		}
	}
	
	
	public static List<Ligne> getLignes() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(TcbConstantes.TCB_URL).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			try {
				String chaine = bufReader.readLine();
				while (chaine != null) {
					if (chaine.contains(TcbConstantes.BLOC_LIGNES_SELECT_NAME)) {
						chaine = chaine.replaceAll("id=\"edit-navitia-line\" ", "");
						GetLignesHandler handler = new GetLignesHandler();
						SAXParserFactory factory = SAXParserFactory.newInstance();
						SAXParser parser = factory.newSAXParser();
						parser.parse(new ByteArrayInputStream(chaine.getBytes()), handler);
						for (Ligne ligne : handler.getLignes()) {
							System.out.println("Ajout des directions de " + ligne.nom);
							ligne.remplirDirections();
						}
						return handler.getLignes();
					}
					chaine = bufReader.readLine();
				}
				return null;
			} finally {
				bufReader.close();
			}
		} catch (Exception exception) {
			throw new TcbException(exception);
		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ligne [identifiant=" + this.identifiant + ", type=" + this.type
				+ ", nom=" + this.nom + ", directionForward="
				+ this.directionForward + ", directionBackward="
				+ this.directionBackward + "]";
	}	
	
	
}
