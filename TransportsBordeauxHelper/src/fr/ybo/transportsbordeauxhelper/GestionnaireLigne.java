package fr.ybo.transportsbordeauxhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.moteurcsv.MoteurCsv.InsertObject;
import fr.ybo.transportsbordeauxhelper.modeletcb.Arret;
import fr.ybo.transportsbordeauxhelper.modeletcb.ArretLigne;
import fr.ybo.transportsbordeauxhelper.modeletcb.Ligne;
import fr.ybo.transportsbordeauxhelper.modeletcb.TcbException;

public class GestionnaireLigne {

	public GestionnaireLigne() {
		File tcbbase = new File("./src/tcbbase");
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(Arret.class);
		classes.add(Ligne.class);
		classes.add(ArretLigne.class);
		moteur = new MoteurCsv(classes);
		try {
			moteur.parseFileAndInsert(new BufferedReader(new FileReader(new File(tcbbase, "lignes.txt"))), Ligne.class,
					new InsertObject<Ligne>() {
						@Override
						public void insertObject(Ligne objet) {
							lignes.put(objet.identifiant, objet);
						}
					});
			moteur.parseFileAndInsert(new BufferedReader(new FileReader(new File(tcbbase, "arrets.txt"))), Arret.class,
					new InsertObject<Arret>() {
						@Override
						public void insertObject(Arret objet) {
							arrets.put(objet.identifant, objet);
						}
					});
			moteur.parseFileAndInsert(new BufferedReader(new FileReader(new File(tcbbase, "arrets_lignes.txt"))),
					ArretLigne.class, new InsertObject<ArretLigne>() {
						@Override
						public void insertObject(ArretLigne objet) {
							if (!arretsLignes.containsKey(objet.ligneId)) {
								arretsLignes.put(objet.ligneId, new HashMap<String, ArretLigne>());
							}
							if (arretsLignes.get(objet.ligneId).containsKey(objet.arretId)) {
								ArretLigne autreArret = arretsLignes.get(objet.ligneId).get(objet.arretId);
								if ((objet.forward && autreArret.forward) || (objet.backward && autreArret.backward)) {
									System.err.println("!!!!!!!!!!!!!!!");
								}
								autreArret.forward |= objet.forward;
								autreArret.backward |= objet.backward;
							} else {
								arretsLignes.get(objet.ligneId).put(objet.arretId, objet);
							}
						}
					});
		} catch (Exception exception) {
			throw new TcbException(exception);
		}
	}

	private MoteurCsv moteur;

	private Map<String, Ligne> lignes = new HashMap<String, Ligne>();
	private Map<String, Arret> arrets = new HashMap<String, Arret>();
	private Map<String, Map<String, ArretLigne>> arretsLignes = new HashMap<String, Map<String, ArretLigne>>();

	public List<ArretLigne> getArretsLignes() {
		List<ArretLigne> retour = new ArrayList<ArretLigne>();
		for (Map<String, ArretLigne> mapOfOneLigne : arretsLignes.values()) {
			for (ArretLigne arretLigne : mapOfOneLigne.values()) {
				retour.add(arretLigne);
			}
		}
		return retour;
	}

}
