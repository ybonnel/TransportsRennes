package fr.ybo.transportsrennes.keolis.modele.bus;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Une ligne.
 *
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class Line implements Serializable {

	/**
	 * Nom.
	 */
	private String name;
	/**
	 * Picto.
	 */
	private String picto;
	/**
	 * URL du picto.
	 */
	private URL urlPicto;

	/**
	 * Génère l'URL du picto à partir d'une URL de base.
	 *
	 * @param baseUrl URL de base.
	 * @throws MalformedURLException URL mal formée.
	 */
	public final void genereUrl(final String baseUrl) throws MalformedURLException {
		urlPicto = new URL(baseUrl + picto);
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the picto
	 */
	public final String getPicto() {
		return picto;
	}

	/**
	 * @return the urlPicto
	 */
	public final URL getUrlPicto() {
		return urlPicto;
	}

	/**
	 * @param pName the name to set
	 */
	public final void setName(final String pName) {
		name = pName;
	}

	/**
	 * @param pPicto the picto to set
	 */
	public final void setPicto(final String pPicto) {
		picto = pPicto;
	}

	/**
	 * @param pUrlPicto the urlPicto to set
	 */
	public final void setUrlPicto(final URL pUrlPicto) {
		urlPicto = pUrlPicto;
	}

}
