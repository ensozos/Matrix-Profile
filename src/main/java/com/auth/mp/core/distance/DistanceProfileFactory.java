package com.auth.mp.core.distance;

import com.auth.mp.core.DistanceProfile;

public class DistanceProfileFactory {

    public static final String STAMP = "STAMP";

    /**
     * Factory method for creating distance profile objects
     *
     * @param distanceProfileType
     * @return distance profile object
     */
    public DistanceProfile getDistanceProfile(String distanceProfileType) {
        if (distanceProfileType == null) return null;

        if (distanceProfileType.equalsIgnoreCase(STAMP)) {
            return new StampDistanceProfile();
        }

        return null;
    }

}
