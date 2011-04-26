package fr.ybo.transportsbordeauxhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsbordeauxhelper.modeletcb.Arret;
import fr.ybo.transportsbordeauxhelper.modeletcb.ArretLigne;
import fr.ybo.transportsbordeauxhelper.modeletcb.Ligne;

public class ChargeLignes {

	public static void main(String[] args) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(Ligne.class);
		classes.add(Arret.class);
		classes.add(ArretLigne.class);
		File tcbbase = new File("./src/tcbbase");
		for (File file : tcbbase.listFiles()) {
			file.delete();
		}
		List<Ligne> lignes = Ligne.getLignes();
		Map<String, Arret> arrets = new HashMap<String, Arret>();
		List<ArretLigne> arretsLignes = new ArrayList<ArretLigne>();
		Map<String, Map<String, ArretLigne>> mapArretsLignes = new HashMap<String, Map<String, ArretLigne>>();

		for (Ligne ligne : lignes) {
			for (Arret arretBackward : ligne.arretsBackward) {
				if (arrets.containsKey(arretBackward.identifant)) {
					if (!arrets.get(arretBackward.identifant).nom.equals(arretBackward.nom)) {
						System.err.println("Plusieurs arrêts avec le même identifiant!");
					}
				} else {
					arrets.put(arretBackward.identifant, arretBackward);
				}
				ArretLigne arretLigne = new ArretLigne();
				arretLigne.arretId = arretBackward.identifant;
				arretLigne.ligneId = ligne.identifiant;
				arretLigne.backward = true;
				arretLigne.forward = false;
				arretsLignes.add(arretLigne);
				if (!mapArretsLignes.containsKey(ligne.identifiant)) {
					mapArretsLignes.put(ligne.identifiant, new HashMap<String, ArretLigne>());
				}
				mapArretsLignes.get(ligne.identifiant).put(arretBackward.identifant, arretLigne);
			}
			for (Arret arretForward : ligne.arretsForward) {
				if (arrets.containsKey(arretForward.identifant)) {
					if (!arrets.get(arretForward.identifant).nom.equals(arretForward.nom)) {
						System.err.println("Plusieurs arrêts avec le même identifiant!");
					}
				} else {
					arrets.put(arretForward.identifant, arretForward);
				}
				ArretLigne arretLigne = new ArretLigne();
				arretLigne.arretId = arretForward.identifant;
				arretLigne.ligneId = ligne.identifiant;
				arretLigne.backward = false;
				arretLigne.forward = true;
				if (mapArretsLignes.containsKey(ligne.identifiant)
						&& mapArretsLignes.get(ligne.identifiant).containsKey(arretForward.identifant)) {
					mapArretsLignes.get(ligne.identifiant).get(arretForward.identifant).forward = true;
				} else {
					arretsLignes.add(arretLigne);
				}
			}
		}
		List<Arret> arretsList = new ArrayList<Arret>();
		arretsList.addAll(arrets.values());
		Collections.sort(lignes, new Comparator<Ligne>() {
			@Override
			public int compare(Ligne o1, Ligne o2) {
				return o1.identifiant.compareTo(o2.identifiant);
			}
		});
		Collections.sort(arretsList, new Comparator<Arret>() {
			@Override
			public int compare(Arret o1, Arret o2) {
				return o1.identifant.compareTo(o2.identifant);
			}
		});
		Collections.sort(arretsLignes, new Comparator<ArretLigne>() {
			@Override
			public int compare(ArretLigne o1, ArretLigne o2) {
				if (o1.ligneId.equals(o2.ligneId)) {
					if (o1.arretId.equals(o2.arretId)) {
						return o1.forward.compareTo(o2.forward);
					} else {
						return o1.arretId.compareTo(o2.arretId);
					}
				} else {
					return o1.ligneId.compareTo(o2.ligneId);
				}
			}
		});
		MoteurCsv moteurCsv = new MoteurCsv(classes);
		moteurCsv.writeFile(new File(tcbbase, "arrets.txt"), arretsList, Arret.class);
		moteurCsv.writeFile(new File(tcbbase, "lignes.txt"), lignes, Ligne.class);
		moteurCsv.writeFile(new File(tcbbase, "arrets_lignes.txt"), arretsLignes, ArretLigne.class);

	}

}
