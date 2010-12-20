package fr.ybo.transportsrennes.keolis.gtfs.moteur;

@SuppressWarnings("serial")
public class ErreurMoteurCsv extends RuntimeException {

	public ErreurMoteurCsv(final String message) {
		super(message);
	}

	public ErreurMoteurCsv(final String message, final Throwable throwable) {
		super(message, throwable);
	}

	public ErreurMoteurCsv(final Throwable throwable) {
		super(throwable);
	}

}
