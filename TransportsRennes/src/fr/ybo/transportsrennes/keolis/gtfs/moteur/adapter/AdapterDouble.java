package fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter;

public class AdapterDouble implements AdapterCsv<Double> {

	public Double parse(final String chaine) {
		return Double.valueOf(chaine);
	}
}
