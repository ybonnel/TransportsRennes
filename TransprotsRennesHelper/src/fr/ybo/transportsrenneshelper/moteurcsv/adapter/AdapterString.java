package fr.ybo.transportsrenneshelper.moteurcsv.adapter;

public class AdapterString implements AdapterCsv<String> {
	public String parse(final String chaine) {
		return chaine;
	}

	public String toString(String s) {
		return s;
	}
}
