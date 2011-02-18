package fr.ybo.itineraires;


import fr.ybo.itineraires.schema.Adresse;
import fr.ybo.itineraires.schema.EnumCalendrier;
import fr.ybo.itineraires.schema.ItineraireReponse;
import fr.ybo.transportsrennes.util.LogYbo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class CalculItineraires {

	private static final LogYbo LOG_YBO = new LogYbo(CalculItineraires.class);

	private static final String URL_BASE = "http://transports-rennes.appspot.com/rest/itineraires";
	private static final String KEY = "YboItineraires01*";

	private static CalculItineraires instance;

	public static synchronized CalculItineraires getInstance() {
		if (instance == null) {
			instance = new CalculItineraires();
		}
		return instance;
	}

	private CalculItineraires() {
	}

	public ItineraireReponse calculItineraires(Adresse adresseDepart, Adresse adresseArrive, Calendar calendar) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpUriRequest httpPost = new HttpPost(getUrl(adresseDepart, adresseArrive, calendar));
		try {
			HttpResponse reponse = httpClient.execute(httpPost);
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			reponse.getEntity().writeTo(ostream);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			ItinerairesHandler handler = new ItinerairesHandler();
			parser.parse(new ByteArrayInputStream(ostream.toByteArray()), handler);
			return handler.getReponse();
		} catch (Exception exception) {
			throw new ItinerairesException(exception);
		}
	}

	private String getUrl(Adresse adresseDepart, Adresse adresseArrive, Calendar calendar) {
		try {
			StringBuilder urlString = new StringBuilder(URL_BASE);
			urlString.append("?key=");
			urlString.append(KEY);
			urlString.append("&adresseDepart=");
			urlString.append(adresseDepart.toUrl());
			urlString.append("&adresseArrivee=");
			urlString.append(adresseArrive.toUrl());
			urlString.append("&heureDepart=");
			int heureDepart = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			urlString.append(heureDepart);
			urlString.append("&calendrier=");
			urlString.append(EnumCalendrier.fromFieldCalendar(calendar.get(Calendar.DAY_OF_WEEK)).getNumCalendrier());
			LOG_YBO.debug(urlString.toString());
			return urlString.toString();
		} catch (UnsupportedEncodingException exception) {
			throw new ItinerairesException(exception);
		}
	}
}
