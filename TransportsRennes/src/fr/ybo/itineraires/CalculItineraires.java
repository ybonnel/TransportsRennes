package fr.ybo.itineraires;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.ybo.itineraires.modele.Adresse;
import fr.ybo.itineraires.modele.EnumCalendrier;
import fr.ybo.itineraires.schema.ItineraireReponse;
import fr.ybo.transportsrennes.util.GsonUtil;
import fr.ybo.transportsrennes.util.LogYbo;
import org.restlet.resource.ClientResource;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class CalculItineraires {

    private static final LogYbo LOG_YBO = new LogYbo(CalculItineraires.class);

    private static final String URL_BASE = "http://transports-rennes.appspot.com/rest/itineraires";
    private static final String KEY = "xxxxx";

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
        ClientResource cr = new ClientResource(getUrl(adresseDepart, adresseArrive, calendar));
        ItineraireResource resource = cr.wrap(ItineraireResource.class);
        return GsonUtil.getInstance().fromJson(resource.calculItineraire(), ItineraireReponse.class);
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
