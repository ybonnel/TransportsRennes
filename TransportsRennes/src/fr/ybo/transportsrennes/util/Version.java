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
package fr.ybo.transportsrennes.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Version {

	private final static String URL_VERSION = "http://transports-rennes.appspot.com/version.txt";

	private static String versionMarket = null;

	public static String getVersionMarket() {
		try {
			URL urlVersion = new URL(URL_VERSION);
			URLConnection connection = urlVersion.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			versionMarket = bufReader.readLine();
			bufReader.close();
		} catch (Exception ignore) {
		}
		return versionMarket;
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
