package io.github.ensozos.core;

import io.github.ensozos.testsupport.CsvExport;
import io.github.ensozos.testsupport.FileUtil;
import javafx.util.Pair;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import static io.github.ensozos.testsupport.FileUtil.*;
import static org.junit.Assert.assertEquals;


public class MatrixProfilePerformanceTest {

    private MatrixProfile serialMP = new MatrixProfile(false);
    private MatrixProfile concurrentMP = new MatrixProfile(true);

    /** Set this to true if you want to automatically update the results files */
    private static final boolean UPDATE_RESULTS = false;



    @Test
    public void testMatrixProfileSelfJoinStmpWindow8() {
        verifyResult("repeat_4",
                "repeat_4_profile_pair.exp",
                8, false);
    }

    // Ran in 3m 40s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtDailyFlatMiddle() {
        verifyResult("numenta_art_daily_flatmiddle",
                "numenta_art_daily_flatmiddle_profile_pair.exp",
                80, false);
    }

    // Ran in 3m 1s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtDailyJumpsDown() {
        verifyResult("numenta_art_daily_jumps_down",
                "numenta_art_daily_jumps_down_profile_pair.exp",
                80, false);
    }

    /* Commenting some tests to reduce runtime.
     * Also noticed that some of these tests fail when run from comman line, but not in intellij.
    // Ran in 6m 43s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtIncreaseSpikeDensity() {
        verifyResult("numenta_art_increase_spike_density",
                "numenta_art_increase_spike_density_profile_pair.exp",
                80, false);
    }

    // Ran in 3m 1s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtLoadBalancerSpikes() {
        verifyResult("numenta_art_load_balancer_spikes",
                "numenta_art_load_balancer_spikes_profile_pair.exp",
                80, false);
    }*/



    @Test
    public void testMatrixProfileSelfJoinStmpWindow8_concurrent() {
        verifyResult("repeat_4",
                "repeat_4_profile_pair.exp",
                8, true);
    }

    // Ran in 1m 49s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtDailyFlatMiddle_concurrent() {
        verifyResult("numenta_art_daily_flatmiddle",
                "numenta_art_daily_flatmiddle_profile_pair.exp",
                80, true);
    }

    // Ran in 57s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtDailyJumpsDown_concurrent() {
        verifyResult("numenta_art_daily_jumps_down",
                "numenta_art_daily_jumps_down_profile_pair.exp",
                80, true);
    }

    /**
    // Ran in 4m 37s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtIncreaseSpikeDensity_concurrent() {
        verifyResult("numenta_art_increase_spike_density",
                "numenta_art_increase_spike_density_profile_pair.exp",
                80, true);
    }

    // Ran in 1m 6s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtLoadBalancerSpikes_concurrent() {
        verifyResult("numenta_art_load_balancer_spikes",
                "numenta_art_load_balancer_spikes_profile_pair.exp",
                80, true);
    }*/

    private void verifyResult(String seriesFile, String expResultFile, int window, boolean concurrent) {
        INDArray series = FileUtil.readIndArray(DATA_DIR + seriesFile);
        System.out.println("The series under test has " + series.length() + " points. Concurrency = " + concurrent);

        // the length of these arrays is length of the series - window + 1
        Pair<INDArray, INDArray> expectedResultWhenSelfJoin =
                FileUtil.readIndArrayPair(RESULTS_DIR + expResultFile);

        Pair<INDArray, INDArray> pair =
                concurrent ? concurrentMP.stmp(series, window) : serialMP.stmp(series, window);
        // Uncomment this to export results to excel. Plotting in excel can be very instructive.
        //CsvExport.printAsCsv(series, expectedResultWhenSelfJoin);
        //CsvExport.printPairToFile(pair, "temp.exp");

        if (UPDATE_RESULTS) {
            CsvExport.printPairToFile(pair, RESULTS_DIR + expResultFile);
        } else {
            assertEquals(expectedResultWhenSelfJoin.toString(), pair.toString());
        }
    }
}
