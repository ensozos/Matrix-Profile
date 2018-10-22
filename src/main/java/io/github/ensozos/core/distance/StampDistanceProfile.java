package io.github.ensozos.core.distance;

import io.github.ensozos.core.DistanceProfile;
import io.github.ensozos.core.Mass;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;


public class StampDistanceProfile implements DistanceProfile {

    /*
     *  MASS 2.0 the main algorithm of distance profile
     */
    private Mass massAlgo;

    public StampDistanceProfile() {
        massAlgo = new Mass();
    }

    @Override
    public INDArray getDistanceProfile(INDArray tsA, INDArray tsB, int index, int w) {
        INDArray query = tsA.get(NDArrayIndex.interval(index, index + w));

        return massAlgo.mass(tsB, query);
    }

    @Override
    public INDArray getDistanceProfileIndex(int n, int index, int w) {
        return Nd4j.valueArrayOf(new int[]{1, n - w + 1}, (double) index);
    }
}
