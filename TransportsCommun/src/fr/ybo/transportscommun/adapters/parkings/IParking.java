package fr.ybo.transportscommun.adapters.parkings;

public interface IParking {

	String getName();

	CharSequence formatDistance();

	int getCarParkAvailable();

	int getCarParkCapacity();

	int getState();

	double getLatitude();

	double getLongitude();
}
