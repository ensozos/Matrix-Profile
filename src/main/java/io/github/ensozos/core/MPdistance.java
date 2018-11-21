package io.github.ensozos.core;

import io.github.ensozos.utils.CustomOperations;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class MPdistance {

    private double threshold;
    private Mass massAlgo;

    /**
     * Matrix Profile distance introduced by Shaghayegh Gharghabi, Shima Imani, Anthony Bagnall,
     * Amirali Darvishzadeh, Eamonn Keogh at "An Ultra-Fast Time Series Distance Measure to
     * allow Data Mining in more Complex Real-World Deployments"
     */
    public MPdistance() {
        this.threshold = 0.05;
        massAlgo = new Mass();
    }

    /**
     * Matrix Profile distance introduced by Shaghayegh Gharghabi, Shima Imani, Anthony Bagnall,
     * Amirali Darvishzadeh, Eamonn Keogh at "An Ultra-Fast Time Series Distance Measure to
     * allow Data Mining in more Complex Real-World Deployments"
     * @param threshold some small fraction of the data length (like 0.05 for example)
     */
    public MPdistance(int threshold) {
        this.threshold = threshold;
        massAlgo = new Mass();
    }

    /**
     * Calculates MP distance given two time series and a subsequence length. In case
     * of different size of time series it works as query by content. This method is based
     * on Speeding up MPdist Search section of the paper.
     * <p>
     * Note: current version is not fully optimized (see centeredMovingMinimum).
     *
     * @param ts1    first time series
     * @param ts2    second time series
     * @param subLen subsequence length
     * @return MP distance measure
     */
    public INDArray getMPdistance(INDArray ts1, INDArray ts2, int subLen) {
        if (ts1.length() < ts2.length()) {
            INDArray temp = ts1;
            ts1 = ts2;
            ts2 = temp;
        }

        INDArray massDistMatrix = getMassDistMatrix(ts1, ts2, subLen);
        int massDistMatrixRow = (int) massDistMatrix.shape()[0];
        int massDistMatrixCol = (int) massDistMatrix.shape()[1];

        INDArray massDistSlideMin = Nd4j.zeros(massDistMatrixRow, massDistMatrixCol);
        INDArray allRightHistogram = massDistMatrix.amin(0);
        for (int i = 0; i < massDistMatrixRow; i++) {
            massDistSlideMin.putRow(i, CustomOperations.centeredMovingMinimum(massDistMatrix.getRow(i), massDistMatrixRow));
        }

        int MPdistLength = (int) (ts1.length() - ts2.length() + 1);
        int rightHistLength = (int) (ts2.length() - subLen + 1);

        INDArray MPdistArray = Nd4j.zeros(MPdistLength);
        INDArray leftHist = Nd4j.zeros(rightHistLength);

        INDArray rightHist;
        for (int i = 0; i < MPdistLength; i++) {
            rightHist = allRightHistogram.get(NDArrayIndex.interval(i, rightHistLength + i));
            leftHist.putRow(0, massDistSlideMin.getColumn((long) (i + Math.floor(massDistMatrixRow / 2))));

            MPdistArray.put(0, i, calcMPdist(Nd4j.concat(1, leftHist, rightHist), (int) (2 * ts2.length())));
        }


        return MPdistArray;
    }

    /**
     * Basic algorithm that finds MP distance given the matrix profile.
     *
     * @param matrixProfile matrix profile of two time series
     * @param dataLength    length of the data
     * @return mp distance
     */
    private double calcMPdist(INDArray matrixProfile, int dataLength) {
        int k = (int) Math.ceil(threshold * dataLength);

        INDArray mpSorted = Nd4j.sort(matrixProfile, true);
        Nd4j.clearNans(mpSorted);

        if (mpSorted.length() == 0) {
            return Double.POSITIVE_INFINITY;
        } else if (mpSorted.length() >= k) {
            return mpSorted.getDouble(0, k);
        } else {
            return mpSorted.getDouble(0, mpSorted.length() - 1);
        }

    }

    /**
     * Calculates distance matrix with MASS V2.0 given
     * two time series.
     *
     * @param ts1    first time series
     * @param ts2    second time series
     * @param sublen subsequence length
     * @return MASS distance matrix
     */
    private INDArray getMassDistMatrix(INDArray ts1, INDArray ts2, int sublen) {
        int numberOfSubSeq = (int) (ts2.length() - sublen + 1);

        INDArray massDist = Nd4j.zeros(numberOfSubSeq, ts1.length() - sublen + 1);
        for (int i = 0; i < numberOfSubSeq; i++) {
            massDist.putRow(i, massAlgo.mass(ts1, ts2.get(NDArrayIndex.interval(i, i + sublen))));
        }

        return massDist;
    }

}
