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

import java.util.*;
import java.util.logging.Logger;

public class Circuit {

	private static final Logger logger = Logger.getLogger(Circuit.class.getName());

	private static double DISTANCE_CORRESPONDANCE = 100.0;

	private JointurePieton arretDepart;
	private JointurePieton arretArrivee;
	private List<Trajet> trajets = new ArrayList<Trajet>();

	public Circuit(JointurePieton arretDepart, JointurePieton arretArrivee) {
		super();
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
	}

	private Map<String, Collection<Arret>> arretsByLigneId = new HashMap<String, Collection<Arret>>();

	public boolean rechercheTrajetBus() {
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
					trajet.getPortionsTrajet().add(new PortionTrajetBus(arretDepart.getArret(), arretArrivee.getArret(),
							GestionnaireGtfs.getInstance().getLigne(ligneDepartId)));

					trajets.add(trajet);
				} else {
					// Trajets avec une correspondance
					for (Arret arretDepartLigne : arretsByLigneId.get(ligneDepartId)) {
						for (Arret arretArriveeLigne : arretsByLigneId.get(ligneArriveeId)) {
							double distance = RechercheCircuit.calculDistanceBetweenArrets(arretDepartLigne, arretArriveeLigne);
							if (distance < DISTANCE_CORRESPONDANCE) {
								Trajet trajet = new Trajet();
								// Premier bus.
								trajet.getPortionsTrajet().add(new PortionTrajetBus(arretDepart.getArret(), arretDepartLigne,
										GestionnaireGtfs.getInstance().getLigne(ligneDepartId)));
								// Correspondance
								trajet.getPortionsTrajet().add(new JointureCorrespondance(arretDepartLigne, arretArriveeLigne, distance));
								// Dexuième bus
								trajet.getPortionsTrajet().add(new PortionTrajetBus(arretArriveeLigne, arretArrivee.getArret(),
										GestionnaireGtfs.getInstance().getLigne(ligneArriveeId)));
								trajets.add(trajet);
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
