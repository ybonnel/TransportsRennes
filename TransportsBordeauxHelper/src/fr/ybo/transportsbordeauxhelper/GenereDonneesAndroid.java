package fr.ybo.transportsbordeauxhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsbordeauxhelper.modeleandroid.Arret;
import fr.ybo.transportsbordeauxhelper.modeleandroid.ArretRoute;
import fr.ybo.transportsbordeauxhelper.modeleandroid.Direction;
import fr.ybo.transportsbordeauxhelper.modeleandroid.Ligne;
import fr.ybo.transportsbordeauxhelper.modeletcb.ArretLigne;

public class GenereDonneesAndroid {

	private static Map<String, Direction> directions = new HashMap<String, Direction>();
	private static int directionId;

	private static Integer getDirectionId(String directionChaine) {
		if (!directions.containsKey(directionChaine)) {
			Direction direction = new Direction();
			direction.id = directionId++;
			direction.direction = directionChaine;
			directions.put(directionChaine, direction);
		}
		return directions.get(directionChaine).id;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		File androidbase = new File("../TransportsBordeaux/res/raw");
		MoteurCsv moteurCsv = new MoteurCsv(Arrays.asList(Ligne.class, ArretRoute.class, Arret.class, Direction.class));

		GestionnaireLigne gestionnaireLigne = new GestionnaireLigne();
		List<Ligne> lignesAndroid = new ArrayList<Ligne>();
		List<fr.ybo.transportsbordeauxhelper.modeletcb.Ligne> lignesTcb = new ArrayList<fr.ybo.transportsbordeauxhelper.modeletcb.Ligne>(gestionnaireLigne.getLignes());
		Collections.sort(lignesTcb, new Comparator<fr.ybo.transportsbordeauxhelper.modeletcb.Ligne>() {
			@Override
			public int compare(fr.ybo.transportsbordeauxhelper.modeletcb.Ligne o1,
					fr.ybo.transportsbordeauxhelper.modeletcb.Ligne o2) {
				if (o1.type.equals(o2.type)) {
					return o1.identifiant.compareTo(o2.identifiant);
				}
				return o2.type.compareTo(o1.type);
			}
		});
		int ordre = 1;
		for (fr.ybo.transportsbordeauxhelper.modeletcb.Ligne ligne : lignesTcb) {
			Ligne ligneAndroid = new Ligne();
			ligneAndroid.id = ligne.identifiant;
			ligneAndroid.nomCourt = ligne.identifiant.split("_")[1];
			ligneAndroid.nomLong = ligne.nom;
			ligneAndroid.ordre = ordre++;
			lignesAndroid.add(ligneAndroid);
		}

		List<Arret> arretsAndroid = new ArrayList<Arret>();
		for (fr.ybo.transportsbordeauxhelper.modeletcb.Arret arret : gestionnaireLigne.getArrets()) {
			Arret arretAndroid = new Arret();
			arretAndroid.id = arret.identifant;
			arretAndroid.nom = arret.nom;
			arretsAndroid.add(arretAndroid);
		}
		Collections.sort(arretsAndroid, new Comparator<Arret>() {
			@Override
			public int compare(Arret o1, Arret o2) {
				return o1.id.compareTo(o2.id);
			}
		});

		List<ArretRoute> arretsRoutes = new ArrayList<ArretRoute>();
		for (ArretLigne arretLigne : gestionnaireLigne.getArretsLignes()) {
			if (arretLigne.forward) {
				ArretRoute arretRoute = new ArretRoute();
				arretRoute.arretId = arretLigne.arretId;
				arretRoute.ligneId = arretLigne.ligneId;
				arretRoute.macroDirection = 0;
				arretRoute.directionId = getDirectionId(gestionnaireLigne.getLigne(arretLigne.ligneId).directionForward);
				arretsRoutes.add(arretRoute);
			}
			if (arretLigne.backward) {
				ArretRoute arretRoute = new ArretRoute();
				arretRoute.arretId = arretLigne.arretId;
				arretRoute.ligneId = arretLigne.ligneId;
				arretRoute.macroDirection = 1;
				arretRoute.directionId = getDirectionId(gestionnaireLigne.getLigne(arretLigne.ligneId).directionBackward);
				arretsRoutes.add(arretRoute);
			}
		}
		List<Direction> directions = new ArrayList<Direction>(GenereDonneesAndroid.directions.values());
		Collections.sort(directions, new Comparator<Direction>() {
			@Override
			public int compare(Direction o1, Direction o2) {
				return o1.id.compareTo(o2.id);
			}
		});

		moteurCsv.writeFile(new File(androidbase, "lignes.txt"), lignesAndroid, Ligne.class);
		moteurCsv.writeFile(new File(androidbase, "arrets.txt"), arretsAndroid, Arret.class);
		moteurCsv.writeFile(new File(androidbase, "arrets_routes.txt"), arretsRoutes, ArretRoute.class);
		moteurCsv.writeFile(new File(androidbase, "directions.txt"), directions, Direction.class);

	}

}
