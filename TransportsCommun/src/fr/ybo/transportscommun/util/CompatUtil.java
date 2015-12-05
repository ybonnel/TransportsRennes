package fr.ybo.transportscommun.util;

import android.app.Activity;

public final class CompatUtil {

	private CompatUtil() {
	}

	public static void invalidateOptionsMenu(final Activity activity) {
		activity.invalidateOptionsMenu();
	}

}
