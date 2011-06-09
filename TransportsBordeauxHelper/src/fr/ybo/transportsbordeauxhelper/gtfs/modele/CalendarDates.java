package fr.ybo.transportsbordeauxhelper.gtfs.modele;

import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("calendar_dates.txt")
public class CalendarDates {
	@BaliseCsv(value = "service_id", ordre = 1)
	public String serviceId;
	@BaliseCsv(value = "date", ordre = 2)
	public String date;
	@BaliseCsv(value = "exception_type", adapter = AdapterInteger.class, ordre = 3)
	public Integer exceptionType;

}
