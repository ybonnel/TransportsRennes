package fr.ybo.transportsrennes.util;

import android.util.Log;

public class LogYbo {

	private static final String PREFIX_TAG = "YBO_";
	private final String tag;

	public LogYbo(final Class<?> clazz) {
		tag = PREFIX_TAG + clazz.getSimpleName();
	}

	public void debug(final String message) {
		Log.d(tag, message);
	}

	public void erreur(final String message, final Throwable throwable) {
		Log.e(tag, message, throwable);
	}

	public void warn(String message) {
		Log.w(tag, message);
	}

}
