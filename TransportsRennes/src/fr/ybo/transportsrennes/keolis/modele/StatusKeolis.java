package fr.ybo.transportsrennes.keolis.modele;

/**
 * Repr�sente le status de retour des apis keolis.
 *
 * @author ybonnel
 */
public class StatusKeolis {

	/**
	 * Le code du status (0 pour ok).
	 */
	private String code;

	/**
	 * M�ssage associ� au status.
	 */
	private String message;

	/**
	 * @return the code
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * @param pCode the code to set
	 */
	public final void setCode(final String pCode) {
		code = pCode;
	}

	/**
	 * @param pMessage the message to set
	 */
	public final void setMessage(final String pMessage) {
		message = pMessage;
	}
}
