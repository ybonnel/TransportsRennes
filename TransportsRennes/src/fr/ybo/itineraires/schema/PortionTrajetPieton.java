package fr.ybo.itineraires.schema;

@SuppressWarnings({"serial", "AbstractClassWithoutAbstractMethods"})
public abstract class PortionTrajetPieton extends PortionTrajet {
	public int tempsTrajet;

	@Override
	public void remplirAttribut(String name, String contenu) {
		if ("tempsTrajet".equals(name)) {
			tempsTrajet = Integer.parseInt(contenu);
		}
	}
}
