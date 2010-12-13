package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.Line;

/**
 * Handler SAX pour la r�ponse du getdistrict.
 * 
 * @author ybonnel
 * 
 */
public class GetLinesHandler extends KeolisHandler<Line> {

	/**
	 * NAME.
	 */
	private static final String NAME = "name";
	/**
	 * PICTO.
	 */
	private static final String PICTO = "picto";
	/**
	 * LINE.
	 */
	private static final String LINE = "line";
	/**
	 * BASE_URL.
	 */
	private static final String BASE_URL = "baseurl";

	/**
	 * baseUrl.
	 */
	private String baseUrl;

	@Override
	protected final String getBaliseData() {
		return LINE;
	}

	/**
	 * 
	 * @return url de base.
	 */
	public final String getBaseUrl() {
		return baseUrl;
	}

	@Override
	protected final Line getNewObjetKeolis() {
		return new Line();
	}

	@Override
	protected final void remplirObjectKeolis(final Line currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(NAME)) {
			currentObjectKeolis.setName(contenu);
		} else if (baliseName.equals(PICTO)) {
			currentObjectKeolis.setPicto(contenu);
		}
	}

	@Override
	public final void surchargeEndElement(final String pLocalName) {
		super.surchargeEndElement(pLocalName);
		if (BASE_URL.equals(pLocalName)) {
			baseUrl = getContenu().toString();
		}
	}
}
