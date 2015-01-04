/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportsrenneshelper.generateurmodele;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.ybo.transportsrenneshelper.generateurmodele.modele.Arret;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.ArretRoute;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.Calendrier;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.CalendrierException;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.Correspondance;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.Direction;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.Horaire;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.HoraireMetro;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.Ligne;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.Trajet;
import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybo.transportsrenneshelper.gtfs.modele.Calendar;
import fr.ybo.transportsrenneshelper.gtfs.modele.CalendarDates;
import fr.ybo.transportsrenneshelper.gtfs.modele.Route;
import fr.ybo.transportsrenneshelper.gtfs.modele.Stop;
import fr.ybo.transportsrenneshelper.gtfs.modele.StopTime;
import fr.ybo.transportsrenneshelper.gtfs.modele.Trip;
import fr.ybo.transportsrenneshelper.keolis.GetMetro;
import fr.ybo.transportsrenneshelper.keolis.modele.MetroStation;
import fr.ybo.transportsrenneshelper.util.CalculDistance;
import fr.ybonnel.csvengine.CsvEngine;
import fr.ybonnel.csvengine.exception.CsvErrorsExceededException;
import fr.ybonnel.csvengine.model.EngineParameters;

/**
 * Générateur.
 * 
 * @author ybonnel
 */
public class Generateur {

	/**
	 * Les lignes.
	 */
	private List<Ligne> lignes = new ArrayList<Ligne>();
	/**
	 * Les calendriers.
	 */
	private Collection<Calendrier> calendriers = new ArrayList<Calendrier>();

	private Collection<CalendrierException> calendrierExceptions = new ArrayList<CalendrierException>();
	/**
	 * Les trajets (par ligne).
	 */
	private Map<String, List<Trajet>> trajets = new HashMap<String, List<Trajet>>();
	/**
	 * Les directions (par id).
	 */
	private Map<Integer, Direction> directions = new HashMap<Integer, Direction>();
	/**
	 * Map des directionsId (pour générer un id unique).
	 */
	private Map<String, Integer> mapDirectionIds;
	/**
	 * Les horaires.
	 */
	private Collection<Horaire> horaires = new ArrayList<Horaire>();
	/**
	 * Les arrets (par id).
	 */
	private Map<String, Arret> arrets = new HashMap<String, Arret>();
	/**
	 * Les ArretRoutes.
	 */
	private Collection<ArretRoute> arretsRoutes = new ArrayList<ArretRoute>();
	/**
	 * Les correspondances.
	 */
	private Collection<Correspondance> correspondances = new ArrayList<Correspondance>();
	/**
	 * Les horaires (par ligne).
	 */
	private Map<String, List<Horaire>> horairesByLigneId = new HashMap<String, List<Horaire>>();

	/**
	 * Les des classes du modèle de données.
	 */
	private static final Class<?>[] LIST_CLASSES = {
		Arret.class,
		ArretRoute.class,
		Calendrier.class,
		CalendrierException.class,
		Direction.class,
		Horaire.class,
		Ligne.class,
		Trajet.class,
		Correspondance.class
	};

	/**
	 * Recherche de point d'intérêts (test à faire).
	 */
	public void rechercherPointsInterets() {
		int max = 0;
		Arret arretLong = null;
		for (Arret arret : arrets.values()) {
			if (arret.nom.length() > max) {
				max = arret.nom.length();
				arretLong = arret;
			}
		}
		if (arretLong != null) {
			System.out.println("Arret avec le nom le plus long : "
					+ arretLong.nom + " qui existe sur les lignes :");
		}
		for (ArretRoute arretRoute : arretsRoutes) {
			if (arretLong != null && arretRoute.arretId.equals(arretLong.id)) {
				System.out.println('\t' + arretRoute.ligneId);
			}
		}

		int maxDirectionLength = 0;
		Direction directionLongue = null;
		for (Direction direction : directions.values()) {
			if (direction.direction.length() > maxDirectionLength) {
				maxDirectionLength = direction.direction.length();
				directionLongue = direction;
			}
		}
		for (ArretRoute arretRoute : arretsRoutes) {
			if (directionLongue != null
					&& arretRoute.directionId == directionLongue.id) {
				System.out.println("Direction avec le nom le plus long : "
						+ directionLongue.direction + " pour la ligne "
						+ arretRoute.ligneId);
				break;
			}
		}

	}

