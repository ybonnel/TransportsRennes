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

import fr.ybo.gtfs.modele.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class PortionTrajetBus extends PortionTrajet {

	private static final Logger logger = Logger.getLogger(PortionTrajetBus.class.getName());

	private static final int UNE_JOURNEE = 24 * 60;

	private class ComparatorHoraires implements Comparator<HorairePortion> {

		private int heureDepart;

		private ComparatorHoraires(int heureDepart) {
			this.heureDepart = heureDepart;
		}
		public int compare(HorairePortion o1, HorairePortion o2) {
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
				logger.warning("Horaires vide pour le trajet : " + toString());
			}
			Collections.sort(horaires, new ComparatorHoraires(heureDepart));
			horaireSelectionnee = horaires.get(0);
			if (horaireSelectionnee.getHeureDepart() < heureDepart ) {
				horaireSelectionnee.annule();
			}
			horaires.clear();
		}
		return horaireSelectionnee.getHeureArrivee();
	}

	private class HorairePortion {
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

	private Arret arretDepart;
	private Arret arretArrivee;
	private Ligne ligne;
	private List<HorairePortion> horaires = new ArrayList<HorairePortion>();
	private HorairePortion horaireSelectionnee;

	public PortionTrajetBus(Arret arretDepart, Arret arretArrivee, Ligne ligne) {
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
		this.ligne = ligne;
	}

	public boolean rechercheHoraire(EnumCalendrier calendrier, int heureDepart) {
		// Recupérer les trajets de l'arret/ligne
		for (Horaire horaire : GestionnaireGtfs.getInstance().getHorairesByArretId(arretDepart.id)) {
			if (ligne.id.equals(GestionnaireGtfs.getInstance().getTrajet(horaire.trajetId).ligneId)) {
				Calendrier calendrierCourant =
						GestionnaireGtfs.getInstance().getCalendrier(GestionnaireGtfs.getInstance().getTrajet(horaire.trajetId).calendrierId);
				Horaire horaireArrivee = GestionnaireGtfs.getInstance().getHoraireByArretIdAndTrajetId(arretArrivee.id, horaire.trajetId);
				if (horaireArrivee != null && horaireArrivee.heureDepart > horaire.heureDepart) {
					if (horaire.heureDepart >= heureDepart && calendrier.isCalendrierValide(calendrierCourant)) {
						horaires.add(new HorairePortion(horaire.heureDepart, horaireArrivee.heureDepart));
					}
					if ((horaire.heureDepart - UNE_JOURNEE) >= heureDepart && calendrier.veille().isCalendrierValide(calendrierCourant)) {
						horaires.add(new HorairePortion(horaire.heureDepart - UNE_JOURNEE, horaireArrivee.heureDepart - UNE_JOURNEE));
					}

				}
			}
		}
		return !horaires.isEmpty();
	}

	private String formatHeure(int time) {
		StringBuilder stringBuilder = new StringBuilder();
		int heure = time / 60;
		int minutes = time - (heure * 60);
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
}
