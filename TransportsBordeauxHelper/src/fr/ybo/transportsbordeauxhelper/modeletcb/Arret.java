package fr.ybo.transportsbordeauxhelper.modeletcb;

import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("arrets.txt")
public class Arret {

	@BaliseCsv(value = "identifiant", ordre = 1)
	public String identifant;
	@BaliseCsv(value = "nom", ordre = 2)
	public String nom;

	@Override
	public String toString() {
		return "Arret [identifant=" + identifant + ", nom=" + nom + "]";
	}


}
