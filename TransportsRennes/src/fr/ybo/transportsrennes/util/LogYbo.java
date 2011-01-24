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

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class LogYbo {

	private static final String PREFIX_TAG = "YBO_";
	private final String tag;
	private static final boolean isDebug = false;

	public LogYbo(final Class<?> clazz) {
		tag = PREFIX_TAG + clazz.getSimpleName();
	}

	public void debug(final String message) {
		if (isDebug) {
			Log.d(tag, message);
		}
	}

	public void erreur(final String message, final Throwable throwable) {
		Log.e(tag, message, throwable);
	}

	public void warn(String message) {
		Log.w(tag, message);
	}

	private Map<String, Long> mapStartTimes;

	private Map<String, Long> getMapStartTimes() {
		if (mapStartTimes == null) {
			mapStartTimes = new HashMap<String, Long>();
		}
		return mapStartTimes;
	}

	public void startChrono(String message) {
		if (isDebug) {
			getMapStartTimes().put(message, System.nanoTime());
		}
	}

	public void stopChrono(String message) {
		if (isDebug) {
			long elapsedTime = (System.nanoTime() - getMapStartTimes().remove(message)) / 1000;
			Log.d(tag, new StringBuilder(message).append('\t').append(elapsedTime).append("\tus").toString());
		}
	}


}
