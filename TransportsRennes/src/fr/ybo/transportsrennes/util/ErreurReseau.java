package fr.ybo.transportsrennes.util;

public class ErreurReseau extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErreurReseau(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ErreurReseau(String detailMessage) {
		super(detailMessage);
	}

	public ErreurReseau(Throwable throwable) {
		super(throwable);
	}

}
