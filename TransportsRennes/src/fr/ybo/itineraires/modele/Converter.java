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

public class Converter {

	public static ItineraireReponse convert(fr.ybo.itineraires.schema.ItineraireReponse reponseJson) {
		ItineraireReponse reponse = new ItineraireReponse();
		reponse.setErreur(reponseJson.getErreur());
		reponse.setAdresseArrivee(convert(reponseJson.getAdresseArrivee()));
		reponse.setAdresseDepart(convert(reponseJson.getAdresseDepart()));
		for (fr.ybo.itineraires.schema.Trajet trajetJson : reponseJson.getTrajets()) {
			reponse.getTrajets().add(convert(trajetJson));
		}
		return reponse;
	}

	private static Adresse convert(fr.ybo.itineraires.schema.Adresse adresseJson) {
		if (adresseJson == null) {
			return null;
		}
		Adresse adresse = new Adresse();
		adresse.latitude = adresseJson.getLatitude();
		adresse.longitude = adresseJson.getLongitude();
		return adresse;
	}

	private static Trajet convert(fr.ybo.itineraires.schema.Trajet trajetJson) {
		if (trajetJson == null) {
			return null;
		}
		Trajet trajet = new Trajet();
		for (fr.ybo.itineraires.schema.PortionTrajet portion : trajetJson.getPortions()) {
			trajet.getPortions().add(convert(portion));
		}
		return trajet;
	}

	private static PortionTrajet convert(fr.ybo.itineraires.schema.PortionTrajet portionJson) {
		if (portionJson == null) {
			return null;
		}
		PortionTrajet portion = new PortionTrajet();
		portion.setJointureCorrespondance(convert(portionJson.getJointureCorrespondance()));
		portion.setJointurePieton(convert(portionJson.getJointurePieton()));
		portion.setPortionTrajetBus(convert(portionJson.getPortionTrajetBus()));
		return portion;
	}

	private static JointureCorrespondance convert(fr.ybo.itineraires.schema.JointureCorrespondance jointureJson) {
		if (jointureJson == null) {
			return null;
		}
		JointureCorrespondance jointure = new JointureCorrespondance();
		jointure.setArretArriveeId(jointureJson.getArretArriveeId());
		jointure.setArretDepartId(jointureJson.getArretDepartId());
		jointure.setTempsTrajet(jointureJson.getTempsTrajet());
		return jointure;
	}

	private static JointurePieton convert(fr.ybo.itineraires.schema.JointurePieton jointureJson) {
		if (jointureJson == null) {
			return null;
		}
		JointurePieton jointure = new JointurePieton();
		jointure.setAdresse(convert(jointureJson.getAdresse()));
		jointure.setArretId(jointureJson.getArretId());
		jointure.setTempsTrajet(jointureJson.getTempsTrajet());
		return jointure;
	}

	private static PortionTrajetBus convert(fr.ybo.itineraires.schema.PortionTrajetBus jointureJson) {
		if (jointureJson == null) {
			return null;
		}
		PortionTrajetBus jointure = new PortionTrajetBus();
		jointure.setHeureArrivee(jointureJson.getHeureArrivee());
		jointure.setArretArriveeId(jointureJson.getArretArriveeId());
		jointure.setArretDepartId(jointureJson.getArretDepartId());
		jointure.setHeureDepart(jointureJson.getHeureDepart());
		jointure.setLigneId(jointureJson.getLigneId());
		jointure.setDirectionId(jointureJson.getDirectionId());
		jointure.setMacroDirection(jointureJson.getMacroDirection());
		return jointure;
	}
}
