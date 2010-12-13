package fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter;

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

}
