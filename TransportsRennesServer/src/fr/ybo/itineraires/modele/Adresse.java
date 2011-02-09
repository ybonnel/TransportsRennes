package fr.ybo.itineraires.modele;

public class Adresse {
	private String adresse;
	private Double latitude;
	private Double longitude;
	
	public Adresse(String adresse, Double latitude, Double longitude) {
		this.adresse = adresse;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
