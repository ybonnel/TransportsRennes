package fr.ybo.transportsbordeauxhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsbordeauxhelper.modeletcb.ArretLigne;
import fr.ybo.transportsbordeauxhelper.modeletcb.Calendrier;
import fr.ybo.transportsbordeauxhelper.modeletcb.Horaire;

public class ChargeHoraires {

	static class Pair<Gauche, Droite> {
		private Gauche gauche;
		private Droite droite;

		public Pair(Gauche pGauche, Droite pDroite) {
			super();
			this.gauche = pGauche;
			this.droite = pDroite;
		}

		public final Gauche getGauche() {
			return this.gauche;
		}

		public final Droite getDroite() {
			return this.droite;
		}
	}

	public ChargeHoraires(int maxThread) {
		super();
		this.maxThread = maxThread;
	}

	private int maxThread;

	private AtomicInteger compteurThread = new AtomicInteger(0);

	private List<String> urlsEnErreurs = new ArrayList<String>();

	private List<Horaire> horaires = new ArrayList<Horaire>();

	protected synchronized void addUrlsEnErreur(Collection<String> urls) {
		urlsEnErreurs.addAll(urls);
	}

	protected synchronized void addHoraires(Collection<Horaire> horairesToAdd) {
		horaires.addAll(horairesToAdd);
	}
	
	private Boolean lock = new Boolean(true);
	private long temps = 0;
	private long size = 0;
	
	private void addTime(long time) {
		synchronized (lock) {
			temps += time;
			size++;
		}
	}
	
	private long getNouveauTempsAttente(long tempsAttenteActuel) {
		synchronized (lock) {
			if (size > 20) {
				long nouveauTempsAttente = ((temps / size) / maxThread);
				size = 0;
				temps = 0;
				System.err.println("Nouveau temps d'attente : " + nouveauTempsAttente);
				return nouveauTempsAttente;
			} else {
				return tempsAttenteActuel;
			}
		}
	}
	
	

	private class MyThread extends Thread {

		private ArretLigne arretLigne;
		private List<Pair<String, Calendrier>> calendriers;

		public MyThread(ArretLigne arretLigne,
				List<Pair<String, Calendrier>> calendriers) {
			this.arretLigne = arretLigne;
			this.calendriers = calendriers;
		}

		@Override
		public void run() {
			super.run();
			long startTime = System.currentTimeMillis();
			List<String> urls = new ArrayList<String>();
			for (Pair<String, Calendrier> calenrier : calendriers) {
				addHoraires(Horaire.getHoraires(calenrier.getGauche(),
						arretLigne, calenrier.getDroite(), urls));
			}
			addUrlsEnErreur(urls);
			compteurThread.decrementAndGet();
			addTime(System.currentTimeMillis() - startTime);
		}
	}

	private Map<String, List<Pair<String, Calendrier>>> calendriersParLigne = new HashMap<String, List<Pair<String, Calendrier>>>();

	private boolean memesHoraires(List<Horaire> horaires1,
			List<Horaire> horaires2) {

		if (horaires1.size() != horaires2.size()) {
			return false;
		}
		Iterator<Horaire> itHoraires2 = horaires2.iterator();
		for (Horaire horaire1 : horaires1) {
			if (!horaire1.equals(itHoraires2.next())) {
				return false;
			}
		}
		return true;
	}

	private List<Calendrier> calendriers = new ArrayList<Calendrier>();
	private int calendrierIdCourant = 1;

	public Calendrier getCalendrierCommun(Calendrier calendrierActuel) {
		boolean calendrierTrouve = false;
		for (Calendrier calendrierConnu : calendriers) {
			if (calendrierActuel.equals(calendrierConnu)) {
				calendrierActuel.id = calendrierConnu.id;
				calendrierTrouve = true;
				break;
			}
		}
		if (!calendrierTrouve) {
			calendrierActuel.id = calendrierIdCourant++;
			calendriers.add(calendrierActuel);
		}
		return calendrierActuel;
	}

