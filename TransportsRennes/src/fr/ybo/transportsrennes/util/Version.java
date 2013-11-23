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
package fr.ybo.transportsrennes.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Version {

	public static final String URL_VERSION = "http://transports.ybonnel.fr/version/transports-rennes.version";

    /**
     * Nom de la version disponible sur le market
     *
     * @param packageName Nom du package de l'application
     * @return Nom de la version disponible sur le market
     */
	public static String getMarketVersion() {
        String version = null;
        BufferedReader reader = null;
        try {
			URL marketURL = new URL(URL_VERSION);
            URLConnection connection = marketURL.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			version = reader.readLine();

        } catch (Exception ignore) {
			ignore.printStackTrace();
        } finally {
            closeReader(reader);
        }
        return version;
    }

    /**
     * Ferme la connexion du reader
     *
     * @param reader Reader
     */
    private static void closeReader(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception ignore) {
            }
        }
    }

    private static String version = null;

    public static String getVersionCourante(Context context) {
        if (version == null) {
            PackageManager manager = context.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                version = info.versionName;
            } catch (PackageManager.NameNotFoundException exception) {
                throw new TransportsRennesException(exception);
            }
        }
        return version;
    }

}
