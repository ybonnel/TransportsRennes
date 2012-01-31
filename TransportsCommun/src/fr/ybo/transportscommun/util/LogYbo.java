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
package fr.ybo.transportscommun.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import fr.ybo.transportscommun.AbstractTransportsApplication;

public class LogYbo {

    private static final String PREFIX_TAG = "YBO";
    private final String tag;

    public LogYbo(Class<?> clazz) {
        tag = clazz.getSimpleName();
    }

    public void debug(String message) {
		if (AbstractTransportsApplication.isDebug()) {
            Log.d(PREFIX_TAG, tag + " : " + message);
        }
    }

    public void erreur(String message, Throwable throwable) {
        Log.e(PREFIX_TAG, tag + " : " + message, throwable);
    }

    public void erreur(String message) {
        Log.e(PREFIX_TAG, tag + " : " + message);
    }

    public void warn(String message) {
        Log.w(PREFIX_TAG, tag + " : " + message);
    }

    private Map<String, Long> mapStartTimes;

    private Map<String, Long> getMapStartTimes() {
        if (mapStartTimes == null) {
            mapStartTimes = new HashMap<String, Long>(5);
        }
        return mapStartTimes;
    }

    public void startChrono(String message) {
		if (AbstractTransportsApplication.isDebug()) {
            getMapStartTimes().put(message, System.nanoTime());
        }
    }

    public void stopChrono(String message) {
		if (AbstractTransportsApplication.isDebug()) {
            long elapsedTime = (System.nanoTime() - getMapStartTimes().remove(message)) / 1000;
            Log.d(PREFIX_TAG, tag + " : "
                    + new StringBuilder(message).append('\t').append(elapsedTime).append("\tus").toString());
        }
    }


}
