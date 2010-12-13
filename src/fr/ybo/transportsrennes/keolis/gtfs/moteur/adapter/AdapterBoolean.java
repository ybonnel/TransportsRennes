package fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter;

public class AdapterBoolean implements AdapterCsv<Boolean> {

	public Boolean parse(final String chaine) {
		return Integer.parseInt(chaine) == 1;
	}
}
