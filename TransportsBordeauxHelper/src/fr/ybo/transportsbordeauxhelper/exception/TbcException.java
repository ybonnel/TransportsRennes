package fr.ybo.transportsbordeauxhelper.exception;

public class TbcException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TbcException(String message, Throwable cause) {
		super(message, cause);
	}

	public TbcException(String message) {
		super(message);
	}

	public TbcException(Throwable cause) {
		super(cause);
	}

}
