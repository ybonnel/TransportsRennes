package fr.ybo.transportsbordeauxhelper;

import java.util.ArrayList;
import java.util.List;

import fr.ybo.transportsbordeauxhelper.modeletcb.ArretLigne;
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
		GestionnaireLigne gestionnaireLigne = new GestionnaireLigne();
		List<ArretLigne> arretsLignes = gestionnaireLigne.getArretsLignes();
		List<Horaire> horaires = new ArrayList<Horaire>();
		for (ArretLigne arretLigne : arretsLignes) {
			
		}
		
	}

}
