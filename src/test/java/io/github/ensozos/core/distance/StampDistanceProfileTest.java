package io.github.ensozos.core.distance;

import org.junit.Test;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class StampDistanceProfileTest {

    /* instance under test */
    private StampDistanceProfile profile = new StampDistanceProfile();

    @Test
    public void testGetDistanceProfileIndex() {

        assertEquals(
                "1.0000", profile.getDistanceProfileIndex(1, 1, 1).toString()
        );
    }

    @Test
    public void testGetDistanceProfile() {

        assertNotNull(profile.getDistanceProfile(
                        new NDArray(5, 5, 'c'),
                        new NDArray(5, 5, 'c'), 1, 2));
    }
}
