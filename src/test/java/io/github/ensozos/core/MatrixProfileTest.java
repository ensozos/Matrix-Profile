package io.github.ensozos.core;

import javafx.util.Pair;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import static org.junit.Assert.assertEquals;
import static java.lang.Double.POSITIVE_INFINITY;


public class MatrixProfileTest {

    /* instance under test */
    private MatrixProfile matrixProfile = new MatrixProfile(false);

    private INDArray shortTargetSeries = Nd4j.create(
        new double[]{0.0, 6.0, -1.0, 2.0, 3.0, 1.0, 4.0},
        new int[]{1, 7}
    );
    private INDArray targetSeriesWithPattern = Nd4j.create(
        new double[]{0.6, 0.5, 2.00,  1.0,  -1.01, -0.5, 1.0,  2.3,  4.0,  5.9, 4.2, 3.1, 3.2,
                3.4,  2.9, 3.5,  1.05, -1.0, -0.50, 1.01, 2.41, 3.99, 6.01, 4.7, 3.2, 2.6, 4.1, 4.3, 1.1, 1.7, 3.1, 1.9,
                -0.5, 2.1, 1.9, 2.01, -0.02, 0.48, 2.03, 3.31, 5.1,  7.1,  5.1, 3.2, 2.3, 1.8, 2.1, 1.7, 1.1, -0.1, 2.1,
                2.01, 3.9, 3.1, 1.05, -1.0,  -0.5, 1.01, 2.41, 3.99, 6.01, 4.7, 4.5, 3.9, 2.1, 3.3, 3.1, 2.7, 1.9
        },
        new int[]{1, 69}
    );

    private INDArray query = Nd4j.create(new double[]{1.0, 2.0, 0.0, 0.0, -1}, new int[]{1, 5});

    private Pair<INDArray, INDArray>expectedResultWhenQuery = new Pair<>(
            Nd4j.create(new double[]{1.3881,    1.7967,    3.0370,    2.6308}, new int[]{1, 4}),
            Nd4j.create(new double[]{0,    1.0000,         0,         0}, new int[]{1, 4})
    );


    @Test
    public void testMatrixProfileStmpWindow4() {
        int window = 4;
        Pair<INDArray, INDArray> pair = matrixProfile.stmp(shortTargetSeries, query, window);
        assertEquals(expectedResultWhenQuery.toString(), pair.toString());
    }

    /**
     * The only difference between this and the above test is that it uses the "anytime"
     * interruptable version (ie.e stamp instead of stmp).
     */
    @Test
    public void testMatrixProfileStampWindow4() {
        int window = 4;
        Pair<INDArray, INDArray> pair = matrixProfile.stamp(shortTargetSeries, query, window, 1.0);
        assertEquals(expectedResultWhenQuery.toString(), pair.toString());
    }

    @Test
    public void testMatrixProfileStmpWindow3() {

        int window = 3;
        Pair<INDArray, INDArray>expected = new Pair<>(
                Nd4j.create(new double[]{0.6732,    0.7582,    2.7278,    0.0014,    2.2059}, new int[]{1, 5}),
                Nd4j.create(new double[]{0,    1.0000,         0,         0,    1.0000}, new int[]{1, 5})
        );

        Pair<INDArray, INDArray> pair = matrixProfile.stmp(shortTargetSeries, query, window);
        assertEquals(expected.toString(), pair.toString());
    }



    @Test
    public void testMatrixProfileSelfJoinStmpWindow8() {
        int window = 8;

        // the length of these arrays is length of the series - window + 1
        Pair<INDArray, INDArray> expectedResultWhenSelfJoin = new Pair<>(
            Nd4j.create(new double[]{
                    1.9782,    0.8371,    0.4547,    0.0873,    0.1701,    0.4099,    0.6994,    1.4519,    1.3319,
                    0.9451,    1.8153,    1.9904,    1.1876,    1.0813,    0.5912,    0.1653,    0.0022,    0.1701,
                    0.4099,    0.6994,    1.4519,    2.0698,    1.4597,    1.9393,    2.2886,    1.8086,    1.6974,
                    2.0345,    2.1472,    1.9444,    1.9850,    2.5536,    1.9782,    0.8371,    0.4881,    0.0872,
                    0.3965,    0.7431,    1.1167,    1.3549,    1.3319,    1.1393,    0.9451,    1.6974,    1.9328,
                    1.3300,    1.5756,    2.0810,    1.8508,    1.1167,    1.1876,    1.0813,    0.5912,    0.1653,
                    0.0022,    0.5018,    0.6485,    1.3795,    1.7919,    1.4695,    1.1392,    1.3864
            }, new int[]{1, 62}),
            Nd4j.create(new double[]{
                    32.0000,   33.0000,   53.0000,   35.0000,   17.0000,   18.0000,   19.0000,   20.0000,   40.0000,
                    42.0000,   42.0000,   49.0000,   50.0000,   51.0000,   52.0000,   53.0000,   54.0000,    4.0000,
                    5.0000,     6.0000,    7.0000,   40.0000,   42.0000,   43.0000,   50.0000,   61.0000,   43.0000,
                    44.0000,        0,    61.0000,   22.0000,   23.0000,         0,    1.0000,    2.0000,    3.0000,
                     4.0000,   18.0000,   49.0000,   50.0000,    8.0000,   60.0000,    9.0000,   26.0000,   13.0000,
                    33.0000,    2.0000,   36.0000,   37.0000,   38.0000,   12.0000,   13.0000,   14.0000,   15.0000,
                    16.0000,   17.0000,    5.0000,    6.0000,    7.0000,   40.0000,   41.0000,   42.0000
            }, new int[]{1, 62})
        );

        Pair<INDArray, INDArray> pair = matrixProfile.stmp(targetSeriesWithPattern, window);
        // Uncomment this to export results to excel. Plotting in excel can be very instructive.
        //CsvExport.printAsCsv(targetSeriesWithPattern, expectedResultWhenSelfJoin);
        assertEquals(expectedResultWhenSelfJoin.toString(), pair.toString());
    }

    @Test
    public void testMatrixProfileSelfJoinStmpWindow4() {
        int window = 4;
        Pair<INDArray, INDArray>expectedResultWhenSelfJoin = new Pair<>(
                Nd4j.create(new double[]{1.7308, POSITIVE_INFINITY, POSITIVE_INFINITY, 1.7308}, new int[]{1, 4}),
                Nd4j.create(new double[]{3.0000,    3.0000,    3.0000,     0}, new int[]{1, 4})
        );

        Pair<INDArray, INDArray> pair = matrixProfile.stmp(shortTargetSeries, window);
        assertEquals(expectedResultWhenSelfJoin.toString(), pair.toString());
    }

    /*
     * Why doesn't stamp give the same result as stmp here?
     * Commenting it out until #4 can be resolved.
    @Test
    public void testMatrixProfileSelfJoinStampWindow4() {
        int window = 4;
        Pair<INDArray, INDArray>expectedResultWhenSelfJoin = new Pair<>(
                Nd4j.create(new double[]{1.7308, POSITIVE_INFINITY, POSITIVE_INFINITY, 1.7308}, new int[]{1, 4}),
                Nd4j.create(new double[]{3.0000,    3.0000,    3.0000,     0}, new int[]{1, 4})
        );

        Pair<INDArray, INDArray> pair = matrixProfile.stamp(shortTargetSeries, window);
        assertEquals(expectedResultWhenSelfJoin.toString(), pair.toString());
    }*/
}
