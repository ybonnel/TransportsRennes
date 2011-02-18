package fr.ybo.itineraires.schema;

@SuppressWarnings({"serial"})
public class JointureCorrespondance extends PortionTrajetPieton {
	public String arretDepartId;
	public String arretArriveeId;

	@Override
	public void remplirAttribut(String name, String contenu) {
		super.remplirAttribut(name, contenu);
		if ("arretDepartId".equals(name)) {
			arretDepartId = contenu;
		} else if ("arretArriveeId".equals(name)) {
			arretArriveeId = contenu;
		}
	}
}
