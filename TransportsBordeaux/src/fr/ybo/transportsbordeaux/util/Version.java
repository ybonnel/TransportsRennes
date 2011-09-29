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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsbordeaux.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Version {

	private static final String MARKET_URL = "https://market.android.com/details?id=";
	private static final String VERSION_PATTERN = ".*<dd itemprop=\"softwareVersion\">([^<]*)<.*";

	/**
	 * Nom de la version disponible sur le market
	 * 
	 * @param context
	 *            Context
	 * @return Nom de la version disponible sur le market
	 */
	public static String getMarketVersion(Context context) {
		return getMarketVersion(context.getPackageName());
	}

	/**
	 * Nom de la version disponible sur le market
	 * 
	 * @param packageName
	 *            Nom du package de l'application
	 * @return Nom de la version disponible sur le market
	 */
	public static String getMarketVersion(String packageName) {
		String version = null;
		BufferedReader reader = null;
		try {
			URL marketURL = new URL(MARKET_URL + packageName);
			URLConnection connection = marketURL.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line = reader.readLine();
			while (line != null) {
				if (line.matches(VERSION_PATTERN)) {
					version = line.replaceFirst(VERSION_PATTERN, "$1");
					break;
				}
				line = reader.readLine();
			}
		} catch (Exception ignore) {
		} finally {
			closeReader(reader);
		}
		return version;
	}

	/**
	 * Ferme la connexion du reader
	 * 
	 * @param reader
	 *            Reader
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
			} catch (PackageManager.NameNotFoundException ignore) {
			}
		}
		return version;
	}

}
