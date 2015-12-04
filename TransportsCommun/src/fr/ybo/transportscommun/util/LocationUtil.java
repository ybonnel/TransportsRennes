/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportscommun.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;


public class LocationUtil implements LocationListener {

    private static final LogYbo LOG_YBO = new LogYbo(LocationUtil.class);

    /**
     * Le locationManager permet d'accéder au GPS du téléphone.
     */
    private final LocationManager locationManager;

    private final UpdateLocationListenner listenner;

    private Location currentBestLocation;

    public LocationUtil(final UpdateLocationListenner listenner, final Context context) {
        super();
        this.listenner = listenner;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getCurrentLocation() {
        return currentBestLocation;
    }

    private boolean activeGps;

    /**
     * Active le GPS.
     *
     * @return true si le GPS a été trouvé.
     */
    public boolean activeGps() {
        if (activeGps) {
            return true;
        }
        final List<String> providers = locationManager.getProviders(true);
        LOG_YBO.debug("Providers courants : " + providers);
        boolean gpsTrouve = false;
        boolean hasBetter = false;
        for (final String providerName : providers) {
            hasBetter |= isBetterLocation(locationManager.getLastKnownLocation(providerName));
            locationManager.requestLocationUpdates(providerName, 60000L, 20L, this);
            if (providerName.equals(LocationManager.GPS_PROVIDER)) {
                gpsTrouve = true;
            }
        }
        if (hasBetter) {
            LOG_YBO.debug("Mise à jour de la loc : " + currentBestLocation);
            listenner.updateLocation(currentBestLocation);
        }
        activeGps = true;
        return gpsTrouve;
    }

    public void desactiveGps() {
        if (activeGps) {
            locationManager.removeUpdates(this);
        }
        activeGps = false;
    }

    public interface UpdateLocationListenner {
        void updateLocation(Location location);
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (isBetterLocation(location)) {
            LOG_YBO.debug("Mise à jour de la loc : " + currentBestLocation);
            listenner.updateLocation(currentBestLocation);
        }
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }

    @Override
    public void onProviderEnabled(final String provider) {
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }

    private static final long FIVE_MINUTES = 1000 * 60 * 5;

    /**
     * Determines whether one Location reading is better than the current
     * Location fix
     *
     * @param location The new Location that you want to evaluate
     * @return true is location is best than the location receive before.
     */
    private boolean isBetterLocation(final Location location) {
        if (location == null) {
            return false;
        }
        LOG_YBO.debug("BestLocation : " + location);
        if (currentBestLocation == null) {
            // A new location is always better than no location
            currentBestLocation = location;
            LOG_YBO.debug("BestLocation : true (ancienne null)");
            return true;
        }

        // Check whether the new location fix is newer or older
        final long timeDelta = location.getTime() - currentBestLocation.getTime();
        final boolean isSignificantlyNewer = timeDelta > FIVE_MINUTES;
        final boolean isSignificantlyOlder = timeDelta < -FIVE_MINUTES;
        final boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            currentBestLocation = location;
            LOG_YBO.debug("BestLocation : true (plus recente)");
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        }
        if (isSignificantlyOlder) {
            LOG_YBO.debug("BestLocation : false (trop vieille)");
            return false;
        }

        // Check whether the new location fix is more or less accurate
        final int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        final boolean isLessAccurate = accuracyDelta > 0;
        final boolean isMoreAccurate = accuracyDelta < 0;
        final boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        final boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            currentBestLocation = location;
            LOG_YBO.debug("BestLocation : true (plus précise)");
            return true;
        }
        if (isNewer && !isLessAccurate) {
            LOG_YBO.debug("BestLocation : true (plus récente et pas moins précise)");
            currentBestLocation = location;
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            LOG_YBO.debug("BestLocation : true (plus récente et pas vraiement moins précise et du même provider)");
            currentBestLocation = location;
            return true;
        }
        return false;
    }

    /**
     * Permet de savoir si deux provider sont les mêmes.
     *
     * @param provider1 permiers provider.
     * @param provider2 second provider.
     * @return true si les deux provider sont identiques.
     */
    private static boolean isSameProvider(final String provider1, final String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