	private List<Pair<String, Calendrier>> getListCalendrier(ArretLigne arretLigne) {
		if (!calendriersParLigne.containsKey(arretLigne.ligneId)) {
			System.out.println("Calcul du calendrier pour la ligne " + arretLigne.ligneId);
			boolean hasErreurs = false;
			Map<String, Calendrier> mapCalendriers = new HashMap<String, Calendrier>();
			List<String> urlsEnErreurs = new ArrayList<String>();
			Map<String, List<Horaire>> mapHoraires = new HashMap<String, List<Horaire>>();
			
			int calendarId = 0;
			
			for (Pair<String, Calendrier> calendrier : ALL_CALENDRIER) {
				List<Horaire> horaires = Horaire.getHoraires(calendrier.getGauche(), arretLigne, calendrier.getDroite(), urlsEnErreurs);
				if (!urlsEnErreurs.isEmpty()) {
					mapCalendriers.put(calendrier.getGauche(), calendrier.getDroite().clone(++calendarId));
					urlsEnErreurs.clear();
					hasErreurs = true;
					continue;
				}
				boolean calendrierMerge = false;
				for (Entry<String, List<Horaire>> autreHoraires : mapHoraires.entrySet()) {
					if (memesHoraires(horaires, autreHoraires.getValue())) {
						mapCalendriers.get(autreHoraires.getKey()).merge(calendrier.getDroite());
						calendrierMerge = true;
						break;
					}
				}
				
				if (!calendrierMerge) {
					mapCalendriers.put(calendrier.getGauche(), calendrier.getDroite());
					mapHoraires.put(calendrier.getGauche(), horaires);
				}
			}
			
			// Réutilisation des calendrier existants.
			List<Pair<String, Calendrier>> pairCalendriers = new ArrayList<ChargeHoraires.Pair<String,Calendrier>>();
			for (Entry<String, Calendrier> calendrier : mapCalendriers.entrySet()) {
				pairCalendriers.add(new Pair<String, Calendrier>(calendrier.getKey(), getCalendrierCommun(calendrier.getValue())));
			}
			
			
			if (!hasErreurs) {
				System.out.println("Ajout du calendrier pour la ligne " + arretLigne.ligneId);
				calendriersParLigne.put(arretLigne.ligneId, pairCalendriers);
			}
			
		}
		return calendriersParLigne.get(arretLigne.ligneId);
	}

