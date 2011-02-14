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

package fr.ybo.itineraires.modele;

import fr.ybo.gtfs.modele.ArretRoute;
import fr.ybo.gtfs.modele.Correspondance;
import fr.ybo.gtfs.modele.GestionnaireGtfs;

import java.util.*;

public class Circuit {
	private static final GestionnaireGtfs GESTIONNAIRE_GTFS = GestionnaireGtfs.getInstance();

	private final JointurePieton arretDepart;
	private final JointurePieton arretArrivee;
	private final List<Trajet> trajets = new ArrayList<Trajet>();

	public Circuit(JointurePieton arretDepart, JointurePieton arretArrivee) {
		super();
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
	}

	public List<Trajet> getTrajets() {
		return trajets;
	}

	public boolean rechercheTrajetBus(EnumCalendrier calendrier, int heureDepart) {
		Set<String> lignesDepart = new HashSet<String>();
		for (ArretRoute arretRoute : GESTIONNAIRE_GTFS.getArretRoutesByArretId(arretDepart.getArret().id)) {
			if (!lignesDepart.contains(arretRoute.ligneId)) {
				lignesDepart.add(arretRoute.ligneId);
			}
		}
		Set<String> lignesArrivee = new HashSet<String>();
		for (ArretRoute arretRoute : GESTIONNAIRE_GTFS.getArretRoutesByArretId(arretArrivee.getArret().id)) {
			if (!lignesArrivee.contains(arretRoute.ligneId)) {
				lignesArrivee.add(arretRoute.ligneId);
			}
		}
		// Calcul des trajets possibles.
		trajets.clear();
		for (String ligneDepartId : lignesDepart) {
			for (String ligneArriveeId : lignesArrivee) {
				// Trajets sans correspondance
				if (ligneDepartId.equals(ligneArriveeId)) {
					Trajet trajet = new Trajet();
					PortionTrajetBus bus = new PortionTrajetBus(arretDepart.getArret(), arretArrivee.getArret(),
							GESTIONNAIRE_GTFS.getLigne(ligneDepartId));
					if (bus.rechercheHoraire(calendrier, heureDepart)) {
						trajet.getPortionsTrajet().add(arretDepart);
						trajet.getPortionsTrajet().add(bus);
						trajet.getPortionsTrajet().add(arretArrivee);
						trajets.add(trajet);
					}
				} else {
					// Trajets avec une correspondance
					Collection<Correspondance> correspondances =
							GESTIONNAIRE_GTFS.getCorrespondances(new GestionnaireGtfs.CoupleLigne(ligneDepartId, ligneArriveeId));
					if (correspondances != null) {
						for (Correspondance correspondance : correspondances) {
							// Premier bus.
							PortionTrajetBus bus1 =
									new PortionTrajetBus(arretDepart.getArret(), GESTIONNAIRE_GTFS.getArret(correspondance.arretId),
											GESTIONNAIRE_GTFS.getLigne(ligneDepartId));
							// Dexuième bus
							PortionTrajetBus bus2 = new PortionTrajetBus(GESTIONNAIRE_GTFS.getArret(correspondance.correspondanceId),
									arretArrivee.getArret(), GESTIONNAIRE_GTFS.getLigne(ligneArriveeId));

							if (bus1.rechercheHoraire(calendrier, heureDepart) && bus2.rechercheHoraire(calendrier, heureDepart)) {
								Trajet trajet = new Trajet();
								trajet.getPortionsTrajet().add(arretDepart);
								trajet.getPortionsTrajet().add(bus1);
								// Correspondance
								trajet.getPortionsTrajet()
										.add(new JointureCorrespondance(GESTIONNAIRE_GTFS.getArret(correspondance.arretId),
												GESTIONNAIRE_GTFS.getArret(correspondance.correspondanceId), correspondance.distance));
								trajet.getPortionsTrajet().add(bus2);
								trajet.getPortionsTrajet().add(arretArrivee);
								trajets.add(trajet);
							}
						}
					}
				}
			}
		}

		return trajets.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Circuit :\n");
		for (Trajet trajet : trajets) {
			stringBuilder.append(trajet.toString());
		}
		return stringBuilder.toString();
	}
}
