package fr.ybo.transportsrennes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.ybo.opentripplanner.client.Constantes;

public class GsonUtil {
	
	private static Gson gson = null;
	
	public static synchronized Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().setDateFormat(Constantes.DATE_FORMAT).create();
		}
		return gson;
	}

}
