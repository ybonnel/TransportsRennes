package fr.ybo.transportsrennes.keolis.gtfs.database;

@SuppressWarnings("serial")
public class DataBaseException extends RuntimeException {

	public DataBaseException() {
		super();
	}

	public DataBaseException(final Exception exception) {
		super(exception);
	}

	public DataBaseException(final String msg) {
		super(msg);
	}

	public DataBaseException(final String msg, final Exception exception) {
		super(msg, exception);
	}
}
