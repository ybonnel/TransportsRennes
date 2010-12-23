package fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter;

public class AdapterInteger implements AdapterCsv<Integer> {

	public Integer parse(final String chaine) {
		return Integer.valueOf(chaine);
	}

	public String toString(Integer integer) {
		return integer.toString();
	}
}
