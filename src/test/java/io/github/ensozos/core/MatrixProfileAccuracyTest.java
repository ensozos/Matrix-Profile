package io.github.ensozos.core;

import io.github.ensozos.testsupport.CsvExport;
import io.github.ensozos.testsupport.FileUtil;
import javafx.util.Pair;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import static io.github.ensozos.testsupport.FileUtil.DATA_DIR;
import static io.github.ensozos.testsupport.FileUtil.RESULTS_DIR;
import static org.junit.Assert.assertEquals;


/**
 * Test that we can trade off accuracy for performance
 */
public class MatrixProfileAccuracyTest {

    private MatrixProfile serialMP = new MatrixProfile(false);
    private MatrixProfile concurrentMP = new MatrixProfile(true);

    /** Set this to true if you want to automatically update the results files */
    private static final boolean UPDATE_RESULTS = false;
    private static final int STEPS_IN_DAY = 287;


    @Test
    public void testMatrixProfileSelfJoinStampWindow8() {
        verifyResult("repeat_4",
                "repeat_4_profile_pair_0_1.exp",
                8, 0.1, false);
    }

    // Originally ran in 3m 40s on 4 core laptop before making it multi-threaded. - only 21s when 10% of steps used
    @Test
    public void testMatrixProfileSelfJoinStamp_ArtDailyFlatMiddle() {
        verifyResult("numenta_art_daily_flatmiddle",
                "numenta_art_daily_flatmiddle_profile_pair_0_1.exp",
                STEPS_IN_DAY, 0.1, false);
    }

    // Originally ran in 2m 40s on 4 core laptop before making it multi-threaded - only 23s when 10% of steps used
    @Test
    public void testMatrixProfileSelfJoinStamp_ArtDailyJumpsDown() {
        verifyResult("numenta_art_daily_jumps_down",
                "numenta_art_daily_jumps_down_profile_pair_0_1.exp",
                STEPS_IN_DAY, 0.1, false);
    }


    // ran in 4s
    @Test
    public void testMatrixProfileSelfJoinStampWindow8_concurrent() {
        verifyResult("repeat_4",
                "repeat_4_profile_pair_0_1.exp",
                8, 0.1,true);
    }

    // Ran in 1m 34s on 4 core laptop after making it multi-threaded, but took 10s using only 10%
    @Test
    public void testMatrixProfileSelfJoinStamp_ArtDailyFlatMiddle_concurrent() {
        verifyResult("numenta_art_daily_flatmiddle",
                "numenta_art_daily_flatmiddle_profile_pair_0_1.exp",
                STEPS_IN_DAY, 0.1, true);
    }

    // Ran in 56s on 4 core laptop after making it multi-threaded, but took only 15 seconds using only 10%;  35s at 20%
    @Test
    public void testMatrixProfileSelfJoinStamp_ArtDailyJumpsDown_concurrent() {
        verifyResult("numenta_art_daily_jumps_down",
                "numenta_art_daily_jumps_down_profile_pair_0_1.exp",
                STEPS_IN_DAY, 0.1, true);
    }

    private void verifyResult(String seriesFile, String expResultFile,
                              int window, double accuracy, boolean concurrent) {
        INDArray series = FileUtil.readIndArray(DATA_DIR + seriesFile);
        System.out.println("The series under test has " + series.length() +
                " points. Concurrency = " + concurrent + " accuracy = " + accuracy);

        // the length of these arrays is length of the series - window + 1
        Pair<INDArray, INDArray> expectedResultWhenSelfJoin =
                FileUtil.readIndArrayPair(RESULTS_DIR + expResultFile);

        Pair<INDArray, INDArray> pair =
                concurrent ? concurrentMP.stamp(series, window, accuracy) : serialMP.stamp(series, window, accuracy);

        // Uncomment this to export results to excel. Plotting in excel can be very instructive.
        CsvExport.printToCsvFile(series, pair, "acc_" + seriesFile + ".xls");
        //CsvExport.printPairToFile(pair, "acc_" + seriesFile + ".xls");

        if (UPDATE_RESULTS) {
            CsvExport.printPairToFile(pair, RESULTS_DIR + expResultFile);
        } else {
            assertEquals(expectedResultWhenSelfJoin.toString(), pair.toString());
        }
    }
}
