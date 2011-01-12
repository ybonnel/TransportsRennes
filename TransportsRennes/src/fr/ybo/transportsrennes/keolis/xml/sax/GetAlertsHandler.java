package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

/**
 * Handler SAX pour la réponse du getdistrict.
 *
 * @author ybonnel
 */
public class GetAlertsHandler extends KeolisHandler<Alert> {

	/**
	 * TITLE.
	 */
	private static final String TITLE = "title";
	/**
	 * STARTTIME.
	 */
	private static final String STARTTIME = "starttime";
	/**
	 * ENDTIME.
	 */
	private static final String ENDTIME = "endtime";
	/**
	 * LINE.
	 */
	private static final String LINE = "line";
	/**
	 * MAJORDISTURBANCE.
	 */
	private static final String MAJORDISTURBANCE = "majordisturbance";
	/**
	 * DETAIL.
	 */
	private static final String DETAIL = "detail";
	/**
	 * LINK.
	 */
	private static final String LINK = "link";
	/**
	 * ALERT.
	 */
	private static final String ALERT = "alert";

	@Override
	protected final String getBaliseData() {
		return ALERT;
	}

	@Override
	protected final Alert getNewObjetKeolis() {
		return new Alert();
	}

	@Override
	protected final void remplirObjectKeolis(final Alert currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(TITLE)) {
			currentObjectKeolis.title = contenu;
		} else if (baliseName.equals(STARTTIME)) {
			currentObjectKeolis.starttime = contenu;
		} else if (baliseName.equals(ENDTIME)) {
			currentObjectKeolis.endtime = contenu;
		} else if (baliseName.equals(LINE)) {
			currentObjectKeolis.lines.add(contenu);
		} else if (baliseName.equals(MAJORDISTURBANCE)) {
			currentObjectKeolis.majordisturbance = Boolean.parseBoolean(contenu);
		} else if (baliseName.equals(DETAIL)) {
			currentObjectKeolis.detail = contenu;
		} else if (baliseName.equals(LINK)) {
			currentObjectKeolis.link = contenu;
		}
	}
}
