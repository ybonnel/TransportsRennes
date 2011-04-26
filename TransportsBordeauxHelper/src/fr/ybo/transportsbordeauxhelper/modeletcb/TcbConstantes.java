package fr.ybo.transportsbordeauxhelper.modeletcb;

public class TcbConstantes {

	public static final String TCB_URL = "http://www.infotbc.com/?nomobile=true";

	public static final String BLOC_LIGNES_SELECT_NAME = "navitia_line";
	public static final String TCB_DIRECTION = "http://www.infotbc.com/ahah/stoppoint?navitia_line={ligne_id}";
	public static final String TCB_ARRET_FORWARD = "http://www.infotbc.com/ahah/stoppoint?navitia_line={ligne_id}&navitia_direction=forward";
	public static final String TCB_ARRET_BACKWARD = "http://www.infotbc.com/ahah/stoppoint?navitia_line={ligne_id}&navitia_direction=backward";

}
