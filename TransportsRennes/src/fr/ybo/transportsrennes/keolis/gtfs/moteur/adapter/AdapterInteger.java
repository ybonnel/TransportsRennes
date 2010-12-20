package fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter;

public class AdapterInteger implements AdapterCsv<Integer> {

	public Integer parse(final String chaine) {
		return Integer.valueOf(chaine);
	}
}