	/**
	 * Génération des fichiers.
	 * 
	 * @param repertoire
	 *            répertoire de sortie.
	 */
	public void genererFichiers(File repertoire) throws IOException {
		if (repertoire.exists()) {
			for (File file : repertoire.listFiles()) {
				if (!file.delete()) {
					System.err.println("Le fichier " + file.getName()
							+ "n'a pas pu être effacé");
				}
			}
		} else {
			if (!repertoire.mkdirs()) {
				System.err.println("Le répertoire " + repertoire.getName()
						+ "n'a pas pu être créé");
			}
		}
		CsvEngine moteurCsv = new CsvEngine(EngineParameters.createBuilder().setAddQuoteCar(false).build(), LIST_CLASSES);
		System.out.println("Génération du fichier arrets.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "arrets.txt")),
				arrets.values(), Arret.class);
		System.out.println("Génération du fichier arrets_routes.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "arrets_routes.txt")),
				arretsRoutes, ArretRoute.class);
		System.out.println("Génération du fichier calendriers.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "calendriers.txt")),
				calendriers, Calendrier.class);
		System.out.println("Génération du fichier calendriers_exceptions.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "calendriers_exceptions.txt")), calendrierExceptions,
				CalendrierException.class);
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "calendriers.txt")),
				calendriers, Calendrier.class);
		System.out.println("Génération du fichier directions.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "directions.txt")),
				directions.values(), Direction.class);
		System.out.println("Génération du fichier horaires.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "horaires.txt")), horaires,
				Horaire.class);
		System.out.println("Génération du fichier lignes.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "lignes.txt")), lignes,
				Ligne.class);
		System.out.println("Génération du fichier correspondances.txt");
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "correspondances.txt")),
				correspondances, Correspondance.class);
		System.out.println("Génération du fichier trajets.txt");
		Collection<Trajet> trajetsTmp = new ArrayList<Trajet>();
		for (List<Trajet> trajetsToAdd : trajets.values()) {
			trajetsTmp.addAll(trajetsToAdd);
		}
		moteurCsv.writeFile(new FileWriter(new File(repertoire, "trajets.txt")), trajetsTmp,
				Trajet.class);
		for (Ligne ligne : lignes) {
			List<Horaire> horaires = horairesByLigneId.get(ligne.id);
			long tailleOctets = 0;
			for (Horaire horaire : horaires) {
				tailleOctets += horaire.nbOctets();
			}
			if (tailleOctets < (900 * 1024)) {
				moteurCsv.writeFile(new FileWriter(new File(repertoire, "horaires_" + ligne.id + ".txt")), horaires, Horaire.class);
			} else {
				// Découpage de la liste en deux liste
				int pivot = horaires.size() / 3;
				moteurCsv.writeFile(new FileWriter(new File(repertoire, "horaires_" + ligne.id + ".txt")), horaires.subList(0, pivot),
						Horaire.class);
				moteurCsv.writeFile(new FileWriter(new File(repertoire, "horaires_" + ligne.id + "_1.txt")),
						horaires.subList(pivot, pivot * 2), Horaire.class);
				moteurCsv.writeFile(new FileWriter(new File(repertoire, "horaires_" + ligne.id + "_2.txt")),
						horaires.subList(pivot * 2, horaires.size()), Horaire.class);
			}
			System.out.println("Nombre d'horaire pour la ligne " + ligne.nomCourt + " : " + horaires.size());
		}
		genereZips(repertoire);
	}

	/**
	 * Génération des zips.
	 * 
	 * @param repertoire
	 *            répertoire de sortie.
	 */
	private void genereZips(File repertoire) {

		System.out.println("Création du zip principal");
		try {

			System.out.println("Création du fichier last_update.txt");
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(
					new File(repertoire, "last_update.txt")));
			try {
				bufWriter.write(new SimpleDateFormat("yyyyMMdd")
						.format(new Date()));
			} finally {
				bufWriter.close();
			}
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Distance réel pour considérer que c'est une correspondance.
	 */
	private static final double DISTANCE_CORRESPONDANCE_REEL = 200.0;

	/**
	 * Distance approximative pour la recherche de correspondance.
	 */
	private static final double DISTANCE_RECHERCHE_METRE = 300.0;
	/**
	 * Nombre de mètres dans un dégré de latitude.
	 */
	private static final double DEGREE_LATITUDE_EN_METRES = 111192.62;
	/**
	 * Nombre de degré de latitude à prendre dans la recherche de
	 * correspondances.
	 */
	private static final double DISTANCE_LAT_IN_DEGREE = DISTANCE_RECHERCHE_METRE
			/ DEGREE_LATITUDE_EN_METRES;
	/**
	 * Nombre de mètres dans un dégré de longitude.
	 */
	private static final double DEGREE_LONGITUDE_EN_METRES = 74452.10;
	/**
	 * Nombre de degré de longitude à prendre dans la recherche de
	 * correspondances.
	 */
	private static final double DISTANCE_LNG_IN_DEGREE = DISTANCE_RECHERCHE_METRE
			/ DEGREE_LONGITUDE_EN_METRES;

	/**
	 * Remplie les correspondances.
	 */
	public void remplirCorrespondance() {
		for (Arret arret : arrets.values()) {
			for (Arret correspondance : arrets.values()) {
				if (hasFastCorrespondance(arret, correspondance)
						&& !arret.id.equals(correspondance.id)) {
					double distance = calculDistanceBetweenArrets(arret,
							correspondance);
					if (distance < DISTANCE_CORRESPONDANCE_REEL) {
						correspondances.add(new Correspondance(arret.id,
								correspondance.id, (int) Math.round(distance)));
					}
				}
			}
		}
	}

	/**
	 * Calcul de distance entre deux arrêts.
	 * 
	 * @param arretDepart
	 *            arrêt de départ.
	 * @param arretArrivee
	 *            arrêt d'arrivé.
	 * @return la distance en mètres.
	 */
	private static double calculDistanceBetweenArrets(Arret arretDepart,
			Arret arretArrivee) {
		return new CalculDistance(arretDepart.latitude, arretDepart.longitude,
				arretArrivee.latitude, arretArrivee.longitude).calculDistance();
	}

	/**
	 * Permet de faire une première selection de correspondance beaucoup plus
	 * rapide que le calcul de distances réel.
	 * 
	 * @param arret1
	 *            arret1.
	 * @param arret2
	 *            arret2.
	 * @return true si on peut considérer que se sont des correspondances.
	 */
	private boolean hasFastCorrespondance(Arret arret1, Arret arret2) {
		double difLatitude = Math.abs(arret1.latitude - arret2.latitude);
		double difLongitude = Math.abs(arret1.longitude - arret2.longitude);
		return difLatitude < DISTANCE_LAT_IN_DEGREE
				&& difLongitude < DISTANCE_LNG_IN_DEGREE;
	}

	/**
	 * Liste des erreurs de double direction pour un trajet unique (pour éviter
	 * l'affichage multiple).
	 */
	private List<String> listeChaineDirectionErreurs = new ArrayList<String>();

	/**
	 * Remplie les arretRoutes.
	 */
	public void remplirArretRoutes() {
		Map<Integer, List<Horaire>> mapHorairesByTrajetId = new HashMap<Integer, List<Horaire>>();
		for (Horaire horaire : horaires) {
			if (!mapHorairesByTrajetId.containsKey(horaire.trajetId)) {
				mapHorairesByTrajetId.put(horaire.trajetId,
						new ArrayList<Horaire>());
			}
			mapHorairesByTrajetId.get(horaire.trajetId).add(horaire);
		}
		// Trip des horaires.
		for (List<Horaire> horairesATrier : mapHorairesByTrajetId.values()) {
			Collections.sort(horairesATrier, new Comparator<Horaire>() {
				public int compare(Horaire o1, Horaire o2) {
					return o1.stopSequence < o2.stopSequence ? -1
							: o1.stopSequence == o2.stopSequence ? 0 : 1;
				}
			});
		}
		for (Ligne ligne : lignes) {
			horairesByLigneId.put(ligne.id, new ArrayList<Horaire>());
			Map<String, Integer> countByChaine = new HashMap<String, Integer>();
			Map<String, List<Trajet>> mapTrajetChaine = new HashMap<String, List<Trajet>>();
			Map<String, Arret> arretOfLigne = new HashMap<String, Arret>();
			Map<String, Integer> macroDirectionsParChaine = new HashMap<String, Integer>();
			// Parcours des trajets.
			if (!trajets.containsKey(ligne.id)) {
				System.err.println("Ligne sans trajets : " + ligne.id);
				continue;
			}
			for (Trajet trajet : trajets.get(ligne.id)) {
				StringBuilder chaineBuilder = new StringBuilder();
				Horaire terminus = null;
				for (Horaire horaire : mapHorairesByTrajetId.get(trajet.id)) {
					horairesByLigneId.get(ligne.id).add(horaire);
					if (!arretOfLigne.containsKey(horaire.arretId)) {
						if (arrets.get(horaire.arretId) == null) {
							System.err.println("L'horaire de l'arrêt "
									+ horaire.arretId + " a été écarté");
						} else {
							arretOfLigne.put(horaire.arretId,
									arrets.get(horaire.arretId));
						}
					}
					chaineBuilder.append(horaire.arretId);
					chaineBuilder.append(',');
					terminus = horaire;
				}
				if (terminus == null) {
					throw new IllegalArgumentException("Pas de terminus trouvé");
				}
				terminus.terminus = true;
				if (!countByChaine.containsKey(chaineBuilder.toString())) {
					countByChaine.put(chaineBuilder.toString(), 0);
					mapTrajetChaine.put(chaineBuilder.toString(),
							new ArrayList<Trajet>());
					macroDirectionsParChaine.put(chaineBuilder.toString(),
							trajet.macroDirection);
				}
				if (!macroDirectionsParChaine.get(chaineBuilder.toString())
						.equals(trajet.macroDirection)) {
					System.err
							.println("ALERTE : plusieurs macro direction trouvée pour un seule chaine");
				}
				countByChaine.put(chaineBuilder.toString(),
						countByChaine.get(chaineBuilder.toString()) + 1);
				mapTrajetChaine.get(chaineBuilder.toString()).add(trajet);
			}
			// parcours des arrêts
			for (Arret arret : arretOfLigne.values()) {
				// Recherche du trajet adéquat.
				Map<Integer, String> mapMacroDirectionChaine = new HashMap<Integer, String>();
				Map<Integer, Integer> mapMacroDirectionMax = new HashMap<Integer, Integer>();
				for (Map.Entry<String, Integer> entryChaineCount : countByChaine
						.entrySet()) {
					if (entryChaineCount.getKey().startsWith(arret.id + ',')
							|| !entryChaineCount.getKey().endsWith(
									',' + arret.id + ',')
							&& entryChaineCount.getKey().contains(
									',' + arret.id + ',')) {
						// Chemin trouvé
						Integer macroDirection = macroDirectionsParChaine
								.get(entryChaineCount.getKey());
						if (!mapMacroDirectionMax.containsKey(macroDirection)) {
							mapMacroDirectionMax.put(macroDirection, 0);
						}
						if (entryChaineCount.getValue() > mapMacroDirectionMax
								.get(macroDirection)) {
							mapMacroDirectionMax.put(macroDirection,
									entryChaineCount.getValue());
							mapMacroDirectionChaine.put(macroDirection,
									entryChaineCount.getKey());
						}
					}
				}
				for (Entry<Integer, String> entryMacroDirectionChaine : mapMacroDirectionChaine
						.entrySet()) {
					ArretRoute arretRoute = new ArretRoute();
					arretRoute.arretId = arret.id;
					arretRoute.ligneId = ligne.id;
					arretRoute.macroDirection = entryMacroDirectionChaine
							.getKey();
					String[] champs = entryMacroDirectionChaine.getValue()
							.split(",");
					int sequence = 1;
					for (String champ : champs) {
						if (champ.equals(arret.id)) {
							break;
						}
						sequence++;
					}
					arretRoute.sequence = sequence;
					Map<Integer, Integer> countDirectionIds = new HashMap<Integer, Integer>();
					for (Trajet trajet : mapTrajetChaine
							.get(entryMacroDirectionChaine.getValue())) {
						if (!countDirectionIds.containsKey(trajet.directionId)) {
							countDirectionIds.put(trajet.directionId, 0);
						}
						countDirectionIds.put(trajet.directionId,
								countDirectionIds.get(trajet.directionId) + 1);
					}
					int directionCount = 0;
					int directionId = -1;
					for (Map.Entry<Integer, Integer> entryDirectionIdCount : countDirectionIds
							.entrySet()) {
						if (entryDirectionIdCount.getValue() > directionCount) {
							directionId = entryDirectionIdCount.getKey();
							directionCount = entryDirectionIdCount.getValue();
						}
					}
					if (countDirectionIds.size() > 1
							&& !listeChaineDirectionErreurs
									.contains(entryMacroDirectionChaine
											.getValue())) {
						listeChaineDirectionErreurs
								.add(entryMacroDirectionChaine.getValue());
						System.err
								.println("Plusieurs directions trouvée pour une seule chaine :");
						System.err.println('\t' + entryMacroDirectionChaine
								.getValue());
						for (int dirId : countDirectionIds.keySet()) {

							System.err.println(directions.get(dirId).direction);
						}
						System.err
								.println("Direction choisi (la plus utilisée) :");
						System.err
								.println('\t' + directions.get(directionId).direction);
					}
					if (directionId == -1) {
						System.err.println("Pas de direction trouvée!!!!!");
					}
					arretRoute.directionId = directionId;
                    if (GestionnaireGtfs.getInstance()
                            .getMapStops().get(arretRoute.arretId) == null) {
                        System.err.println("Pas d'arrêt d'id : " + arretRoute.arretId);
                    }
					if (GestionnaireGtfs.getInstance()
							.getMapStops().get(arretRoute.arretId).accessible) {
						arretRoute.accessible = false;
						for (Trip trip : GestionnaireGtfs.getInstance().getMapTrips().values()) {
							if (trip.routeId.equals(arretRoute.ligneId) && trip.accessible) {
								arretRoute.accessible = true;
								break;
							}
						}
					} else {
						arretRoute.accessible = false;
					}
					arretsRoutes.add(arretRoute);
				}
			}
		}
	}

	/**
	 * Remplie les arrêts.
	 */
	public void remplirArrets() {
		for (Stop stop : GestionnaireGtfs.getInstance().getMapStops().values()) {
			Arret arret = new Arret();
			arret.id = stop.id;
			arret.nom = stop.nom;
			arret.latitude = stop.latitude;
			arret.longitude = stop.longitude;
			arrets.put(arret.id, arret);
		}
	}

	/**
	 * Remplie les horaires.
	 */
	public void remplirHoraires() {
		for (StopTime stopTime : GestionnaireGtfs.getInstance()
				.getMapStopTimes().values()) {
			Horaire horaire = new Horaire();
			horaire.arretId = stopTime.stopId;
			horaire.trajetId = Integer.parseInt(stopTime.tripId);
			horaire.heureDepart = stopTime.heureDepart;
			horaire.stopSequence = stopTime.stopSequence;
			horaire.terminus = false;
			horaires.add(horaire);
		}
	}

	/**
	 * Remplie les trajets.
	 */
	public void remplirTrajets() {
		remplirDirections();
		for (Trip trip : GestionnaireGtfs.getInstance().getMapTrips().values()) {
			Trajet trajet = new Trajet();
			trajet.id = Integer.parseInt(trip.id);
			trajet.calendrierId = Integer.parseInt(trip.serviceId);
			trajet.ligneId = trip.routeId;
			trajet.directionId = mapDirectionIds.get(trip.headSign);
			trajet.macroDirection = trip.directionId;
			if (!trajets.containsKey(trajet.ligneId)) {
				trajets.put(trajet.ligneId, new ArrayList<Trajet>());
			}
			trajets.get(trajet.ligneId).add(trajet);
		}
	}

	/**
	 * Remplie les directions.
	 */
	public void remplirDirections() {
		if (mapDirectionIds == null) {
			mapDirectionIds = new HashMap<String, Integer>();
			int directionId = 1;
			for (Trip trip : GestionnaireGtfs.getInstance().getMapTrips()
					.values()) {
				if (!mapDirectionIds.containsKey(trip.headSign)) {
					mapDirectionIds.put(trip.headSign, directionId);
					directionId++;
				}

			}
			for (Map.Entry<String, Integer> headSign : mapDirectionIds
					.entrySet()) {
				Direction direction = new Direction();
				direction.id = headSign.getValue();
				String directionTmp = headSign.getKey();
				String[] champs = directionTmp.split("\\|");
				if (champs.length == 2) {
					direction.direction = champs[1];
					while (direction.direction.length() > 0
							&& direction.direction.charAt(0) == ' ') {
						direction.direction = direction.direction.substring(1);
					}
				} else {
					System.err.println("Problème sur la direction : "
							+ headSign.getKey());
				}
				directions.put(direction.id, direction);
			}
		}
	}

	/**
	 * Remplie les calendrier.
	 */
	public void remplirCalendrier() {
		for (Calendar calendar : GestionnaireGtfs.getInstance()
				.getMapCalendars().values()) {
			Calendrier calendrier = new Calendrier();
			calendrier.id = Integer.parseInt(calendar.id);
			calendrier.lundi = calendar.lundi;
			calendrier.mardi = calendar.mardi;
			calendrier.mercredi = calendar.mercredi;
			calendrier.jeudi = calendar.jeudi;
			calendrier.vendredi = calendar.vendredi;
			calendrier.samedi = calendar.samedi;
			calendrier.dimanche = calendar.dimanche;
			calendrier.dateDebut = calendar.startDate;
			calendrier.dateFin = calendar.endDate;
			calendriers.add(calendrier);
		}
	}
	
	public void remplirCalendrierExceptions() {
		for (CalendarDates calendarDate : GestionnaireGtfs.getInstance().getCalendarsDates()) {
			CalendrierException calendrierException = new CalendrierException();
			calendrierException.calendrierId = Integer.parseInt(calendarDate.serviceId);
			calendrierException.date = calendarDate.date;
			calendrierException.ajout = calendarDate.exceptionType == 1;
			calendrierExceptions.add(calendrierException);
		}
	}

	/**
	 * Remplie les lignes.
	 */
	public void remplirLignes() {
		List<Route> routes = new ArrayList<Route>();
		routes.addAll(GestionnaireGtfs.getInstance().getMapRoutes().values());
		int maxLength = 0;
		// Recherche de la route avec le nom le plus long.
		for (Route route : routes) {
			if (route.nomCourt.length() > maxLength) {
				maxLength = route.nomCourt.length();
			}
		}
		// Formatage du nom court de la ligne.
		for (Route route : routes) {
			route.nomCourtFormatte = route.nomCourt;
			while (route.nomCourtFormatte.length() < maxLength) {
				route.nomCourtFormatte = '0' + route.nomCourtFormatte;
			}
		}

		int ordre = 1;
		for (Route route : routes) {
			Ligne ligne = new Ligne();
			ligne.id = route.id;
			ligne.nomCourt = route.nomCourt;
			ligne.nomLong = route.nomLong;
			if (ligne.nomCourt.equals("a")) {
				ligne.ordre = 0;
			} else {
				ligne.ordre = ordre;
				ordre++;
			}
			lignes.add(ligne);
		}
	}
}
