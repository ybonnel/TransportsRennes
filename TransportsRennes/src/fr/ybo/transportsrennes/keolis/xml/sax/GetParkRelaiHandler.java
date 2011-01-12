package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;

/**
 * Handler pour rÃ©cupÃ©rer les parks relais.
 *
 * @author ybonnel
 */
public class GetParkRelaiHandler extends KeolisHandler<ParkRelai> {

	/**
	 * RELAY_PARK.
	 */
	public static final String RELAY_PARK = "relaypark";
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
	 * CAR_PARK_AVAILABLE.
	 */
	public static final String CAR_PARK_AVAILABLE = "carparkavailable";
	/**
	 * CAR_PARK_CAPACITY.
	 */
	public static final String CAR_PARK_CAPACITY = "carparkcapacity";
	/**
	 * LAST_UPDATE.
	 */
	public static final String LAST_UPDATE = "lastupdate";
	/**
	 * STATE.
	 */
	public static final String STATE = "state";

	@Override
	protected final String getBaliseData() {
		return RELAY_PARK;
	}

	@Override
	protected final ParkRelai getNewObjetKeolis() {
		return new ParkRelai();
	}

	@Override
	protected final void remplirObjectKeolis(final ParkRelai currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(NAME)) {
			currentObjectKeolis.name = contenu;
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.latitude = Double.parseDouble(contenu);
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.longitude = Double.parseDouble(contenu);
		} else if (baliseName.equals(CAR_PARK_AVAILABLE)) {
			currentObjectKeolis.carParkAvailable = Integer.parseInt(contenu);
		} else if (baliseName.equals(CAR_PARK_CAPACITY)) {
			currentObjectKeolis.carParkCapacity = Integer.parseInt(contenu);
		} else if (baliseName.equals(LAST_UPDATE)) {
			currentObjectKeolis.lastupdate = contenu;
		} else if (baliseName.equals(STATE)) {
			currentObjectKeolis.state = Integer.parseInt(contenu);
		}
	}
}
