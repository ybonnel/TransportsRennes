/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.util.Constantes;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
public class TransportsRennesApplication extends Application {

	private static DataBaseHelper databaseHelper;

	public static DataBaseHelper getDataBaseHelper() {
		return databaseHelper;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		databaseHelper = new DataBaseHelper(this, ConstantesKeolis.LIST_CLASSES_DATABASE);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		TransportsWidget.verifKiller(getApplicationContext(), appWidgetManager);
		traker = GoogleAnalyticsTracker.getInstance();
		traker.start(Constantes.UA_ACCOUNT, 60, this);
		traker.setCustomVar(1, "androidVersion", android.os.Build.FINGERPRINT);
		traker.setCustomVar(2, "androidModel", android.os.Build.MODEL);
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			traker.setCustomVar(3, "appVersion", info.versionName);
		} catch (PackageManager.NameNotFoundException ignore) {
		}
	}

	private static GoogleAnalyticsTracker traker;

	public static GoogleAnalyticsTracker getTraker() {
		return traker;
	}
}
