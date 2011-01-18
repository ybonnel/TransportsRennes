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

package fr.ybo.transportsrennes.util;

import android.util.Log;

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

}
