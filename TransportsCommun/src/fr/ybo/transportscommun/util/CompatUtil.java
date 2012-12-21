package fr.ybo.transportscommun.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

public class CompatUtil {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void invalidateOptionsMenu(Activity activity) {
		activity.invalidateOptionsMenu();
	}

}
