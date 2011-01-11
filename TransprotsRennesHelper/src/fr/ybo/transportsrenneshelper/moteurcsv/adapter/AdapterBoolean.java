package fr.ybo.transportsrenneshelper.moteurcsv.adapter;

public class AdapterBoolean implements AdapterCsv<Boolean> {

	public Boolean parse(final String chaine) {
		return Integer.parseInt(chaine) == 1;
	}

	public String toString(Boolean aBoolean) {
		return aBoolean ? "1" : "0";
	}
}
