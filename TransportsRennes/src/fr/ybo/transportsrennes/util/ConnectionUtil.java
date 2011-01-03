package fr.ybo.transportsrennes.util;


import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionUtil {

	private static final LogYbo LOG_YBO = new LogYbo(ConnectionUtil.class);

	public static boolean isConnected(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean connected =
				(conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected());
		if (!connected) {
			LOG_YBO.debug("Pas de connection active.");
		}
		return connected;
	}
}
