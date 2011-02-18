package fr.ybo.itineraires;


@SuppressWarnings({"serial"})
public class ItinerairesException extends RuntimeException {

	public ItinerairesException(Throwable cause) {
		super(cause);
	}

	public ItinerairesException(String message) {
		super(message);
	}
}
