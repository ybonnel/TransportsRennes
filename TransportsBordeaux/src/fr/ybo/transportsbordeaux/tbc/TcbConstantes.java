package fr.ybo.transportsbordeaux.tbc;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TcbConstantes {

	private static final String TCB_HORAIRES = "http://www.infotbc.com/timetable/{ligne_id}/stoppoint/{stop_id}/{direction}/{date}";
	
	private static final SimpleDateFormat SDF_DATE_HORAIRE = new SimpleDateFormat("yyyy/MM/dd");

	public static final String URL_ALERTES = "http://www.mobilinfotbc.com/traffic-info";

	public static final String URL_MOBILE_TBC = "http://www.mobilinfotbc.com";

	public static final String URL_INFOS_TBC = "http://www.infotbc.com";

	public static String getUrlHoraire(String ligneId, String stopId, boolean forward, Date date) {
		return TCB_HORAIRES.replaceAll("\\{ligne_id\\}", ligneId.split("_")[1]).replaceAll("\\{stop_id\\}", stopId)
				.replaceAll("\\{direction\\}", forward ? "forward" : "backward").replaceAll("\\{date\\}", SDF_DATE_HORAIRE.format(date));
	}
}
