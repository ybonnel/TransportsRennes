package fr.ybo.transportsbordeaux.util;

public class StringUtils {

	public static String doubleTrim(String string) {
		String retour = string;
		while (retour.charAt(0) == ' ') {
			retour = retour.substring(1);
		}
		while (retour.charAt(retour.length() - 1) == ' ') {
			retour = retour.substring(0, retour.length() - 2);
		}
		return retour;
	}

}
