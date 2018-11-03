package io.github.ensozos.core.distance;

import io.github.ensozos.core.DistanceProfile;

public class DistanceProfileFactory {

    public static final String STAMP = "STAMP";

    /**
     * Factory method for creating distance profile objects
     *
     * @param distanceProfileType the type of distance profile
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
