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

import fr.ybo.gtfs.chargement.GestionZipKeolis;
import fr.ybo.gtfs.csv.moteur.MoteurCsv;

import java.util.*;
import java.util.logging.Logger;

public class GestionnaireGtfs {

	public static class CoupleLigne {
		public final String ligneId1;
		public final String ligneId2;

		public CoupleLigne(String ligneId1, String ligneId2) {
			this.ligneId1 = ligneId1;
			this.ligneId2 = ligneId2;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			CoupleLigne that = (CoupleLigne) o;

			return ligneId1.equals(that.ligneId1) && ligneId2.equals(that.ligneId2);

		}

		@Override
		public int hashCode() {
			int result = ligneId1.hashCode();
			result = 31 * result + ligneId2.hashCode();
			return result;
		}
	}

	private static final Logger logger = Logger.getLogger(GestionnaireGtfs.class.getName());

	private static final List<Class<?>> CLASSES_CSV = new ArrayList<Class<?>>();

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


	private final Map<String, Arret> arrets = new HashMap<String, Arret>();
	private final Map<String, Map<String, ArretRoute>> arretsRoutesByArretId = new HashMap<String, Map<String, ArretRoute>>();
	private final Map<Integer, Calendrier> calendriers = new HashMap<Integer, Calendrier>();
	private final Map<String, Map<Integer, Horaire>> horaires = new HashMap<String, Map<Integer, Horaire>>();
	private final Map<String, Ligne> lignes = new HashMap<String, Ligne>();
	private final Map<CoupleLigne, Collection<Correspondance>> correspondances = new HashMap<CoupleLigne, Collection<Correspondance>>();

	public Collection<Correspondance> getCorrespondances(CoupleLigne coupleLigne) {
		return correspondances.get(coupleLigne);
	}

	public Calendrier getCalendrier(Integer calendrierId) {
		return calendriers.get(calendrierId);
	}

	public Collection<Arret> getAllArrets() {
		return arrets.values();
	}

	public Arret getArret(String arretId) {
		return arrets.get(arretId);
	}

	public Collection<Horaire> getHorairesByArretId(String arretId) {
		if (horaires.containsKey(arretId)) {
			return horaires.get(arretId).values();
		}
		return new ArrayList<Horaire>();
	}

	public Horaire getHoraireByArretIdAndTrajetId(String arretId, Integer trajetId) {
		if (horaires.containsKey(arretId)) {
			return horaires.get(arretId).get(trajetId);
		}
		return null;
	}

	public Collection<ArretRoute> getArretRoutesByArretId(String arretId) {
		if (arretsRoutesByArretId.containsKey(arretId)) {
			return arretsRoutesByArretId.get(arretId).values();
		} else {
			return new ArrayList<ArretRoute>();
		}
	}

	public Ligne getLigne(String ligneId) {
		return lignes.get(ligneId);
	}

	private static GestionnaireGtfs instance = null;

	synchronized public static GestionnaireGtfs getInstance() {
		if (instance == null) {
			instance = new GestionnaireGtfs();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private GestionnaireGtfs() {
		long startTime = System.nanoTime();
		MoteurCsv moteurCsv = new MoteurCsv(CLASSES_CSV);
		Map<Class<?>, List<?>> retourMoteur = GestionZipKeolis.getAndParseZipKeolis(moteurCsv);
		for (Arret arret : (List<Arret>) retourMoteur.get(Arret.class)) {
			arrets.put(arret.id, arret);
		}
		for (ArretRoute arretRoute : (List<ArretRoute>) retourMoteur.get(ArretRoute.class)) {
			if (!arretsRoutesByArretId.containsKey(arretRoute.arretId)) {
				arretsRoutesByArretId.put(arretRoute.arretId, new HashMap<String, ArretRoute>());
			}
			arretsRoutesByArretId.get(arretRoute.arretId).put(arretRoute.ligneId, arretRoute);
		}
		for (Calendrier calendrier : (List<Calendrier>) retourMoteur.get(Calendrier.class)) {
			calendriers.put(calendrier.id, calendrier);
		}
		for (Ligne ligne : (List<Ligne>) retourMoteur.get(Ligne.class)) {
			lignes.put(ligne.id, ligne);
		}
		Map<Integer, Trajet> trajets = new HashMap<Integer, Trajet>();
		for (Trajet trajet : (List<Trajet>) retourMoteur.get(Trajet.class)) {
			trajets.put(trajet.id, trajet);
		}
		for (Ligne ligne : lignes.values()) {
			for (Horaire horaire : GestionZipKeolis.chargeLigne(moteurCsv, ligne.id)) {
				horaire.trajet = trajets.get(horaire.trajetId);
				if (!horaires.containsKey(horaire.arretId)) {
					horaires.put(horaire.arretId, new HashMap<Integer, Horaire>());
				}
				horaires.get(horaire.arretId).put(horaire.trajetId, horaire);
			}
		}
		for (Correspondance correspondance : GestionZipKeolis.getCorrespondances(moteurCsv)) {
			List<String> lignes1 = new ArrayList<String>();
			for (ArretRoute arretRoute : getArretRoutesByArretId(correspondance.arretId)) {
				lignes1.add(arretRoute.ligneId);
			}
			List<String> lignes2 = new ArrayList<String>();
			for (ArretRoute arretRoute : getArretRoutesByArretId(correspondance.correspondanceId)) {
				lignes2.add(arretRoute.ligneId);
			}

			CoupleLigne coupleLigne;
			for (String ligne1 : lignes1) {
				for (String ligne2 : lignes2) {
					coupleLigne = new CoupleLigne(ligne1, ligne2);
					if (!correspondances.containsKey(coupleLigne)) {
						correspondances.put(coupleLigne, new ArrayList<Correspondance>());
					}
					correspondances.get(coupleLigne).add(correspondance);
				}
			}
		}
		long elapsedTime = (System.nanoTime() - startTime) / 1000000;
		logger.info("Construction du gestionnaire gtfs en " + elapsedTime + " ms");
	}
}
