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

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.ubikod.capptain.android.sdk.CapptainAgentUtils;

import fr.ybo.transportscommun.util.GeocodeUtil;

public abstract class AbstractTransportsApplication extends Application {

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

	@Override
	public void onCreate() {
		if (CapptainAgentUtils.isInDedicatedCapptainProcess(this))
			return;
		majTheme(this);
		super.onCreate();
		debug = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getApplicationName() + "_debug", false);
		constructDatabase();
		if (!isInPrincipalProcess()) {
			return;
		}
		geocodeUtil = new GeocodeUtil(this);
	}

	public abstract void majTheme(Context context);

	public abstract String getApplicationName();

	public abstract void constructDatabase();

}
