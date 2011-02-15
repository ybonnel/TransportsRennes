package fr.ybo.transportsrennes.twitter;

import fr.ybo.transportsrennes.keolis.KeolisException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class GetTwitters {

	@SuppressWarnings({"StaticNonFinalField"})
	private static GetTwitters instance;

	private GetTwitters() {
	}

	public static synchronized GetTwitters getInstance() {
		if (instance == null) {
			instance = new GetTwitters();
		}
		return instance;
	}

	public Collection<MessageTwitter> getMessages() {
		try {
			GetTwittersHandler handler = new GetTwittersHandler();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			HttpURLConnection connection = (HttpURLConnection) new URL("http://transports-rennes.appspot.com/twitterstarbusmetro").openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			parser.parse(connection.getInputStream(), handler);
			return handler.getMessages();
		} catch (Exception e) {
			throw new KeolisException("Erreur lors de l'interrogation de twitter", e);
		}
	}
}
