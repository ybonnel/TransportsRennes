package fr.ybo.itineraires.modele;

import java.math.BigDecimal;

import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

public class Bounds {
	
	private double minLatitude;
	private double maxLatitude;
	private double minLongitude;
	private double maxLongitude;
	public Bounds(double minLatitude, double maxLatitude, double minLongitude,
			double maxLongitude) {
		this.minLatitude = minLatitude;
		this.maxLatitude = maxLatitude;
		this.minLongitude = minLongitude;
		this.maxLongitude = maxLongitude;
	}
	public double getMinLatitude() {
		return minLatitude;
	}
	public double getMaxLatitude() {
		return maxLatitude;
	}
	public double getMinLongitude() {
		return minLongitude;
	}
	public double getMaxLongitude() {
		return maxLongitude;
	}
	
	private LatLngBounds bounds = null;
	
	public LatLngBounds getLatLngBounds() {
		if (bounds == null) {
			bounds = new LatLngBounds(new LatLng(new BigDecimal(minLatitude), new BigDecimal(minLongitude)), new LatLng(new BigDecimal(maxLatitude), new BigDecimal(maxLongitude)));
		}
		return bounds;
	}
}
