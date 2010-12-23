package fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter;

public class AdapterDouble implements AdapterCsv<Double> {

	public Double parse(final String chaine) {
		return Double.valueOf(chaine);
	}

	public String toString(Double aDouble) {
		return aDouble.toString();
	}
}
