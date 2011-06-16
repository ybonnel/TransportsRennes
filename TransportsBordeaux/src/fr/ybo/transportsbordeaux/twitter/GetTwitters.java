package fr.ybo.transportsbordeaux.twitter;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.tbc.TcbException;

public class GetTwitters {

	private static GetTwitters instance;

	private GetTwitters() {
	}

	public static synchronized GetTwitters getInstance() {
		if (instance == null) {
			instance = new GetTwitters();
		}
		return instance;
	}

	public Collection<MessageTwitter> getMessages() throws TbcErreurReseaux {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(
					"http://transports-rennes.appspot.com/twittertbc").openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
			connection.connect();
			Gson gson = new GsonBuilder().create();
			Type listType = new TypeToken<List<MessageTwitter>>() {
			}.getType();
			return gson.fromJson(new InputStreamReader(connection.getInputStream()), listType);
		} catch (SocketTimeoutException timeoutException) {
			throw new TbcErreurReseaux(timeoutException);
		} catch (Exception e) {
			throw new TcbException("Erreur lors de l'interrogation de twitter", e);
		}
	}
}
