/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package fr.ybo.opentripplanner.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.ybo.opentripplanner.client.modele.Request;
import fr.ybo.opentripplanner.client.modele.Response;

public class Planner {

	public Response getItineraries(Request request) throws OpenTripPlannerException {
		try {
			URL url = new URL(request.constructUrl(Constantes.URL_PLANER));
			System.out.println(url.toString());
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
					Constantes.ENCODAGE));
			StringBuilder stringBuilder = new StringBuilder();
			String ligne;
			while ((ligne = bufReader.readLine()) != null) {
				stringBuilder.append(ligne);
			}
			bufReader.close();
			System.out.println(stringBuilder.toString());
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
			return gson.fromJson(stringBuilder.toString(), Response.class);
		} catch (Exception exception) {
			throw new OpenTripPlannerException(exception);
		}
    }

}
