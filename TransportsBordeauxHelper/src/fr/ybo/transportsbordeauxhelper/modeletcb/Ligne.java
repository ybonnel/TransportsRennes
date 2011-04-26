package fr.ybo.transportsbordeauxhelper.modeletcb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.ybo.transportsbordeauxhelper.modeletcb.sax.GetLignesHandler;

public class Ligne {
	
	public String identifiant;
	public String type;
	public String nom;
	
	
	
	public static List<Ligne> getLignes() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(TcbConstantes.TCB_URL).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			try {
				String ligne = bufReader.readLine();
				while (ligne != null) {
					System.out.println(ligne);
					if (ligne.contains(TcbConstantes.BLOC_LIGNES_SELECT_NAME)) {
						ligne = ligne.replaceAll("id=\"edit-navitia-line\" ", "");
						GetLignesHandler handler = new GetLignesHandler();
						SAXParserFactory factory = SAXParserFactory.newInstance();
						SAXParser parser = factory.newSAXParser();
						parser.parse(new ByteArrayInputStream(ligne.getBytes()), handler);
						return handler.getLignes();
					}
					ligne = bufReader.readLine();
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
				+ ", nom=" + this.nom + "]";
	}

	
	
}
