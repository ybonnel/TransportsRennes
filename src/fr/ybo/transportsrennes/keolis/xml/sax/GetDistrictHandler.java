package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.District;

/**
 * Handler SAX pour la r�ponse du getdistrict.
 * 
 * @author ybonnel
 * 
 */
public class GetDistrictHandler extends KeolisHandler<District> {
	/**
	 * Nom de la balise id.
	 */
	private static final String ID = "id";
	/**
	 * Nom de la balise name.
	 */
	private static final String NAME = "name";
	/**
	 * Nom de la balise district.
	 */
	private static final String DISTRICT = "district";

	@Override
	protected final String getBaliseData() {
		return DISTRICT;
	}

	@Override
	protected final District getNewObjetKeolis() {
		return new District();
	}

	@Override
	protected final void remplirObjectKeolis(final District currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(ID)) {
			currentObjectKeolis.setId(contenu);
		} else if (baliseName.equals(NAME)) {
			currentObjectKeolis.setName(contenu);
		}
	}
}
