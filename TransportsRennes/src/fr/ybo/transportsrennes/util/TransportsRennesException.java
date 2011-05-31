package fr.ybo.transportsrennes.util;

public class TransportsRennesException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransportsRennesException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public TransportsRennesException(String detailMessage) {
		super(detailMessage);
	}

	public TransportsRennesException(Throwable throwable) {
		super(throwable);
	}

}
