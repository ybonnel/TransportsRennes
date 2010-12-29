package fr.ybo.transportsrennes.util;

public class Formatteur {

	public static String formatterChaine(String chaine) {
		StringBuilder nomLongFormateBuilder = new StringBuilder();
		for (String champ : chaine.replaceAll("/", "-").split(" ")) {
			for (String champ2 : champ.split("\\(")) {
				if (champ2.length() > 0) {
					nomLongFormateBuilder.append(champ2.substring(0, 1).toUpperCase());
					nomLongFormateBuilder.append(champ2.substring(1, champ2.length()).toLowerCase());
				}
				nomLongFormateBuilder.append('(');
			}
			// on enleve le dernier tiret.
			nomLongFormateBuilder.deleteCharAt(nomLongFormateBuilder.length() - 1);
			nomLongFormateBuilder.append(' ');
		}
		// on enleve le dernier espace.
		nomLongFormateBuilder.deleteCharAt(nomLongFormateBuilder.length() - 1);
		String nomLongFormate = nomLongFormateBuilder.toString().replaceAll("\\|", "");
		while (nomLongFormate.contains("  ")) {
			nomLongFormate = nomLongFormate.replaceAll("  ", " ");
		}
		while (nomLongFormate.startsWith(" ")) {
			nomLongFormate = nomLongFormate.substring(1);
		}
		return nomLongFormate;
	}
}
