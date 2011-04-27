package fr.ybo.transportsbordeauxhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsbordeauxhelper.modeletcb.ArretLigne;
import fr.ybo.transportsbordeauxhelper.modeletcb.Calendrier;
import fr.ybo.transportsbordeauxhelper.modeletcb.Horaire;

public class ChargeHoraires {

	private final static String LUNDI = "2011/04/18";
	private final static String MARDI = "2011/04/19";
	private final static String MERCREDI = "2011/04/20";
	private final static String JEUDI = "2011/04/21";
	private final static String VENDREDI = "2011/04/22";
	private final static String SAMEDI = "2011/04/23";
	private final static String DIMANCHE = "2011/04/24";

	public static void main(String[] args) {
		File fileHoraires = new File("./src/tcbbase/horaires.txt");
		if (fileHoraires.exists()) {
			fileHoraires.delete();
		}
		GestionnaireLigne gestionnaireLigne = new GestionnaireLigne();
		List<ArretLigne> arretsLignes = gestionnaireLigne.getArretsLignes();
		List<Horaire> horaires = new ArrayList<Horaire>();
		int nbArretLigne = arretsLignes.size();
		int count = 0;
		long temps = 0;
		long startTime;
		for (ArretLigne arretLigne : arretsLignes) {
			System.out.println("Avancement : " + count + " / " + nbArretLigne);
			startTime = System.currentTimeMillis();
			horaires.addAll(Horaire.getHoraires(LUNDI, arretLigne, Calendrier.LUNDI));
			horaires.addAll(Horaire.getHoraires(MARDI, arretLigne, Calendrier.MARDI));
			horaires.addAll(Horaire.getHoraires(MERCREDI, arretLigne, Calendrier.MERCREDI));
			horaires.addAll(Horaire.getHoraires(JEUDI, arretLigne, Calendrier.JEUDI));
			horaires.addAll(Horaire.getHoraires(VENDREDI, arretLigne, Calendrier.VENDREDI));
			horaires.addAll(Horaire.getHoraires(SAMEDI, arretLigne, Calendrier.SAMEDI));
			horaires.addAll(Horaire.getHoraires(DIMANCHE, arretLigne, Calendrier.DIMANCHE));
			temps += (System.currentTimeMillis() - startTime);
			count++;
			long tempsMoyen = temps / count;
			System.out.println("Temps restant estimé : " + (tempsMoyen * (nbArretLigne - count)));
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		MoteurCsv moteur = new MoteurCsv((List)Collections.singletonList(Horaire.class));
		moteur.writeFile(fileHoraires, horaires, Horaire.class);
		
	}

}
