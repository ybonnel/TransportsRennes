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

import fr.ybo.gtfs.modele.Arret;
import fr.ybo.gtfs.modele.ArretRoute;
import fr.ybo.gtfs.modele.GestionnaireGtfs;
import fr.ybo.itineraires.util.RechercheCircuit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Circuit {

	private JointurePieton arretDepart;
	private JointurePieton arretArrivee;
	private List<Trajet> trajets = new ArrayList<Trajet>();

	public Circuit(JointurePieton arretDepart, JointurePieton arretArrivee) {
		super();
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
	}

	public List<Trajet> getTrajets() {
		return trajets;
	}

	private Map<String, Collection<Arret>> arretsByLigneId = new HashMap<String, Collection<Arret>>();

	public boolean rechercheTrajetBus(EnumCalendrier calendrier, int heureDepart) {
		Set<String> lignesDepart = new HashSet<String>();
		for (ArretRoute arretRoute : GestionnaireGtfs.getInstance().getArretRoutesByArretId(arretDepart.getArret().id)) {
			if (!lignesDepart.contains(arretRoute.ligneId)) {
				if (!arretsByLigneId.containsKey(arretRoute.ligneId)) {
					arretsByLigneId.put(arretRoute.ligneId, GestionnaireGtfs.getInstance().getArretsByLigneId(arretRoute.ligneId));
				}
				lignesDepart.add(arretRoute.ligneId);
			}
		}
		Set<String> lignesArrivee = new HashSet<String>();
		for (ArretRoute arretRoute : GestionnaireGtfs.getInstance().getArretRoutesByArretId(arretArrivee.getArret().id)) {
			if (!lignesArrivee.contains(arretRoute.ligneId)) {
				if (!arretsByLigneId.containsKey(arretRoute.ligneId)) {
					arretsByLigneId.put(arretRoute.ligneId, GestionnaireGtfs.getInstance().getArretsByLigneId(arretRoute.ligneId));
				}
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
							GestionnaireGtfs.getInstance().getLigne(ligneDepartId));
					if (bus.rechercheHoraire(calendrier, heureDepart)) {
						trajet.getPortionsTrajet().add(arretDepart);
						trajet.getPortionsTrajet().add(bus);
						trajet.getPortionsTrajet().add(arretArrivee);
						trajets.add(trajet);
					}
				} else {
					// Trajets avec une correspondance
					for (Arret arretDepartLigne : arretsByLigneId.get(ligneDepartId)) {
						for (Arret arretArriveeLigne : arretsByLigneId.get(ligneArriveeId)) {
							if (GestionnaireGtfs.getInstance().getCorrespondances(arretDepartLigne.id).contains(arretArriveeLigne.id)) {
								// Premier bus.
								PortionTrajetBus bus1 = new PortionTrajetBus(arretDepart.getArret(), arretDepartLigne,
										GestionnaireGtfs.getInstance().getLigne(ligneDepartId));
								// Dexuième bus
								PortionTrajetBus bus2 = new PortionTrajetBus(arretArriveeLigne, arretArrivee.getArret(),
										GestionnaireGtfs.getInstance().getLigne(ligneArriveeId));

								if (bus1.rechercheHoraire(calendrier, heureDepart) && bus2.rechercheHoraire(calendrier, heureDepart)) {
									Trajet trajet = new Trajet();
									trajet.getPortionsTrajet().add(arretDepart);
									trajet.getPortionsTrajet().add(bus1);
									// Correspondance
									trajet.getPortionsTrajet().add(new JointureCorrespondance(arretDepartLigne, arretArriveeLigne,
											RechercheCircuit.calculDistanceBetweenArrets(arretDepartLigne, arretArriveeLigne)));
									trajet.getPortionsTrajet().add(bus2);
									trajet.getPortionsTrajet().add(arretArrivee);
									trajets.add(trajet);
								}
							}
						}
					}
				}
			}
		}

		return !trajets.isEmpty();
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
