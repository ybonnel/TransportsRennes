package fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterDate implements AdapterCsv<Date> {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public Date parse(final String chaine) {
		try {
			return sdf.parse(chaine);
		} catch (final ParseException e) {
			return null;
		}
	}

	public String toString(Date date) {
		return sdf.format(date);
	}

}
