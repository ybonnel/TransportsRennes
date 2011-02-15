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

	public static class CoupleLigne {
		public final String ligneId1;
		public final String ligneId2;

		public CoupleLigne(final String ligneId1, final String ligneId2) {
			super();
			this.ligneId1 = ligneId1;
			this.ligneId2 = ligneId2;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}

			final GestionnaireGtfs.CoupleLigne that = (GestionnaireGtfs.CoupleLigne) obj;

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


	private final Map<String, Arret> arrets = new HashMap<String, Arret>(1500);
	private final Map<String, Map<String, ArretRoute>> arretsRoutesByArretId = new HashMap<String, Map<String, ArretRoute>>(1500);
	private final Map<Integer, Calendrier> calendriers = new HashMap<Integer, Calendrier>(20);
	private final Map<String, Map<Integer, Horaire>> horaires = new HashMap<String, Map<Integer, Horaire>>(1500);
	private final Map<String, Ligne> lignes = new HashMap<String, Ligne>(67);
	private final Map<GestionnaireGtfs.CoupleLigne, Collection<Correspondance>> correspondances = new HashMap<GestionnaireGtfs.CoupleLigne, Collection<Correspondance>>(200);

	public Collection<Correspondance> getCorrespondances(final GestionnaireGtfs.CoupleLigne coupleLigne) {
		return correspondances.get(coupleLigne);
	}

	public Calendrier getCalendrier(final Integer calendrierId) {
		return calendriers.get(calendrierId);
	}

	public Iterable<Arret> getAllArrets() {
		return arrets.values();
	}

	public Arret getArret(final String arretId) {
		return arrets.get(arretId);
	}

	public Iterable<Horaire> getHorairesByArretId(final String arretId) {
		if (horaires.containsKey(arretId)) {
			return horaires.get(arretId).values();
		}
		return new ArrayList<Horaire>(0);
	}

	public Horaire getHoraireByArretIdAndTrajetId(final String arretId, final Integer trajetId) {
		if (horaires.containsKey(arretId)) {
			return horaires.get(arretId).get(trajetId);
		}
		return null;
	}

	public final Iterable<ArretRoute> getArretRoutesByArretId(final String arretId) {
		return arretsRoutesByArretId.containsKey(arretId) ? arretsRoutesByArretId.get(arretId).values() : new ArrayList<ArretRoute>();
	}

	public Ligne getLigne(final String ligneId) {
		return lignes.get(ligneId);
	}

	@SuppressWarnings({"StaticNonFinalField"})
	private static GestionnaireGtfs instance;

	public static synchronized GestionnaireGtfs getInstance() {
		if (instance == null) {
			instance = new GestionnaireGtfs();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private GestionnaireGtfs() {
		super();
		final long startTime = System.nanoTime();
		final MoteurCsv moteurCsv = new MoteurCsv(CLASSES_CSV);
		final Map<Class<?>, List<?>> retourMoteur = GestionZipKeolis.getAndParseZipKeolis(moteurCsv);
		for (final Arret arret : (List<Arret>) retourMoteur.get(Arret.class)) {
			arrets.put(arret.id, arret);
		}
		for (final ArretRoute arretRoute : (List<ArretRoute>) retourMoteur.get(ArretRoute.class)) {
			if (!arretsRoutesByArretId.containsKey(arretRoute.arretId)) {
				arretsRoutesByArretId.put(arretRoute.arretId, new HashMap<String, ArretRoute>(100));
			}
			arretsRoutesByArretId.get(arretRoute.arretId).put(arretRoute.ligneId, arretRoute);
		}
		for (final Calendrier calendrier : (List<Calendrier>) retourMoteur.get(Calendrier.class)) {
			calendriers.put(calendrier.id, calendrier);
		}
		for (final Ligne ligne : (List<Ligne>) retourMoteur.get(Ligne.class)) {
			lignes.put(ligne.id, ligne);
		}
		final Map<Integer, Trajet> trajets = new HashMap<Integer, Trajet>(2500);
		for (final Trajet trajet : (List<Trajet>) retourMoteur.get(Trajet.class)) {
			trajets.put(trajet.id, trajet);
		}
		for (final Ligne ligne : lignes.values()) {
			for (final Horaire horaire : GestionZipKeolis.chargeLigne(moteurCsv, ligne.id)) {
				horaire.trajet = trajets.get(horaire.trajetId);
				if (!horaires.containsKey(horaire.arretId)) {
					horaires.put(horaire.arretId, new HashMap<Integer, Horaire>(100));
				}
				horaires.get(horaire.arretId).put(horaire.trajetId, horaire);
			}
		}
		for (final Correspondance correspondance : GestionZipKeolis.getCorrespondances(moteurCsv)) {
			final Collection<String> lignes1 = new ArrayList<String>(10);
			for (final ArretRoute arretRoute : getArretRoutesByArretId(correspondance.arretId)) {
				lignes1.add(arretRoute.ligneId);
			}
			final Collection<String> lignes2 = new ArrayList<String>(10);
			for (final ArretRoute arretRoute : getArretRoutesByArretId(correspondance.correspondanceId)) {
				lignes2.add(arretRoute.ligneId);
			}

			for (final String ligne1 : lignes1) {
				for (final String ligne2 : lignes2) {
					CoupleLigne coupleLigne = new CoupleLigne(ligne1, ligne2);
					if (!correspondances.containsKey(coupleLigne)) {
						correspondances.put(coupleLigne, new ArrayList<Correspondance>(10));
					}
					correspondances.get(coupleLigne).add(correspondance);
				}
			}
		}
		final long elapsedTime = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info("Construction du gestionnaire gtfs en " + elapsedTime + " ms");
	}
}
