package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.velos.Station;

/**
 * Handler SAX pour l'api getstation.
 * 
 * @author ybonnel
 * 
 */
public class GetStationHandler extends KeolisHandler<Station> {

	/**
	 * Nom de la balise station.
	 */
	private static final String STATION = "station";
	/**
	 * Nom de la balise number.
	 */
	private static final String NUMBER = "number";
	/**
	 * Nom de la balise name.
	 */
	private static final String NAME = "name";
	/**
	 * Nom de la balise address.
	 */
	private static final String ADRESSE = "address";
	/**
	 * Nom de la balise state.
	 */
	private static final String STATE = "state";
	/**
	 * Nom de la balise latitude.
	 */
	private static final String LATITUDE = "latitude";
	/**
	 * Nom de la balise longitude.
	 */
	private static final String LONGITUDE = "longitude";
	/**
	 * Nom de la balise slotsavailable.
	 */
	private static final String SLOTSAVAILABLE = "slotsavailable";
	/**
	 * Nom de la balise bikesavailable.
	 */
	private static final String BIKESAVAILABLE = "bikesavailable";
	/**
	 * Nom de la balise pos.
	 */
	private static final String POS = "pos";
	/**
	 * Nom de la balise district.
	 */
	private static final String DISTRICT = "district";
	/**
	 * Nom de la balise lastupdate.
	 */
	private static final String LASTUPDATE = "lastupdate";

	@Override
	protected final String getBaliseData() {
		return STATION;
	}

	@Override
	protected final Station getNewObjetKeolis() {
		return new Station();
	}

	@Override
	protected final void remplirObjectKeolis(final Station currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(NUMBER)) {
			currentObjectKeolis.setNumber(contenu);
		} else if (baliseName.equals(NAME)) {
			currentObjectKeolis.setName(contenu);
		} else if (baliseName.equals(ADRESSE)) {
			currentObjectKeolis.setAdresse(contenu);
		} else if (baliseName.equals(STATE)) {
			currentObjectKeolis.setState(Boolean.parseBoolean(contenu));
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.setLatitude(Double.parseDouble(contenu));
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.setLongitude(Double.parseDouble(contenu));
		} else if (baliseName.equals(SLOTSAVAILABLE)) {
			currentObjectKeolis.setSlotsavailable(Integer.parseInt(contenu));
		} else if (baliseName.equals(BIKESAVAILABLE)) {
			currentObjectKeolis.setBikesavailable(Integer.parseInt(contenu));
		} else if (baliseName.equals(POS)) {
			currentObjectKeolis.setPos(Integer.parseInt(contenu));
		} else if (baliseName.equals(DISTRICT)) {
			currentObjectKeolis.setDistrict(contenu);
		} else if (baliseName.equals(LASTUPDATE)) {
			currentObjectKeolis.setLastupdate(contenu);
		}
	}
}
