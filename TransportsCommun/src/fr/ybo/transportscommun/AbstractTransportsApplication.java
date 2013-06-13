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
package fr.ybo.transportscommun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.code.geocoder.model.LatLngBounds;

import fr.ybo.database.DataBaseHelper;
import fr.ybo.transportscommun.activity.AccueilActivity;
import fr.ybo.transportscommun.activity.commun.ActivityHelper;
import fr.ybo.transportscommun.donnees.manager.gtfs.CoupleResourceFichier;
import fr.ybo.transportscommun.util.GeocodeUtil;
import fr.ybo.transportscommun.util.Theme;

public abstract class AbstractTransportsApplication extends Application {

	protected static DonnesSpecifiques donnesSpecifiques;

	public static DonnesSpecifiques getDonnesSpecifiques() {
		return donnesSpecifiques;
	}

	protected abstract void initDonneesSpecifiques();

	private static boolean debug = false;

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		AbstractTransportsApplication.debug = debug;
	}

	private static GeocodeUtil geocodeUtil;

	public static GeocodeUtil getGeocodeUtil() {
		return geocodeUtil;
	}

	private boolean isInPrincipalProcess() {
		PackageInfo packageinfo;
		try {
			packageinfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SERVICES);
		} catch (android.content.pm.PackageManager.NameNotFoundException ex) {
			return false;
		}
		String processName = packageinfo.applicationInfo.processName;

		for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE))
				.getRunningAppProcesses()) {
			if (runningAppProcessInfo.pid == android.os.Process.myPid()) {
				return runningAppProcessInfo.processName.equals(processName);
			}
		}
		return false;
	}

	protected static DataBaseHelper databaseHelper;

	public static DataBaseHelper getDataBaseHelper() {
		return databaseHelper;
	}

	protected static List<CoupleResourceFichier> RESOURCES_PRINCIPALE;

	public static List<CoupleResourceFichier> getResourcesPrincipale() {
		return RESOURCES_PRINCIPALE;
	}

	@Override
	public void onCreate() {
		initDonneesSpecifiques();
		majTheme(this);
		super.onCreate();
		debug = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				getDonnesSpecifiques().getApplicationName() + "_debug", false);
		constructDatabase();
		if (!isInPrincipalProcess()) {
			return;
		}
		geocodeUtil = new GeocodeUtil(this);
		postCreate();
	}

	public static Theme getTheme(Context context) {
		return Theme.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(
				getDonnesSpecifiques().getApplicationName() + "_choixTheme", Theme.BLANC.name()));
	}

	public static int getTextColor(Context context) {
		return getTheme(context).getTextColor();
	}

	public static void majTheme(Context context) {
		context.setTheme(getTheme(context).getTheme());
	}

	public abstract void constructDatabase();

	public abstract Class<? extends AccueilActivity> getAccueilActivity();


	public boolean isThemeNoir() {
		return getTheme(this) == Theme.NOIR;
	}

	public abstract boolean onOptionsItemSelected(MenuItem item, Activity activity, ActivityHelper helper);


	public int getActionBarBackground() {
		return getTheme(this).getActionBarBackground();
	}

	public abstract void postCreate();

	private static Set<String> lignesWithAlerts = new HashSet<String>();

	public static Set<String> getLignesWithAlerts() {
		return lignesWithAlerts;
	}

	public static boolean hasAlert(String ligneNomCourt) {
		return lignesWithAlerts.contains(ligneNomCourt);
	}

	private static LatLngBounds bounds;

	public static void setBounds(LatLngBounds bounds) {
		AbstractTransportsApplication.bounds = bounds;
	}

	public static LatLngBounds getBounds() {
		return bounds;
	}

}
