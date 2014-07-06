package fr.ybo.transportscommun.util;

import android.app.Activity;
import android.os.Build;

public class CompatUtil {

	public static void invalidateOptionsMenu(Activity activity) {
		activity.invalidateOptionsMenu();
	}

}
