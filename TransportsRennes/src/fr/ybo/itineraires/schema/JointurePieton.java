package fr.ybo.itineraires.schema;

@SuppressWarnings({"serial"})
public class JointurePieton extends PortionTrajetPieton {
	public String arretId;
	public Adresse adresse;

	@Override
	public void remplirAttribut(String name, String contenu) {
		super.remplirAttribut(name, contenu);
		if ("arretId".equals(name)) {
			arretId = contenu;
		} else if ("adresse".equals(name)) {
			adresse = new Adresse();
		} else if ("latitude".equals(name)) {
			adresse.latitude = Double.parseDouble(contenu);
		} else if ("longitude".equals(name)) {
			adresse.longitude = Double.parseDouble(contenu);
		}
	}
}
