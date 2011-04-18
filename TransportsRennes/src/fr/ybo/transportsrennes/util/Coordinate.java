package fr.ybo.transportsrennes.util;

import com.google.android.maps.GeoPoint;

public class Coordinate {
	
	private double x;
	private double y;
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public GeoPoint toGeoPoint() {
		return new GeoPoint((int)(x*1.0E6), (int)(y*1.0E6));
	}
	
	

}
