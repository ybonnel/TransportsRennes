package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.Equipement;

/**
 * Handler pour récupérer les équipements.
 *
 * @author ybonnel
 */
public class GetEquipmentsHandler extends KeolisHandler<Equipement> {

	/**
	 * EQUIPEMENT.
	 */
	public static final String EQUIPEMENT = "equipment";
	/**
	 * ID.
	 */
	public static final String ID = "id";
	/**
	 * STATION.
	 */
	public static final String STATION = "station";
	/**
	 * TYPE.
	 */
	public static final String TYPE = "type";
	/**
	 * FROM_FLOOR.
	 */
	public static final String FROM_FLOOR = "fromfloor";
	/**
	 * TO_FLOOR.
	 */
	public static final String TO_FLOOR = "tofloor";
	/**
	 * PLATFROM.
	 */
	public static final String PLATFROM = "platform";
	/**
	 * LAST_UPDATE.
	 */
	public static final String LAST_UPDATE = "lastupdate";

	@Override
	protected final String getBaliseData() {
		return EQUIPEMENT;
	}

	@Override
	protected final Equipement getNewObjetKeolis() {
		return new Equipement();
	}

	@Override
	protected final void remplirObjectKeolis(final Equipement currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(ID)) {
			currentObjectKeolis.setId(contenu);
		} else if (baliseName.equals(STATION)) {
			currentObjectKeolis.setStation(contenu);
		} else if (baliseName.equals(TYPE)) {
			currentObjectKeolis.setType(contenu);
		} else if (baliseName.equals(FROM_FLOOR)) {
			currentObjectKeolis.setFromfloor(Integer.parseInt(contenu));
		} else if (baliseName.equals(TO_FLOOR)) {
			currentObjectKeolis.setTofloor(Integer.parseInt(contenu));
		} else if (baliseName.equals(PLATFROM)) {
			currentObjectKeolis.setPlatform(Integer.parseInt(contenu));
		} else if (baliseName.equals(LAST_UPDATE)) {
			currentObjectKeolis.setLastupdate(contenu);
		}
	}
}
