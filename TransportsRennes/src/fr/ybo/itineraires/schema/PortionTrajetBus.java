package fr.ybo.itineraires.schema;

import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;

@SuppressWarnings({"serial"})
public class PortionTrajetBus extends PortionTrajet {
	public String ligneId;
	public String arretDepartId;
	public String heureDepart;
	public String arretArriveeId;
	public String heureArrivee;

	private Ligne ligne = null;
	private Arret arretDepart = null;
	private Arret arretArrivee = null;

	public Ligne getLigne() {
		if (ligne == null) {
			ligne = new Ligne();
			ligne.id = ligneId;
			ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligne);
		}
		return ligne;
	}

	public Arret getArretDepart() {
		if (arretDepart == null) {
			arretDepart = new Arret();
			arretDepart.id = arretDepartId;
			arretDepart = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretDepart);
		}
		return arretDepart;
	}

	public Arret getArretArrivee() {
		if (arretArrivee == null) {
			arretArrivee = new Arret();
			arretArrivee.id = arretArriveeId;
			arretArrivee = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretArrivee);
		}
		return arretArrivee;
	}


	@Override
	public void remplirAttribut(String name, String contenu) {
		if ("ligneId".equals(name)) {
			ligneId = contenu;
		} else if ("arretDepartId".equals(name)) {
			arretDepartId = contenu;
		} else if ("heureDepart".equals(name)) {
			heureDepart = contenu;
		} else if ("arretArriveeId".equals(name)) {
			arretArriveeId = contenu;
		} else if ("heureArrivee".equals(name)) {
			heureArrivee = contenu;
		}
	}
}
