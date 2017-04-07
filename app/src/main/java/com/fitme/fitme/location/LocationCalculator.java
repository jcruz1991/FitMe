package com.fitme.fitme.location;

import com.fitme.fitme.model.UserLocation;

public class LocationCalculator {

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    public LocationCalculator() {}

    /**
     * Calculates the distance between two locations in KM
     * */
    public double calculateDistance(UserLocation location1, UserLocation location2) {

        double latDistance = Math.toRadians(location1.getLatitude() - location2.getLatitude());
        double lngDistance = Math.toRadians(location1.getLongitude() - location2.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(location1.getLatitude())) * Math.cos(Math.toRadians
                (location2.getLatitude())) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (double) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }

}
