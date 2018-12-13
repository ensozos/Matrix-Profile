package io.github.ensozos.core;

import io.github.ensozos.core.order.Order;
import io.github.ensozos.utils.CustomOperations;
import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import java.util.concurrent.*;


/**
 * Calculates the matrix profile given parameters either serially or concurrently.
 */
class MatrixProfileCalculator {

    /** The maximum number of minutes to wait for processing to complete before forcefully terminating */
    private static final int MAX_MINUTES = 60;

    private INDArray matrixProfile;
    private INDArray matrixProfileIndex;
    private int n;

    private INDArray timeSeriesA;
    private INDArray timeSeriesB;

    private int window;
    private Order order;
    private DistanceProfile dp;
    private boolean trivialMatch;


    /**
     *  Algorithm that computes a vector(matrix profile) of distances between each subsequence and its nearest neighbor
     *  in O(n^2 logn) time complexity and O(n) space complexity.
     */
    MatrixProfileCalculator(INDArray timeSeriesA, int window, Order order, DistanceProfile dp,
                                   INDArray timeSeriesB, boolean trivialMatch) {
        n = (int) timeSeriesB.length();
        matrixProfile = Nd4j.valueArrayOf(new int[]{1, n - window + 1}, Double.POSITIVE_INFINITY);
        matrixProfileIndex = Nd4j.valueArrayOf(new int[]{1, n - window + 1}, Double.NaN);

        this.timeSeriesA = timeSeriesA;
        this.timeSeriesB = timeSeriesB;
        this.window = window;
        this.order = order;
        this.dp = dp;
        this.trivialMatch = trivialMatch;
    }

    /**
     * @return a Pair with profile matrix as key and profile index as value.
     */
    Pair<INDArray, INDArray> calculateSerially() {

        int index = order.getNext();

        while (index != -1) {
            new MPRunnable(index).run();
            index = order.getNext();
        }

        return new Pair<>(matrixProfile, matrixProfileIndex);
    }

    /**
     * Use fixed thread pool to execute concurrently using available processors.
     * @return a Pair with profile matrix as key and profile index as value.
     */
    Pair<INDArray, INDArray> calculateConcurrently() {

        int numProcs = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numProcs);

        int index = order.getNext();

        while (index != -1) {
            executor.execute(new MPRunnable(index));
            index = order.getNext();
        }
        shutdown(executor);

        return new Pair<>(matrixProfile, matrixProfileIndex);
    }

    /**
     * Gracefully shuts down the executor service.
     * If it does not finish in a reasonable time, it is forcefully terminated.
     * @param executor the executor service to shutdown
     */
    private void shutdown(ExecutorService executor) {
        try {
            //System.out.println("attempting to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(MAX_MINUTES, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
    }


    private class MPRunnable implements Runnable{

        int index;

        MPRunnable(int i){
            this.index = i;
        }

        public void run(){
            INDArray distanceProfile = dp.getDistanceProfile(timeSeriesA, timeSeriesB, index, window);
            INDArray distanceProfileIndex = dp.getDistanceProfileIndex(n, index, window);

            if (trivialMatch) {
                INDArrayIndex[] indices = new INDArrayIndex[]{
                        NDArrayIndex.interval(Math.max(0, index - window / 2), Math.min(index + window / 2 + 1, n))
                };
                distanceProfile.put(indices, Double.POSITIVE_INFINITY);
            }

            updateProfile(distanceProfile, distanceProfileIndex);
        }
    }

    private synchronized void updateProfile(INDArray distanceProfile, INDArray distanceProfileIndex) {
        INDArray uptIndices = CustomOperations.lessThan(distanceProfile, matrixProfile);
        //System.out.println("Now updating these indices " + Arrays.toString(uptIndices.data().asInt()));

        // if the indices array are zero length keep the old matrixProfileIndex
        if (uptIndices.shape()[1] > 0)
            matrixProfileIndex.put(uptIndices, distanceProfileIndex.get(uptIndices));

        matrixProfile = CustomOperations.elementWiseMin(matrixProfile, distanceProfile);
    }
}
