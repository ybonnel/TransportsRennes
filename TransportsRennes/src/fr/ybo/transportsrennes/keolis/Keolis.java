/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportsrennes.keolis;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.keolis.modele.ParametreUrl;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.keolis.modele.bus.Departure;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.keolis.xml.sax.GetAlertsHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetDeparturesHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetDeparturesMetroHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetParkRelaiHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetPointDeVenteHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.GetStationHandler;
import fr.ybo.transportsrennes.keolis.xml.sax.KeolisHandler;
import fr.ybo.transportsrennes.util.HttpUtils;

/**
 * Classe d'accés aux API Keolis. Cette classe est une singletton.
 *
 * @author ybonnel
 */
public final class Keolis {

    private static final LogYbo LOG_YBO = new LogYbo(Keolis.class);

    /**
     * Instance du singletton.
     */
    private static Keolis instance;


    /**
     * Retourne l'instance du singletton.
     *
     * @return l'instance du singletton.
     */
    public static synchronized Keolis getInstance() {
        if (instance == null) {
            instance = new Keolis();
        }
        return instance;
    }

    /**
     * Constructeur privé.
     */
    private Keolis() {
    }

    /**
     * @param <ObjetKeolis> type d'objet Keolis.
     * @param handler       handler.
     * @return liste d'objets Keolis.
     * @throws ErreurReseau    en cas d'erreur réseau.
     * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
     */
    @SuppressWarnings("unchecked")
    private <ObjetKeolis> List<ObjetKeolis> appelKeolis(KeolisHandler<ObjetKeolis> handler) throws ErreurReseau {
        return appelKeolis(handler, null);
    }

    private <ObjetKeolis> List<ObjetKeolis> appelKeolis(KeolisHandler<ObjetKeolis> handler, String query)
            throws ErreurReseau {
        LOG_YBO.warn("Appel d'une API Keolis sur le dataset '" + handler.getDatasetid() + "' with query : " + query);
        long startTime = System.nanoTime() / 1000;
        HttpClient httpClient = HttpUtils.getHttpClient();

        String urlParams = "";
        if (query != null) {
            try {
                urlParams = "&where=" + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException ignore) {
            }
        }

        String request = "http://data.explore.star.fr/api/v2/catalog/datasets/" +
                handler.getDatasetid() +
                "/records?rows=-1&pretty=false&timezone=CET&apikey=e519de4f9d490b95947ad21716127633e2b8445dd7cd18644e446958"
                + urlParams;

        LOG_YBO.warn(request);
        HttpUriRequest httpGet = new HttpGet(
                request
        );
        List<ObjetKeolis> result = new ArrayList<ObjetKeolis>();
        try {
            HttpResponse reponse = httpClient.execute(httpGet);
            InputStream inputStream = reponse.getEntity().getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            LOG_YBO.warn(sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray records = jsonObject.getJSONArray("records");

            for (int index = 0; index < records.length(); index++) {
                JSONObject record = records.getJSONObject(index).getJSONObject("record").getJSONObject("fields");
                result.add(handler.fromJson(record));
            }

        } catch (IOException socketException) {
            throw new ErreurReseau(socketException);
        } catch (JSONException jsonException) {
            throw new ErreurReseau(jsonException);
        }
        long elapsedTime = System.nanoTime() / 1000 - startTime;
        LOG_YBO.debug("Réponse de Keolis en " + elapsedTime + "µs");
        return result;
    }


    /**
     * Appel les API Keolis pour récupérer les alertes.
     *
     * @return les alertes.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public Iterable<Alert> getAlerts() throws ErreurReseau {
        return appelKeolis(new GetAlertsHandler());
    }

    /**
     * Appel aux API Keolis pour récupérer les stations.
     *
     * @return la liste des stations.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public List<Station> getStations() throws ErreurReseau {
        return appelKeolis(new GetStationHandler());
    }

    /**
     * Appel aux API Keolis pour récupérer une station à partir de son number.
     *
     * @param number numéro de la station.
     * @return la station.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    private Station getStationByNumber(int number) throws ErreurReseau {
        List<Station> stations = appelKeolis(new GetStationHandler(), "idstation:" + number);
        if (stations.isEmpty()) {
            return null;
        }
        return stations.get(0);
    }

    /**
     * Appel aux API Keolis pour récupérer les stations à partir de leurs
     * numéros.
     *
     * @param numbers numéros des stations.
     * @return la station.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public Collection<Station> getStationByNumbers(Collection<Integer> numbers) throws ErreurReseau {
        List<Station> stations = new ArrayList<Station>();
        for (Station station : getStations()) {
            if (numbers.contains(station.number)) {
                stations.add(station);
            }
        }
        return stations;
    }

    /**
     * @return les parks relais.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public List<ParkRelai> getParkRelais() throws ErreurReseau {
        return appelKeolis(new GetParkRelaiHandler());
    }

    /**
     * @return les points de ventes.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public List<PointDeVente> getPointDeVente() throws ErreurReseau {
        return appelKeolis(new GetPointDeVenteHandler());
    }

    public List<Departure> getDepartues(ArretFavori favori) throws ErreurReseau {
        if (favori.nomCourt.equals("a")) {
            return getDeparturesForMetro(favori);
        }
        return appelKeolis(new GetDeparturesHandler(),
                "idarret:\"" + favori.arretId + "\" AND idligne:\"" + favori.ligneId + "\" AND sens:" + favori.macroDirection);
    }

    public List<Departure> getDeparturesForMetro(ArretFavori favori) throws ErreurReseau {
        return appelKeolis(new GetDeparturesMetroHandler(), "idarret:\"" + favori.arretId + "\" AND sens:" + favori.macroDirection);
    }


}
