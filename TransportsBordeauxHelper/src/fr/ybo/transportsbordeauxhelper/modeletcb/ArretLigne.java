package fr.ybo.transportsbordeauxhelper.modeletcb;

import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("arrets_lignes.txt")
public class ArretLigne {
	@BaliseCsv(value = "ligne_id", ordre = 1)
	public String ligneId;
	@BaliseCsv(value = "arret_id", ordre = 2)
	public String arretId;
	@BaliseCsv(value = "forward", ordre = 3, adapter = AdapterBoolean.class)
	public Boolean forward;
	@BaliseCsv(value = "backward", ordre = 4, adapter = AdapterBoolean.class)
	public Boolean backward;

}
