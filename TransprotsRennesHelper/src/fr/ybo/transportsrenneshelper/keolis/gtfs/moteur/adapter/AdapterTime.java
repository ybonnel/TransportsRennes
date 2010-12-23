package fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter;

public class AdapterTime implements AdapterCsv<Integer> {

	public Integer parse(final String chaine) {
		if (chaine == null) {
			return null;
		}
		final String[] champs = chaine.split(":");
		if (champs.length != 3) {
			return null;
		}
		return Integer.parseInt(champs[0]) * 60 + Integer.parseInt(champs[1]);
	}

	public String toString(Integer integer) {
		if (integer == null) {
			return null;
		}
		StringBuilder retour = new StringBuilder();
		int heures;
		int minutes;
		heures = integer / 60;
		minutes = integer - heures * 60;
		if (heures < 10) {
			retour.append('0');
		}
		retour.append(heures);
		retour.append(':');
		if (minutes < 10) {
			retour.append('0');
		}
		retour.append(minutes);
		retour.append(":00");
		return retour.toString();
	}

}
