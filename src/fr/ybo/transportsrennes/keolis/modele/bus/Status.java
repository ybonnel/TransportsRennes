package fr.ybo.transportsrennes.keolis.modele.bus;

import java.io.Serializable;

/**
 * Status.
 * 
 * @author ybonnel
 * 
 */
@SuppressWarnings("serial")
public class Status implements Serializable {
	/**
	 * Id.
	 */
	private String id;
	/**
	 * Derni�re mise � jour.
	 */
	private String lastupdate;
	/**
	 * Etat.
	 */
	private boolean state;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the lastupdate
	 */
	public final String getLastupdate() {
		return lastupdate;
	}

	/**
	 * @return the state
	 */
	public final boolean isState() {
		return state;
	}

	/**
	 * @param pId
	 *            the id to set
	 */
	public final void setId(final String pId) {
		id = pId;
	}

	/**
	 * @param pLastupdate
	 *            the lastupdate to set
	 */
	public final void setLastupdate(final String pLastupdate) {
		lastupdate = pLastupdate;
	}

	/**
	 * @param pState
	 *            the state to set
	 */
	public final void setState(final boolean pState) {
		state = pState;
	}

}
