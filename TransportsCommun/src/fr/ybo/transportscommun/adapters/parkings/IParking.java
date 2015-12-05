package fr.ybo.transportscommun.adapters.parkings;

import android.location.Location;

import java.util.Comparator;

public interface IParking {

    class ComparatorDistance implements Comparator<IParking> {

        @Override
		public int compare(final IParking o1, final IParking o2) {
            if (o1 == null || o2 == null || o1.getDistance() == null || o2.getDistance() == null) {
                return 0;
            }
            return o1.getDistance().compareTo(o2.getDistance());
        }
    }

	String getName();

	CharSequence formatDistance();

	int getCarParkAvailable();

	int getCarParkCapacity();

	int getState();

	double getLatitude();

	double getLongitude();

	void calculDistance(Location pCurrentLocation); 
	
	Integer getDistance();

}
