package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;

/**
 * Handler permettant de rÃ©cupÃ©rer les points de vente.
 *
 * @author ybonnel
 */
public class GetPointDeVenteHandler extends KeolisHandler<PointDeVente> {

	/**
	 * POS.
	 */
	public static final String POS = "pos";
	/**
	 * NAME.
	 */
	public static final String NAME = "name";
	/**
	 * TYPE.
	 */
	public static final String TYPE = "type";
	/**
	 * ADRESSE.
	 */
	public static final String ADRESSE = "address";
	/**
	 * CODE_POSTAL.
	 */
	public static final String CODE_POSTAL = "zipcode";
	/**
	 * VILLE.
	 */
	public static final String VILLE = "city";
	/**
	 * DISTRICT.
	 */
	public static final String DISTRICT = "district";
	/**
	 * TELEPHONE.
	 */
	public static final String TELEPHONE = "phone";
	/**
	 * SCHEDULE.
	 */
	public static final String SCHEDULE = "schedule";
	/**
	 * LATITUDE.
	 */
	public static final String LATITUDE = "latitude";
	/**
	 * LONGITUDE.
	 */
	public static final String LONGITUDE = "longitude";

	@Override
	protected final String getBaliseData() {
		return POS;
	}

	@Override
	protected final PointDeVente getNewObjetKeolis() {
		return new PointDeVente();
	}

	@Override
	protected final void remplirObjectKeolis(final PointDeVente currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(NAME)) {
			currentObjectKeolis.name = contenu;
		} else if (baliseName.equals(TYPE)) {
			currentObjectKeolis.type = contenu;
		} else if (baliseName.equals(ADRESSE)) {
			currentObjectKeolis.adresse = contenu;
		} else if (baliseName.equals(CODE_POSTAL)) {
			currentObjectKeolis.codePostal = contenu;
		} else if (baliseName.equals(VILLE)) {
			currentObjectKeolis.ville = contenu;
		} else if (baliseName.equals(DISTRICT)) {
			currentObjectKeolis.district = contenu;
		} else if (baliseName.equals(TELEPHONE)) {
			currentObjectKeolis.telephone = contenu;
		} else if (baliseName.equals(SCHEDULE)) {
			currentObjectKeolis.schedule = contenu;
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.latitude = Double.parseDouble(contenu);
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.longitude = Double.parseDouble(contenu);
		}
	}
}
