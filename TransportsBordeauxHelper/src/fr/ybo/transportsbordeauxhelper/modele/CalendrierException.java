package fr.ybo.transportsbordeauxhelper.modele;

import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("calendrier_exception.txt")
public class CalendrierException {
	@BaliseCsv(value = "calendrier_id", adapter = AdapterInteger.class, ordre = 0)
	public Integer calendrierId;
	@BaliseCsv(value = "date", ordre = 1)
	public String date;
	@BaliseCsv(value = "ajout", adapter = AdapterBoolean.class, ordre = 2)
	public Boolean ajout;

}
