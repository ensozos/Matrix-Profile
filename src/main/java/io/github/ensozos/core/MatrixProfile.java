package io.github.ensozos.core;


import io.github.ensozos.utils.CustomOperations;
import io.github.ensozos.core.distance.DistanceProfileFactory;
import io.github.ensozos.core.order.LinearOrder;
import io.github.ensozos.core.order.Order;
import io.github.ensozos.core.order.RandomOrder;
import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;


public class MatrixProfile {

    private DistanceProfileFactory distanceProfileFactory;

    /**
     *  Class for matrix profile computation based on Yeh, Chin-Chia Michael Zhu, Yan Ulanova, Liudmila Begum, Nurjahan Ding, Yifei  Dau, Anh  Silva, Diego  Mueen,
     *  Abdullah  Keogh, Eamonn. (2016). Matrix Profile I: All Pairs Similarity Joins for Time Series: A Unifying View That Includes Motifs,
     *  Discords and Shapelets. 1317-1322. 10.1109/ICDM.2016.0179.
     *
     */
    public MatrixProfile() {
        distanceProfileFactory = new DistanceProfileFactory();
    }

    /**
     * Scalable time series matrix profile algorithm. Computes a vector(matrix profile)
     * of distances between each subsequence and its nearest neighbor in
     * O(n^2 logn) time complexity and O(n) space complexity.
     *
     * @param target target time series
     * @param query  query time series
     * @param window size of window
     * @return a Pair with profile matrix as key and profile index as value.
     */
    public Pair<INDArray, INDArray> stmp(INDArray target, INDArray query, int window) {
        DistanceProfile stamp = distanceProfileFactory.getDistanceProfile(DistanceProfileFactory.STAMP);

        int target_shape = (int) target.shape()[1];
        int query_shape = (int) query.shape()[1];
        if (target_shape >= query_shape)
            return matrixProfile(query, window, new LinearOrder((int) (query.length() - window + 1)), stamp, target, false);

        INDArray new_target = CustomOperations.singlePad(target, query_shape - target_shape);

        return matrixProfile(query, window, new LinearOrder((int) (new_target.length() - window + 1)), stamp, new_target, false);
    }

    /**
     * Scalable time series matrix profile algorithm (self join). Computes a vector(matrix profile)
     * of distances between each subsequence and its nearest neighbor in
     * O(n^2 logn) time complexity and O(n) space complexity. In case of self join we take into
     * account trivial match. To avoid trivial matches we find the first match then we set an exclusion
     * zone around the best match. The size of exclusion zone in 1/2 of the query size.
     *
     * @param target target time series
     * @param window size of window
     * @return a Pair with profile matrix as key and profile index as value.
     */
    public Pair<INDArray, INDArray> stmp(INDArray target, int window) {
        DistanceProfile stamp = distanceProfileFactory.getDistanceProfile(DistanceProfileFactory.STAMP);

        if (target.shape()[1] == window)
            throw new IllegalArgumentException();

        return matrixProfile(target, window, new LinearOrder((int) (target.length() - window + 1)), stamp, target, true);
    }

    /**
     * Scalable time series anytime matrix profile. Computes a vector(matrix profile)
     * of distances between each subsequence and its nearest neighbor in O(n^2 logn) time
     * complexity and O(n) space complexity. Each distance profile is independent of other
     * distance profiles, the order in which we compute them is random. The random ordering
     * allows interrupt resume(not implemented yet) operation anytime.
     *
     * @param target target time series
     * @param query  query time series
     * @param window size of window
     * @return a Pair with profile matrix as key and profile index as value.
     */
    public Pair<INDArray, INDArray> stamp(INDArray target, INDArray query, int window) {
        DistanceProfile stamp = distanceProfileFactory.getDistanceProfile(DistanceProfileFactory.STAMP);

        int target_shape = (int) target.shape()[1];
        int query_shape = (int) query.shape()[1];
        if (target_shape >= query_shape)
            return matrixProfile(query, window, new RandomOrder((int) (query.length() - window + 1)), stamp, target, false);

        INDArray new_target = CustomOperations.singlePad(target, query_shape - target_shape);

        return matrixProfile(query, window, new RandomOrder((int) (new_target.length() - window + 1)), stamp, new_target, false);
    }

    /**
     * Scalable time series anytime matrix profile (self join). Each distance profile is independent of other
     * distance profiles, the order in which we compute them is random. The random ordering
     * allows interrupt resume(not implemented yet) operation anytime.In case of self join we take into
     * account trivial match. To avoid trivial matches we find the first match then we set an exclusion
     * zone around the best match. The size of exclusion zone in 1/2 of the query size.
     *
     * @param target target time series
     * @param window size of window
     * @return a Pair with profile matrix as key and profile index as value.
     */
    public Pair<INDArray, INDArray> stamp(INDArray target, int window) {
        DistanceProfile stamp = distanceProfileFactory.getDistanceProfile(DistanceProfileFactory.STAMP);

        if (target.shape()[1] == window)
            throw new IllegalArgumentException();

        return matrixProfile(target, window, new RandomOrder((int) (target.length() - window + 1)), stamp, target, true);
    }

    /**
     * Algorithm that computes a vector(matrix profile) of distances between each subsequence and its nearest neighbor in O(n^2 logn) time
     * complexity and O(n) space complexity.
     *
     *
     * @param timeSeriesA the first time series
     * @param window  size of window
     * @param order order
     * @param dp distance profile
     * @param timeSeriesB the second time series
     * @param trivialMatch trivial match
     * @return a Pair with profile matrix as key and profile index as value.
     */
    private Pair<INDArray, INDArray> matrixProfile(INDArray timeSeriesA, int window, Order order, DistanceProfile dp, INDArray timeSeriesB, boolean trivialMatch) {
        int n = (int) timeSeriesB.length();

        INDArray matrixProfile = Nd4j.valueArrayOf(new int[]{1, n - window + 1}, Double.POSITIVE_INFINITY);
        INDArray matrixProfileIndex = Nd4j.valueArrayOf(new int[]{1, n - window + 1}, Double.NaN);

        INDArray distanceProfile;
        INDArray distanceProfileIndex;

        INDArray uptIndices;
        int index = order.getNext();
        while (index != -1) {
            distanceProfile = dp.getDistanceProfile(timeSeriesA, timeSeriesB, index, window);
            distanceProfileIndex = dp.getDistanceProfileIndex(n, index, window);

            if (trivialMatch)
                distanceProfile.put(new INDArrayIndex[]{NDArrayIndex.interval(Math.max(0, index - window / 2), Math.min(index + window / 2 + 1, n))}, Double.POSITIVE_INFINITY);

            //update indices
            uptIndices = CustomOperations.lessThan(distanceProfile, matrixProfile);
            matrixProfileIndex.put(uptIndices, distanceProfileIndex.get(uptIndices));

            //element wise minimum
            matrixProfile = CustomOperations.elementWiseMin(matrixProfile, distanceProfile);

            index = order.getNext();
        }

        return new Pair<>(matrixProfile, matrixProfileIndex);
    }

}
