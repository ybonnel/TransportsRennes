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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GestionnaireGtfs {

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
	}


	private Map<String, Arret> arrets = new HashMap<String, Arret>();
	private Map<String, Map<String, ArretRoute>> arretsRoutesByLigneId = new HashMap<String, Map<String, ArretRoute>>();
	private Map<String, Map<String, ArretRoute>> arretsRoutesByArretId = new HashMap<String, Map<String, ArretRoute>>();
	private Map<Integer, Calendrier> calendriers = new HashMap<Integer, Calendrier>();
	private Map<Integer, Direction> directions = new HashMap<Integer, Direction>();
	private Map<String, Map<Integer, Horaire>> horaires = new HashMap<String, Map<Integer, Horaire>>();
	private Map<String, Ligne> lignes = new HashMap<String, Ligne>();
	private Map<Integer, Trajet> trajets = new HashMap<Integer, Trajet>();

	public Collection<Arret> getAllArrets() {
		return arrets.values();
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

	private Collection<ArretRoute> getArretRoutesByLigneId(String ligneId) {
		if (arretsRoutesByLigneId.containsKey(ligneId)) {
			return arretsRoutesByLigneId.get(ligneId).values();
		} else {
			return new ArrayList<ArretRoute>();
		}
	}

	public Collection<Arret> getArretsByLigneId(String ligneId) {
		List<Arret> arretsRetour = new ArrayList<Arret>();
		for (ArretRoute arretRoute : getArretRoutesByLigneId(ligneId)) {
			arretsRetour.add(arrets.get(arretRoute.arretId));
		}
		return arretsRetour;
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
			if (!arretsRoutesByLigneId.containsKey(arretRoute.ligneId)) {
				arretsRoutesByLigneId.put(arretRoute.ligneId, new HashMap<String, ArretRoute>());
			}
			arretsRoutesByLigneId.get(arretRoute.ligneId).put(arretRoute.arretId, arretRoute);
			if (!arretsRoutesByArretId.containsKey(arretRoute.arretId)) {
				arretsRoutesByArretId.put(arretRoute.arretId, new HashMap<String, ArretRoute>());
			}
			arretsRoutesByArretId.get(arretRoute.arretId).put(arretRoute.ligneId, arretRoute);
		}
		for (Calendrier calendrier : (List<Calendrier>) retourMoteur.get(Calendrier.class)) {
			calendriers.put(calendrier.id, calendrier);
		}
		for (Direction direction : (List<Direction>) retourMoteur.get(Direction.class)) {
			directions.put(direction.id, direction);
		}
		for (Ligne ligne : (List<Ligne>) retourMoteur.get(Ligne.class)) {
			lignes.put(ligne.id, ligne);
		}
		for (Trajet trajet : (List<Trajet>) retourMoteur.get(Trajet.class)) {
			trajets.put(trajet.id, trajet);
		}
		for (Ligne ligne : lignes.values()) {
			for (Horaire horaire : GestionZipKeolis.chargeLigne(moteurCsv, ligne.id)) {
				if (!horaires.containsKey(horaire.arretId)) {
					horaires.put(horaire.arretId, new HashMap<Integer, Horaire>());
				}
				horaires.get(horaire.arretId).put(horaire.trajetId, horaire);
			}
		}
		long elapsedTime = (System.nanoTime() - startTime) / 1000000;
		logger.info("Construction du gestionnaire gtfs en " + elapsedTime + " ms");
	}
}
