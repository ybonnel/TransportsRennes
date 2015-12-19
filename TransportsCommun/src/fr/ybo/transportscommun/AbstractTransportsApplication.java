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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import fr.ybo.transportscommun.util.Theme;

public abstract class AbstractTransportsApplication extends Application {

	protected static DonnesSpecifiques donnesSpecifiques;

	public static DonnesSpecifiques getDonnesSpecifiques() {
		return donnesSpecifiques;
	}

	protected abstract void initDonneesSpecifiques();

	private static boolean debug;

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(final boolean debug) {
		AbstractTransportsApplication.debug = debug;
	}

	private boolean isInPrincipalProcess() {
		final PackageInfo packageinfo;
		try {
			packageinfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SERVICES);
		} catch (final PackageManager.NameNotFoundException ex) {
			return false;
		}
		final String processName = packageinfo.applicationInfo.processName;

		for (final ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE))
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

	public static Iterable<CoupleResourceFichier> getResourcesPrincipale() {
		return RESOURCES_PRINCIPALE;
	}

	@Override
	public void onCreate() {
		initDonneesSpecifiques();
		majTheme(this);
		super.onCreate();
		debug = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				donnesSpecifiques.getApplicationName() + "_debug", false);
		constructDatabase();
		if (!isInPrincipalProcess()) {
			return;
		}
		postCreate();
	}

	public static Theme getTheme(final Context context) {
		return Theme.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(
				donnesSpecifiques.getApplicationName() + "_choixTheme", Theme.BLANC.name()));
	}

	public static int getTextColor(final Context context) {
		return getTheme(context).getTextColor();
	}

	public static void majTheme(final Context context) {
		context.setTheme(getTheme(context).getTheme());
	}

	protected abstract void constructDatabase();

	public abstract Class<? extends AccueilActivity> getAccueilActivity();


	public boolean isThemeNoir() {
		return getTheme(this) == Theme.NOIR;
	}

	public abstract boolean onOptionsItemSelected(MenuItem item, Activity activity, ActivityHelper helper);


	public int getActionBarBackground() {
		return getTheme(this).getActionBarBackground();
	}

	protected abstract void postCreate();

	private static final Collection<String> lignesWithAlerts = new HashSet<String>();

	protected static Collection<String> getLignesWithAlerts() {
		return lignesWithAlerts;
	}

	public static boolean hasAlert(final String ligneNomCourt) {
		return lignesWithAlerts.contains(ligneNomCourt);
	}

	private static LatLngBounds bounds;

	protected static void setBounds(final LatLngBounds bounds) {
		AbstractTransportsApplication.bounds = bounds;
	}

	public static LatLngBounds getBounds() {
		return bounds;
	}

}
