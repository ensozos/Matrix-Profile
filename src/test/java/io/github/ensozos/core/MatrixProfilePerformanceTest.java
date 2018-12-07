package io.github.ensozos.core;

import io.github.ensozos.testsupport.CsvExport;
import io.github.ensozos.testsupport.FileUtil;
import javafx.util.Pair;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import static io.github.ensozos.testsupport.FileUtil.*;
import static org.junit.Assert.assertEquals;


public class MatrixProfilePerformanceTest {

    /* instance under test */
    private MatrixProfile matrixProfile = new MatrixProfile();



    @Test
    public void testMatrixProfileSelfJoinStmpWindow8() {
        verifyResult("repeat_4",
                "repeat_4_profile_pair.exp",
                8);
    }

    // Ran in 3m 9s on 4 core laptop before making it multi-threaded.
    @Test
    public void testMatrixProfileSelfJoinStmp_ArtDailyFlatMiddle() {
        verifyResult("numenta_art_daily_flatmiddle",
                "numenta_art_daily_flatmiddle_profile_pair.exp",
                80);
    }

    private void verifyResult(String seriesFile, String expResultFile, int window) {
        INDArray series = FileUtil.readIndArray(DATA_DIR + seriesFile);

        // the length of these arrays is length of the series - window + 1
        Pair<INDArray, INDArray> expectedResultWhenSelfJoin =
                FileUtil.readIndArrayPair(RESULTS_DIR + expResultFile);

        Pair<INDArray, INDArray> pair = matrixProfile.stmp(series, window);
        // Uncomment this to export results to excel. Plotting in excel can be very instructive.
        CsvExport.printAsCsv(series, expectedResultWhenSelfJoin);
        assertEquals(expectedResultWhenSelfJoin.toString(), pair.toString());
    }
}
