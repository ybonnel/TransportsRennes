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

package fr.ybo.itineraires.util;

import fr.ybo.gtfs.modele.Arret;
import fr.ybo.gtfs.modele.GestionnaireGtfs;
import fr.ybo.itineraires.modele.Adresse;
import fr.ybo.itineraires.modele.Circuit;
import fr.ybo.itineraires.modele.EnumCalendrier;
import fr.ybo.itineraires.modele.JointurePieton;
import fr.ybo.itineraires.modele.Trajet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RechercheCircuit {

	private static final double DISTANCE_RECHERCHE_ARRETS = 400;
	private final Adresse adresseDepart;
	private final Adresse adresseArrivee;
	private final Collection<JointurePieton> arretsDeparts = new ArrayList<JointurePieton>(10);
	private final Collection<JointurePieton> arretsArrivees = new ArrayList<JointurePieton>(10);

	public RechercheCircuit(Adresse adresseDepart, Adresse adresseArrivee) {
		this.adresseDepart = adresseDepart;
		this.adresseArrivee = adresseArrivee;
	}

	private final Collection<Trajet> bestTrajets = new ArrayList<Trajet>(25);

	public Iterable<Chrono> calculCircuits(EnumCalendrier calendrier, int heureDepart) {
		Collection<Chrono> chronos = new ArrayList<Chrono>(5);
		Chrono chrono = new Chrono("ArretEligibles");
		for (Arret arret : GestionnaireGtfs.getInstance().getAllArrets()) {
			double distanceDepart = calculDistanceDepart(arret);
			if (distanceDepart < DISTANCE_RECHERCHE_ARRETS) {
				arretsDeparts.add(new JointurePieton(arret, adresseDepart, distanceDepart));
			}
			double distanceArrivee = calculDistanceArrivee(arret);
			if (distanceArrivee < DISTANCE_RECHERCHE_ARRETS) {
				arretsArrivees.add(new JointurePieton(arret, adresseArrivee, distanceArrivee));
			}
		}
		chronos.add(chrono.stop());
		Chrono remplirCircuits = new Chrono("RemplirCircuits");
		List<Circuit> circuits = new ArrayList<Circuit>(50);
		for (JointurePieton arretDepart : arretsDeparts) {
			for (JointurePieton arretArrivee : arretsArrivees) {
				circuits.add(new Circuit(arretDepart, arretArrivee));
			}
		}
		chronos.add(remplirCircuits.stop());
		Chrono rechercheTrajets = new Chrono("RechercheTrajets");
		Iterator<Circuit> iteratorCircuit = circuits.iterator();
		while (iteratorCircuit.hasNext()) {
			if (iteratorCircuit.next().hasTrajetBus(calendrier, heureDepart)) {
				iteratorCircuit.remove();
			}
		}
		chronos.add(rechercheTrajets.stop());
		Chrono remplirTrajets = new Chrono("RemplirTrajets");
		List<Trajet> trajets = new ArrayList<Trajet>(50);
		for (Circuit circuit : circuits) {
			trajets.addAll(circuit.getTrajets());
		}
		chronos.add(remplirTrajets.stop());
		Chrono sortTrajets = new Chrono("SortTrajets");
		Collections.sort(trajets, new RechercheCircuit.ComparatorTrajet(heureDepart));
		chronos.add(sortTrajets.stop());
		bestTrajets.addAll(trajets.subList(0, trajets.size() > 25 ? 25 : trajets.size()));
		return chronos;
	}

	private static class ComparatorTrajet implements Comparator<Trajet> {
		private final int heureDepart;

		private ComparatorTrajet(int heureDepart) {
			this.heureDepart = heureDepart;
		}

		public int compare(Trajet o1, Trajet o2) {
			int tempsTrajet1 = o1.calculTempsTrajet(heureDepart);
			int tempsTrajet2 = o2.calculTempsTrajet(heureDepart);
			if (tempsTrajet1 < tempsTrajet2) {
				return -1;
			}
			if (tempsTrajet1 > tempsTrajet2) {
				return 1;
			}
			int tempsTrajetPieton1 = o1.calculTempsTrajetPieton();
			int tempsTrajetPieton2 = o2.calculTempsTrajetPieton();
			return tempsTrajetPieton1 < tempsTrajetPieton2 ? -1 : tempsTrajetPieton1 == tempsTrajetPieton2 ? 0 : 1;
		}
	}

	public Collection<Trajet> getBestTrajets() {
		return bestTrajets;
	}

	private double calculDistanceDepart(Arret arret) {
		return new CalculDistance(adresseDepart.getLatitude(), adresseDepart.getLongitude(), arret.latitude, arret.longitude).calculDistance();
	}

	private double calculDistanceArrivee(Arret arret) {
		return new CalculDistance(adresseArrivee.getLatitude(), adresseArrivee.getLongitude(), arret.latitude, arret.longitude).calculDistance();
	}

}
