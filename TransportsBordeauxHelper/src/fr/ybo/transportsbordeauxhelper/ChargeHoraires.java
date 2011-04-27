package fr.ybo.transportsbordeauxhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsbordeauxhelper.modeletcb.ArretLigne;
import fr.ybo.transportsbordeauxhelper.modeletcb.Calendrier;
import fr.ybo.transportsbordeauxhelper.modeletcb.Horaire;

public class ChargeHoraires {

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

	private class MyThread extends Thread {

		private ArretLigne arretLigne;

		public MyThread(ArretLigne arretLigne) {
			this.arretLigne = arretLigne;
		}

		@Override
		public void run() {
			super.run();
			List<String> urls = new ArrayList<String>();
			addHoraires(Horaire.getHoraires(LUNDI, arretLigne, Calendrier.LUNDI, urls));
			addHoraires(Horaire.getHoraires(MARDI, arretLigne, Calendrier.MARDI, urls));
			addHoraires(Horaire.getHoraires(MERCREDI, arretLigne, Calendrier.MERCREDI, urls));
			addHoraires(Horaire.getHoraires(JEUDI, arretLigne, Calendrier.JEUDI, urls));
			addHoraires(Horaire.getHoraires(VENDREDI, arretLigne, Calendrier.VENDREDI, urls));
			addHoraires(Horaire.getHoraires(SAMEDI, arretLigne, Calendrier.SAMEDI, urls));
			addHoraires(Horaire.getHoraires(DIMANCHE, arretLigne, Calendrier.DIMANCHE, urls));
			addUrlsEnErreur(urls);
			compteurThread.decrementAndGet();
		}
	}

	protected void traitement() throws InterruptedException {
		File fileHoraires = new File("./src/tcbbase/horaires.txt");
		if (fileHoraires.exists()) {
			fileHoraires.delete();
		}
		GestionnaireLigne gestionnaireLigne = new GestionnaireLigne();
		List<ArretLigne> arretsLignes = gestionnaireLigne.getArretsLignes();
		int nbArretLigne = arretsLignes.size();
		int count = 0;
		long startTime = System.currentTimeMillis();
		for (ArretLigne arretLigne : arretsLignes) {
			System.out.println("Avancement : " + count + " / " + nbArretLigne);
			// Get amount of free memory within the heap in bytes. This size
			// will increase
			// after garbage collection and decrease as new objects are created.
			long heapFreeSize = Runtime.getRuntime().freeMemory();
			System.out.println("Mémoire libre : " + (heapFreeSize / 1024 / 1024) + "Mo");
			int compteurLocal = compteurThread.incrementAndGet();
			long startLocalTime = System.currentTimeMillis();
			while (compteurLocal > maxThread) {
				compteurThread.decrementAndGet();
				Thread.sleep(200);
				compteurLocal = compteurThread.incrementAndGet();
			}
			if ((System.currentTimeMillis() - startLocalTime) > 30000) {
				System.out.println("Détection d'un temps d'attente > 30s -> Attente de 60 minutes");
				Thread.sleep(3600000);
			} else if ((System.currentTimeMillis() - startLocalTime) > 10000) {
				System.out.println("Détection d'un temps d'attente > 10s -> Attente de 15 minute");
				Thread.sleep(900000);
			}
			new MyThread(arretLigne).start();
			count++;
			Thread.sleep(1000);
			if (count > maxThread) {
				long tempsTraitment = System.currentTimeMillis() - startTime;
				long tempsMoyen = ((tempsTraitment - (maxThread * 1000)) / (count - maxThread));
				int tempsRestantSecondes = (int) (((nbArretLigne - (count - maxThread)) * tempsMoyen) / 1000);
				int tempsRestantMinutes = tempsRestantSecondes / 60;
				tempsRestantSecondes -= (tempsRestantMinutes * 60);
				int tempsRestantHeures = tempsRestantMinutes / 60;
				tempsRestantMinutes -= (tempsRestantHeures * 60);
				System.out.println("Temps restant approximatif : " + tempsRestantHeures + ":" + tempsRestantMinutes
						+ ":" + tempsRestantSecondes);
			}
		}

		while (compteurThread.get() > 0) {
			Thread.sleep(200);
		}
		System.out.println("Temps de traitement avec " + maxThread + " thread : "
				+ (System.currentTimeMillis() - startTime));
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
		@SuppressWarnings({ "unchecked", "rawtypes" })
		MoteurCsv moteur = new MoteurCsv((List) Collections.singletonList(Horaire.class));
		moteur.writeFile(fileHoraires, horaires, Horaire.class);

		for (String url : urlsEnErreurs) {
			System.err.println(url);
		}

	}

	private final static String LUNDI = "2011/05/02";
	private final static String MARDI = "2011/05/03";
	private final static String MERCREDI = "2011/05/04";
	private final static String JEUDI = "2011/05/05";
	private final static String VENDREDI = "2011/05/06";
	private final static String SAMEDI = "2011/05/07";
	private final static String DIMANCHE = "2011/05/08";

	public static void main(String[] args) throws InterruptedException {
		new ChargeHoraires(20).traitement();
	}

}
