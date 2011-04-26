package fr.ybo.transportsbordeauxhelper.modeletcb;

import java.util.ArrayList;
import java.util.List;

import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("horaires.txt")
public class Horaire {

	private String arretId;
	private String ligneId;
	private Integer horaire;
	private Boolean forward;
	private Boolean backward;

	public static List<Horaire> getHoraires(String date, ArretLigne arretLigne) {
		List<Horaire> horaires = new ArrayList<Horaire>();

		return new ArrayList<Horaire>();
	}

}
