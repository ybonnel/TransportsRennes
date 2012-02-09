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
package fr.ybo.transportsrennes.twitter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import fr.ybo.transportscommun.util.ErreurReseau;

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

    public Collection<MessageTwitter> getMessages() throws ErreurReseau {
        try {
			URL myUrl = new URL("http://support-twitter.herokuapp.com/starbusmetro");
			URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
			connection.addRequestProperty("Accept", "application/json");
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<List<MessageTwitter>>() {
            }.getType();
            return gson.fromJson(new InputStreamReader(connection.getInputStream()), listType);
        } catch (SocketTimeoutException timeoutException) {
            throw new ErreurReseau(timeoutException);
        } catch (UnknownHostException erreurReseau) {
            throw new ErreurReseau(erreurReseau);
        } catch (IOException exception) {
            throw new ErreurReseau(exception);
		} catch (JsonParseException exception) {
			throw new ErreurReseau(exception);
        }
    }
}
