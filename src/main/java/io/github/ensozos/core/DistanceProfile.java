package io.github.ensozos.core;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface DistanceProfile {

    /**
     *  Returns the distance profile given two time series index and window
     *
     * @param tsA time series
     * @param tsB time series
     * @param index current index
     * @param window window size
     * @return distance profile
     */
    INDArray getDistanceProfile(INDArray tsA, INDArray tsB, int index, int window);

    /**
     *  Returns the distance profile index given index, window and n the size of target
     *  time series.
     *
     * @param n size of target time series
     * @param index the current index
     * @param window the size of window
     * @return distance profile index
     */
    INDArray getDistanceProfileIndex(int n,int index, int window);
}
