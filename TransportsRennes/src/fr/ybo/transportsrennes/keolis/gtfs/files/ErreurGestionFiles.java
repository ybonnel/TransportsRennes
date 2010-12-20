package fr.ybo.transportsrennes.keolis.gtfs.files;

@SuppressWarnings("serial")
public class ErreurGestionFiles extends RuntimeException {

	public ErreurGestionFiles(final Throwable throwable) {
		super(throwable);
	}
}
