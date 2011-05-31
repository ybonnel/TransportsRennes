package fr.ybo.transportsrennes.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Version {

	private final static String URL_VERSION = "http://transports-rennes.appspot.com/version.txt";

	private static String versionMarket = null;

	public static String getVersionMarket() {
		try {
			URL urlVersion = new URL(URL_VERSION);
			URLConnection connection = urlVersion.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			versionMarket = bufReader.readLine();
			bufReader.close();
		} catch (Exception ignore) {
		}
		return versionMarket;
	}

	private static String version = null;

	public static String getVersionCourante(Application application) {
		if (version == null) {
			PackageManager manager = application.getPackageManager();
			try {
				PackageInfo info = manager.getPackageInfo(application.getPackageName(), 0);
				version = info.versionName;
			} catch (PackageManager.NameNotFoundException exception) {
				throw new TransportsRennesException(exception);
			}
		}
		return version;
	}

}
