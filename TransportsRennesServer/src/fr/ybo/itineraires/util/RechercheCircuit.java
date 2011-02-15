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

	public RechercheCircuit(final Adresse adresseDepart, final Adresse adresseArrivee) {
		super();
		this.adresseDepart = adresseDepart;
		this.adresseArrivee = adresseArrivee;
	}

	private final Collection<Trajet> bestTrajets = new ArrayList<Trajet>(3);

	public Iterable<Chrono> calculCircuits(final EnumCalendrier calendrier, final int heureDepart) {
		final Collection<Chrono> chronos = new ArrayList<Chrono>(5);
		Chrono chrono = new Chrono("ArretEligibles");
		for (final Arret arret : GestionnaireGtfs.getInstance().getAllArrets()) {
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
		final List<Circuit> circuits = new ArrayList<Circuit>(50);
		for (final JointurePieton arretDepart : arretsDeparts) {
			for (final JointurePieton arretArrivee : arretsArrivees) {
				circuits.add(new Circuit(arretDepart, arretArrivee));
			}
		}
		chronos.add(remplirCircuits.stop());
		remplirCircuits = new Chrono("RechercheTrajets");
		final Iterator<Circuit> iteratorCircuit = circuits.iterator();
		while (iteratorCircuit.hasNext()) {
			if (iteratorCircuit.next().rechercheTrajetBus(calendrier, heureDepart)) {
				iteratorCircuit.remove();
			}
		}
		chronos.add(remplirCircuits.stop());
		remplirCircuits = new Chrono("RemplirTrajets");
		final List<Trajet> trajets = new ArrayList<Trajet>(50);
		for (final Circuit circuit : circuits) {
			trajets.addAll(circuit.getTrajets());
		}
		chronos.add(remplirCircuits.stop());
		remplirCircuits = new Chrono("SortTrajets");
		Collections.sort(trajets, new ComparatorTrajet(heureDepart));
		chronos.add(remplirCircuits.stop());
		bestTrajets.addAll(trajets.subList(0, trajets.size() > 3 ? 3 : trajets.size()));
		return chronos;
	}

	private class ComparatorTrajet implements Comparator<Trajet> {
		private final int heureDepart;

		private ComparatorTrajet(final int heureDepart) {
			super();
			this.heureDepart = heureDepart;
		}

		public int compare(final Trajet o1, final Trajet o2) {
			final int tempsTrajet1 = o1.calculTempsTrajet(heureDepart);
			final int tempsTrajet2 = o2.calculTempsTrajet(heureDepart);
			return tempsTrajet1 < tempsTrajet2 ? -1 : tempsTrajet1 == tempsTrajet2 ? 0 : 1;
		}
	}

	public Collection<Trajet> getBestTrajets() {
		return bestTrajets;
	}

	private double calculDistanceDepart(final Arret arret) {
		return new CalculDistance(adresseDepart.getLatitude(), adresseDepart.getLongitude(), arret.latitude, arret.longitude).calculDistance();
	}

	private double calculDistanceArrivee(final Arret arret) {
		return new CalculDistance(adresseArrivee.getLatitude(), adresseArrivee.getLongitude(), arret.latitude, arret.longitude).calculDistance();
	}

}
