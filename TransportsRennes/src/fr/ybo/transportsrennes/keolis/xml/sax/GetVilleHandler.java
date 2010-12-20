package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.Ville;

/**
 * Handler pour r�cup�rer les villeS.
 *
 * @author ybonnel
 */
public class GetVilleHandler extends KeolisHandler<Ville> {

	/**
	 * VILLE.
	 */
	public static final String VILLE = "city";
	/**
	 * NAME.
	 */
	public static final String NAME = "name";
	/**
	 * DISTRICT.
	 */
	public static final String DISTRICT = "district";
	/**
	 * ID.
	 */
	public static final String ID = "id";

	@Override
	protected final String getBaliseData() {
		return VILLE;
	}

	@Override
	protected final Ville getNewObjetKeolis() {
		return new Ville();
	}

	@Override
	protected final void remplirObjectKeolis(final Ville currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(NAME)) {
			currentObjectKeolis.setName(contenu);
		} else if (baliseName.equals(DISTRICT)) {
			currentObjectKeolis.setNombreDistricts(Integer.parseInt(contenu));
		} else if (baliseName.equals(ID)) {
			currentObjectKeolis.setId(Integer.parseInt(contenu));
		}
	}
}
