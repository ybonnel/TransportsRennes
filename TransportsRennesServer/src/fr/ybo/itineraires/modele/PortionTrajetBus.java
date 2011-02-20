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
import fr.ybo.gtfs.modele.Calendrier;
import fr.ybo.gtfs.modele.GestionnaireGtfs;
import fr.ybo.gtfs.modele.Horaire;
import fr.ybo.gtfs.modele.Ligne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class PortionTrajetBus extends PortionTrajet {

	private static final Logger LOGGER = Logger.getLogger(PortionTrajetBus.class.getName());

	private static final GestionnaireGtfs GESTIONNAIRE_GTFS = GestionnaireGtfs.getInstance();

	private static final int UNE_JOURNEE = 24 * 60;
	private static final int DEUX_HEURES = 2 * 60;

	private static class ComparatorHoraires implements Comparator<PortionTrajetBus.HorairePortion> {

		private final int heureDepart;

		private ComparatorHoraires(int heureDepart) {
			this.heureDepart = heureDepart;
		}

		public int compare(PortionTrajetBus.HorairePortion o1, PortionTrajetBus.HorairePortion o2) {
			if (o1.getHeureDepart() < heureDepart && o2.getHeureDepart() >= heureDepart) {
				return 1;
			}
			if (o1.getHeureDepart() >= heureDepart && o2.getHeureDepart() < heureDepart) {
				return -1;
			}
			return o1.getHeureDepart().compareTo(o2.getHeureDepart());
		}
	}

	@Override
	public int calculHeureArrivee(int heureDepart) {
		if (horaireSelectionnee == null) {
			if (horaires.isEmpty()) {
				LOGGER.warning("Horaires vide pour le trajet : " + toString());
			}
			Collections.sort(horaires, new PortionTrajetBus.ComparatorHoraires(heureDepart));
			horaireSelectionnee = horaires.get(0);
			if (horaireSelectionnee.getHeureDepart() < heureDepart) {
				horaireSelectionnee.annule();
			}
			horaires.clear();
		}
		return horaireSelectionnee.getHeureArrivee();
	}

	private static class HorairePortion {
		private Integer heureDepart;
		private Integer heureArrivee;

		private HorairePortion(Integer heureDepart, Integer heureArrivee) {
			this.heureDepart = heureDepart;
			this.heureArrivee = heureArrivee;
		}

		public Integer getHeureDepart() {
			return heureDepart;
		}

		public Integer getHeureArrivee() {
			return heureArrivee;
		}

		public void annule() {
			heureDepart = 9999;
			heureArrivee = 9999;
		}
	}

	private final Arret arretDepart;
	private final Arret arretArrivee;
	private final Ligne ligne;
	private final List<PortionTrajetBus.HorairePortion> horaires = new ArrayList<PortionTrajetBus.HorairePortion>(10);
	private PortionTrajetBus.HorairePortion horaireSelectionnee;

	public PortionTrajetBus(Arret arretDepart, Arret arretArrivee, Ligne ligne) {
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
		this.ligne = ligne;
	}

	public boolean hasHoraire(EnumCalendrier calendrier, int heureDepart) {
		// Recupérer les trajets de l'arret/ligne
		int heureDepartFin = heureDepart + DEUX_HEURES;
		for (Horaire horaire : GESTIONNAIRE_GTFS.getHorairesByArretId(arretDepart.id)) {
			if (ligne.id.equals(horaire.trajet.ligneId) && (horaire.heureDepart >= heureDepart && horaire.heureDepart <= heureDepartFin ||
					horaire.heureDepart - UNE_JOURNEE >= heureDepart && horaire.heureDepart - UNE_JOURNEE <= heureDepartFin)) {
				Calendrier calendrierCourant = GESTIONNAIRE_GTFS.getCalendrier(horaire.trajet.calendrierId);
				Horaire horaireArrivee = GESTIONNAIRE_GTFS.getHoraireByArretIdAndTrajetId(arretArrivee.id, horaire.trajetId);
				if (horaireArrivee != null && horaireArrivee.heureDepart > horaire.heureDepart) {
					if (horaire.heureDepart >= heureDepart && calendrier.isCalendrierValide(calendrierCourant)) {
						horaires.add(new PortionTrajetBus.HorairePortion(horaire.heureDepart, horaireArrivee.heureDepart));
					}
					if (horaire.heureDepart - UNE_JOURNEE >= heureDepart && calendrier.veille().isCalendrierValide(calendrierCourant)) {
						horaires.add(
								new PortionTrajetBus.HorairePortion(horaire.heureDepart - UNE_JOURNEE, horaireArrivee.heureDepart - UNE_JOURNEE));
					}

				}
			}
		}
		return !horaires.isEmpty();
	}

	private String formatHeure(int time) {
		StringBuilder stringBuilder = new StringBuilder();
		int heure = time / 60;
		int minutes = time - heure * 60;
		if (heure < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(heure);
		stringBuilder.append(':');
		if (minutes < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(minutes);
		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Portion en bus :\n");
		stringBuilder.append("\tLigne :");
		stringBuilder.append(ligne.id);
		stringBuilder.append("\n\tDépart (");
		if (horaireSelectionnee != null) {
			stringBuilder.append(formatHeure(horaireSelectionnee.getHeureDepart()));
		}
		stringBuilder.append(") : ");
		stringBuilder.append(arretDepart.nom);
		stringBuilder.append("\n\tArrivee (");
		if (horaireSelectionnee != null) {
			stringBuilder.append(formatHeure(horaireSelectionnee.getHeureArrivee()));
		}
		stringBuilder.append(") : ");
		stringBuilder.append(arretArrivee.nom);
		return stringBuilder.toString();
	}

	@Override
	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<ligneId>");
		stringBuilder.append(ligne.id);
		stringBuilder.append("</ligneId>");
		stringBuilder.append("<arretDepartId>");
		stringBuilder.append(arretDepart.id);
		stringBuilder.append("</arretDepartId>");
		if (horaireSelectionnee != null) {
			stringBuilder.append("<heureDepart>");
			stringBuilder.append(formatHeure(horaireSelectionnee.getHeureDepart()));
			stringBuilder.append("</heureDepart>");
		}
		stringBuilder.append("<arretArriveeId>");
		stringBuilder.append(arretArrivee.id);
		stringBuilder.append("</arretArriveeId>");
		if (horaireSelectionnee != null) {
			stringBuilder.append("<heureArrivee>");
			stringBuilder.append(formatHeure(horaireSelectionnee.getHeureArrivee()));
			stringBuilder.append("</heureArrivee>");
		}
		return stringBuilder.toString();
	}

	@Override
	public fr.ybo.itineraires.schema.PortionTrajet convert() {
		fr.ybo.itineraires.schema.PortionTrajetBus retour = new fr.ybo.itineraires.schema.PortionTrajetBus();
		retour.setLigneId(ligne.id);
		retour.setArretDepartId(arretDepart.id);
		retour.setHeureDepart(formatHeure(horaireSelectionnee.getHeureDepart()));
		retour.setArretArriveeId(arretArrivee.id);
		retour.setHeureArrivee(formatHeure(horaireSelectionnee.getHeureArrivee()));
        fr.ybo.itineraires.schema.PortionTrajet portionTrajet = new fr.ybo.itineraires.schema.PortionTrajet();
        portionTrajet.setPortionTrajetBus(retour);
		return portionTrajet;
	}
}