	protected void traitement() throws InterruptedException {
		File fileHoraires = new File("./src/tcbbase/horaires.txt");
		if (fileHoraires.exists()) {
			fileHoraires.delete();
		}
		File fileCalendriers = new File("./src/tcbbase/calendriers.txt");
		if (fileCalendriers.exists()) {
			fileCalendriers.delete();
		}
		GestionnaireLigne gestionnaireLigne = new GestionnaireLigne();
		List<ArretLigne> arretsLignes = gestionnaireLigne.getArretsLignes();
		int nbArretLigne = arretsLignes.size();
		int count = 0;
		long tempsAttente = 500;
		long startTime = System.currentTimeMillis();
		for (ArretLigne arretLigne : arretsLignes) {
			System.out
					.println("Avancement : " + count + " / " + nbArretLigne + "(" + compteurThread.get() + "threads)");
			// Get amount of free memory within the heap in bytes. This size
			// will increase
			// after garbage collection and decrease as new objects are created.
			long heapFreeSize = Runtime.getRuntime().freeMemory();
			System.out.println("Mémoire libre : "
					+ (heapFreeSize / 1024 / 1024) + "Mo");
			int compteurLocal = compteurThread.incrementAndGet();
			while (compteurLocal > maxThread) {
				compteurThread.decrementAndGet();
				Thread.sleep(200);
				compteurLocal = compteurThread.incrementAndGet();
			}
			new MyThread(arretLigne, getListCalendrier(arretLigne)).start();
			count++;
			tempsAttente = getNouveauTempsAttente(tempsAttente);
			Thread.sleep(tempsAttente);
			if (count > maxThread) {
				int tempsRestantSecondes = (int) (((nbArretLigne - (count)) * tempsAttente) / 1000);
				int tempsRestantMinutes = tempsRestantSecondes / 60;
				tempsRestantSecondes -= (tempsRestantMinutes * 60);
				int tempsRestantHeures = tempsRestantMinutes / 60;
				tempsRestantMinutes -= (tempsRestantHeures * 60);
				System.out.println("Temps restant approximatif : "
						+ tempsRestantHeures + ":" + tempsRestantMinutes + ":"
						+ tempsRestantSecondes);
			}
		}

		while (compteurThread.get() > 0) {
			Thread.sleep(200);
		}
		System.out.println("Temps de traitement avec " + maxThread
				+ " thread : " + (System.currentTimeMillis() - startTime));
		System.out.println("Ecriture du fichier resultat");
		Collections.sort(horaires, new Comparator<Horaire>() {

			@Override
			public int compare(Horaire o1, Horaire o2) {
				if (o1.ligneId.equals(o2.ligneId)) {
					if (o1.arretId.equals(o2.arretId)) {
						if (o1.calendrierId.equals(o2.calendrierId)) {
							return o1.horaire.compareTo(o2.horaire);
						} else {
							return o1.calendrierId.compareTo(o2.calendrierId);
						}
					} else {
						return o1.arretId.compareTo(o2.arretId);
					}
				} else {
					return o1.ligneId.compareTo(o2.ligneId);
				}
			}
		});
		Collections.sort(calendriers, new Comparator<Calendrier>(){
			@Override
			public int compare(Calendrier o1, Calendrier o2) {
				if (o1.lundi.equals(o2.lundi)) {
					if (o1.mardi.equals(o2.mardi)) {
						if (o1.mercredi.equals(o2.mercredi)) {
							if (o1.jeudi.equals(o2.jeudi)) {
								if (o1.vendredi.equals(o2.vendredi)) {
									if (o1.samedi.equals(o2.samedi)) {
										return o1.dimanche.compareTo(o2.dimanche);
									} else {
										return o1.samedi.compareTo(o2.samedi);
									}	
								} else {
									return o1.vendredi.compareTo(o2.vendredi);
								}
							} else {
								return o1.jeudi.compareTo(o2.jeudi);
							}
						} else {
							return o1.mercredi.compareTo(o2.mercredi);
						}
					} else {
						return o1.mardi.compareTo(o2.mardi);
					}
				} else {
					return o1.lundi.compareTo(o2.lundi);
				}
			}
		});
		@SuppressWarnings("unchecked")
		MoteurCsv moteur = new MoteurCsv(Arrays.asList(Horaire.class, Calendrier.class));
		moteur.writeFile(fileHoraires, horaires, Horaire.class);
		moteur.writeFile(fileCalendriers, calendriers, Calendrier.class);

		for (String url : urlsEnErreurs) {
			System.err.println(url);
		}

	}

	private final static Pair<String, Calendrier> LUNDI = new Pair<String, Calendrier>(
			"2011/05/02", Calendrier.LUNDI);
	private final static Pair<String, Calendrier> MARDI = new Pair<String, Calendrier>(
			"2011/05/03", Calendrier.MARDI);
	private final static Pair<String, Calendrier> MERCREDI = new Pair<String, Calendrier>(
			"2011/05/04", Calendrier.MERCREDI);
	private final static Pair<String, Calendrier> JEUDI = new Pair<String, Calendrier>(
			"2011/05/05", Calendrier.JEUDI);
	private final static Pair<String, Calendrier> VENDREDI = new Pair<String, Calendrier>(
			"2011/05/06", Calendrier.VENDREDI);
	private final static Pair<String, Calendrier> SAMEDI = new Pair<String, Calendrier>(
			"2011/05/07", Calendrier.SAMEDI);
	private final static Pair<String, Calendrier> DIMANCHE = new Pair<String, Calendrier>(
			"2011/05/08", Calendrier.DIMANCHE);
	@SuppressWarnings("unchecked")
	private final static List<Pair<String, Calendrier>> ALL_CALENDRIER = Arrays
			.asList(LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI, SAMEDI, DIMANCHE);

	public static void main(String[] args) throws InterruptedException {
		new ChargeHoraires(10).traitement();
	}

}
