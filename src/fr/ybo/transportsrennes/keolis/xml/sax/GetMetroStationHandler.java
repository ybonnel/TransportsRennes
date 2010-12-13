package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.MetroStation;

/**
 * Handler pour r�cup�rer les stations de m�tros.
 * 
 * @author ybonnel
 * 
 */
public class GetMetroStationHandler extends KeolisHandler<MetroStation> {

	/**
	 * STATION.
	 */
	public static final String STATION = "station";
	/**
	 * ID.
	 */
	public static final String ID = "id";
	/**
	 * NAME.
	 */
	public static final String NAME = "name";
	/**
	 * LATITUDE.
	 */
	public static final String LATITUDE = "latitude";
	/**
	 * LONGITUDE.
	 */
	public static final String LONGITUDE = "longitude";
	/**
	 * HAS_PLATEFORM_DIRECTION_1.
	 */
	public static final String HAS_PLATEFORM_DIRECTION_1 = "hasPlatformDirection1";
	/**
	 * HAS_PLATEFORM_DIRECTION_2.
	 */
	public static final String HAS_PLATEFORM_DIRECTION_2 = "hasPlatformDirection2";
	/**
	 * RANKING_PLATFORM_DIRECTION_1.
	 */
	public static final String RANKING_PLATFORM_DIRECTION_1 = "rankingPlatformDirection1";
	/**
	 * RANKING_PLATFORM_DIRECTION_2.
	 */
	public static final String RANKING_PLATFORM_DIRECTION_2 = "rankingPlatformDirection2";
	/**
	 * FLOORS.
	 */
	public static final String FLOORS = "floors";
	/**
	 * LASTUPDATE.
	 */
	public static final String LASTUPDATE = "lastupdate";

	@Override
	protected final String getBaliseData() {
		return STATION;
	}

	@Override
	protected final MetroStation getNewObjetKeolis() {
		return new MetroStation();
	}

	@Override
	protected final void remplirObjectKeolis(final MetroStation currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(ID)) {
			currentObjectKeolis.setId(contenu);
		} else if (baliseName.equals(NAME)) {
			currentObjectKeolis.setName(contenu);
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.setLatitude(Double.parseDouble(contenu));
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.setLongitude(Double.parseDouble(contenu));
		} else if (baliseName.equals(HAS_PLATEFORM_DIRECTION_1)) {
			currentObjectKeolis.setHasPlatformDirection1(Boolean.parseBoolean(contenu));
		} else if (baliseName.equals(HAS_PLATEFORM_DIRECTION_2)) {
			currentObjectKeolis.setHasPlatformDirection2(Boolean.parseBoolean(contenu));
		} else if (baliseName.equals(RANKING_PLATFORM_DIRECTION_1)) {
			currentObjectKeolis.setRankingPlatformDirection1(Integer.parseInt(contenu));
		} else if (baliseName.equals(RANKING_PLATFORM_DIRECTION_2)) {
			currentObjectKeolis.setRankingPlatformDirection2(Integer.parseInt(contenu));
		} else if (baliseName.equals(FLOORS)) {
			currentObjectKeolis.setFloors(Integer.parseInt(contenu));
		} else if (baliseName.equals(LASTUPDATE)) {
			currentObjectKeolis.setLastupdate(contenu);
		}
	}
}
