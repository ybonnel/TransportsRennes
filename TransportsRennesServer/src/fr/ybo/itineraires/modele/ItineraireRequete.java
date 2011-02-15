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

import fr.ybo.itineraires.bean.ItineraireException;

import java.util.Calendar;

public class ItineraireRequete {

	private Adresse adresseDepart;
	private Adresse adresseArrivee;
	private EnumCalendrier calendrier;
	private int heureDepart;

	public ItineraireRequete(Adresse adresseDepart, Adresse adresseArrivee, EnumCalendrier calendrier, Integer heureDepart) {
		if (adresseDepart == null || adresseArrivee == null) {
			throw new ItineraireException("L'adresse de départ et d'arrivee devrait être valorisées");
		}
		this.adresseDepart = adresseDepart;
		this.adresseArrivee = adresseArrivee;
		if (calendrier == null) {
			Calendar calendar = Calendar.getInstance();
			this.calendrier = EnumCalendrier.fromFieldCalendar(calendar.get(Calendar.DAY_OF_WEEK));
		} else {
			this.calendrier = calendrier;
		}
		if (heureDepart == null) {
			Calendar calendar = Calendar.getInstance();
			this.heureDepart = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		} else {
			this.heureDepart = heureDepart;
		}
	}

	public Adresse getAdresseDepart() {
		return adresseDepart;
	}

	public void setAdresseDepart(Adresse adresseDepart) {
		this.adresseDepart = adresseDepart;
	}

	public Adresse getAdresseArrivee() {
		return adresseArrivee;
	}

	public void setAdresseArrivee(Adresse adresseArrivee) {
		this.adresseArrivee = adresseArrivee;
	}

	public EnumCalendrier getCalendrier() {
		return calendrier;
	}

	public void setCalendrier(EnumCalendrier calendrier) {
		this.calendrier = calendrier;
	}

	public int getHeureDepart() {
		return heureDepart;
	}

	public void setHeureDepart(int heureDepart) {
		this.heureDepart = heureDepart;
	}
}
