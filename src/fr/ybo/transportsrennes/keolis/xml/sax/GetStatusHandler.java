package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.Status;

/**
 * Handler SAX pour l'api getstation.
 * 
 * @author ybonnel
 * 
 */
public class GetStatusHandler extends KeolisHandler<Status> {

	/**
	 * EQUIPEMENT.
	 */
	public static final String EQUIPEMENT = "equipment";
	/**
	 * ID.
	 */
	public static final String ID = "id";
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
		return EQUIPEMENT;
	}

	@Override
	protected final Status getNewObjetKeolis() {
		return new Status();
	}

	@Override
	protected final void remplirObjectKeolis(final Status currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(ID)) {
			currentObjectKeolis.setId(contenu);
		} else if (baliseName.equals(LAST_UPDATE)) {
			currentObjectKeolis.setLastupdate(contenu);
		} else if (baliseName.equals(STATE)) {
			currentObjectKeolis.setState(Boolean.parseBoolean(contenu));
		}
	}
}
