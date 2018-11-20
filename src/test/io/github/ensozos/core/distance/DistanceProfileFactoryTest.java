package io.github.ensozos.core.distance;

import static org.junit.Assert.assertEquals;
import io.github.ensozos.core.DistanceProfile;
import org.junit.Test;


public class DistanceProfileFactoryTest {

    /* instance under test */
    private DistanceProfileFactory factory = new DistanceProfileFactory();

    @Test
    public void createStampDistanceProfile() {
        DistanceProfile profile = factory.getDistanceProfile(DistanceProfileFactory.STAMP);

        assertEquals(
                "1.0000", profile.getDistanceProfileIndex(2, 1, 2).toString()
        );
    }
}
