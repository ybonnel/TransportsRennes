package fr.ybo.transportsrennes.keolis.gtfs.database;

@SuppressWarnings("serial")
public class DataBaseException extends RuntimeException {


	public DataBaseException(final Exception exception) {
		super(exception);
	}

	public DataBaseException(final String msg) {
		super(msg);
	}
}
