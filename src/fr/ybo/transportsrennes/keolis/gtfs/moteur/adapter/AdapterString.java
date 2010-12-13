package fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter;

public class AdapterString implements AdapterCsv<String> {
	public String parse(final String chaine) {
		return chaine;
	}
}
