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

package fr.ybo.gtfs.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.ybo.gtfs.chargement.GestionZipKeolis;
import fr.ybo.gtfs.csv.moteur.MoteurCsv;

public class GestionnaireGtfs {

	@SuppressWarnings("serial")
	public static class CoupleLigne implements Serializable {
		public final String ligneId1;
		public final String ligneId2;

		public CoupleLigne(String ligneId1, String ligneId2) {
			this.ligneId1 = ligneId1;
			this.ligneId2 = ligneId2;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}

			GestionnaireGtfs.CoupleLigne that = (GestionnaireGtfs.CoupleLigne) obj;

			return ligneId1.equals(that.ligneId1) && ligneId2.equals(that.ligneId2);

		}

		@Override
		public int hashCode() {
			int result = ligneId1.hashCode();
			result = 31 * result + ligneId2.hashCode();
			return result;
		}
	}

	private static final Logger LOGGER = Logger.getLogger(GestionnaireGtfs.class.getName());

	private static final List<Class<?>> CLASSES_CSV = new ArrayList<Class<?>>(8);

	static {
		CLASSES_CSV.add(Arret.class);
		CLASSES_CSV.add(ArretRoute.class);
		CLASSES_CSV.add(Calendrier.class);
		CLASSES_CSV.add(Direction.class);
		CLASSES_CSV.add(Horaire.class);
		CLASSES_CSV.add(Ligne.class);
		CLASSES_CSV.add(Trajet.class);
		CLASSES_CSV.add(Correspondance.class);
	}


	private Map<String, Arret> arrets;
	private Map<String, Map<String, ArretRoute>> arretsRoutesByArretId;
	private Map<Integer, Calendrier> calendriers;
	private Map<String, Map<Integer, Horaire>> horaires = new HashMap<String, Map<Integer, Horaire>>(1500);
	private Map<String, Ligne> lignes;
	private Map<Integer, Direction> directions;
	private Map<GestionnaireGtfs.CoupleLigne, Collection<Correspondance>> correspondances;

	public String getDirection(int directionId) {
		return directions.get(directionId).direction;
	}

	public Collection<Correspondance> getCorrespondances(GestionnaireGtfs.CoupleLigne coupleLigne) {
		return correspondances.get(coupleLigne);
	}

	public Calendrier getCalendrier(Integer calendrierId) {
		return calendriers.get(calendrierId);
	}

	public Iterable<Arret> getAllArrets() {
		return arrets.values();
	}

	public Arret getArret(String arretId) {
		return arrets.get(arretId);
	}

	public Iterable<Horaire> getHorairesByArretId(String arretId) {
		if (horaires.containsKey(arretId)) {
			return horaires.get(arretId).values();
		}
		return new ArrayList<Horaire>(0);
	}

	public Horaire getHoraireByArretIdAndTrajetId(String arretId, Integer trajetId) {
		if (horaires.containsKey(arretId)) {
			return horaires.get(arretId).get(trajetId);
		}
		return null;
	}

	public Iterable<ArretRoute> getArretRoutesByArretId(String arretId) {
		return arretsRoutesByArretId.containsKey(arretId) ? arretsRoutesByArretId.get(arretId).values() : new ArrayList<ArretRoute>(0);
	}

	public Ligne getLigne(String ligneId) {
		return lignes.get(ligneId);
	}

	private static GestionnaireGtfs instance;

	public static synchronized GestionnaireGtfs getInstance() {
		if (instance == null) {
			instance = new GestionnaireGtfs();
		}
		return instance;
	}

	private MoteurCsv moteur = null;

	private MoteurCsv getMoteur() {
		if (moteur == null) {
			moteur = new MoteurCsv(CLASSES_CSV);
		}
		return moteur;
	}

	private GestionnaireGtfs() {
		long startTime = System.nanoTime();
		directions = new HashMap<Integer, Direction>();
		for (Direction direction : GestionZipKeolis.getAndParseKeolis(getMoteur(), "directions.txt", Direction.class)) {
			directions.put(direction.id, direction);
		}
		arrets = new HashMap<String, Arret>(1500);
		for (Arret arret : GestionZipKeolis.getAndParseKeolis(getMoteur(), "arrets.txt", Arret.class)) {
			arrets.put(arret.id, arret);
		}

		arretsRoutesByArretId = new HashMap<String, Map<String, ArretRoute>>(1500);
		for (ArretRoute arretRoute : GestionZipKeolis.getAndParseKeolis(getMoteur(), "arrets_routes.txt", ArretRoute.class)) {
			if (!arretsRoutesByArretId.containsKey(arretRoute.arretId)) {
				arretsRoutesByArretId.put(arretRoute.arretId, new HashMap<String, ArretRoute>(2));
			}
			arretsRoutesByArretId.get(arretRoute.arretId).put(arretRoute.ligneId, arretRoute);
		}

		calendriers = new HashMap<Integer, Calendrier>(20);
		for (Calendrier calendrier : GestionZipKeolis.getAndParseKeolis(getMoteur(), "calendriers.txt", Calendrier.class)) {
			calendriers.put(calendrier.id, calendrier);
		}

		lignes = new HashMap<String, Ligne>(67);
		for (Ligne ligne : GestionZipKeolis.getAndParseKeolis(getMoteur(), "lignes.txt", Ligne.class)) {
			lignes.put(ligne.id, ligne);
		}

		Map<Integer, Trajet> trajets;
		trajets = new HashMap<Integer, Trajet>(2500);
		for (Trajet trajet : GestionZipKeolis.getAndParseKeolis(getMoteur(), "trajets.txt", Trajet.class)) {
			trajets.put(trajet.id, trajet);
		}
		for (Ligne ligne : lignes.values()) {
			Collection<Horaire> horairesLigne;
			horairesLigne = GestionZipKeolis.chargeLigne(getMoteur(), ligne.id);
			for (Horaire horaire : horairesLigne) {
				horaire.trajet = trajets.get(horaire.trajetId);
				if (!horaires.containsKey(horaire.arretId)) {
					horaires.put(horaire.arretId, new HashMap<Integer, Horaire>(100));
				}
				horaires.get(horaire.arretId).put(horaire.trajetId, horaire);
			}
		}

		correspondances =
				new HashMap<GestionnaireGtfs.CoupleLigne, Collection<Correspondance>>(200);
		for (Correspondance correspondance : GestionZipKeolis.getCorrespondances(getMoteur())) {
			Collection<String> lignes1 = new ArrayList<String>(10);
			for (ArretRoute arretRoute : getArretRoutesByArretId(correspondance.arretId)) {
				lignes1.add(arretRoute.ligneId);
			}
			Collection<String> lignes2 = new ArrayList<String>(10);
			for (ArretRoute arretRoute : getArretRoutesByArretId(correspondance.correspondanceId)) {
				lignes2.add(arretRoute.ligneId);
			}

			for (String ligne1 : lignes1) {
				for (String ligne2 : lignes2) {
					GestionnaireGtfs.CoupleLigne coupleLigne = new GestionnaireGtfs.CoupleLigne(ligne1, ligne2);
					if (!correspondances.containsKey(coupleLigne)) {
						correspondances.put(coupleLigne, new ArrayList<Correspondance>(10));
					}
					correspondances.get(coupleLigne).add(correspondance);
				}
			}
		}
		long elapsedTime = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info("Construction du gestionnaire gtfs en " + elapsedTime + " ms");
	}
}
