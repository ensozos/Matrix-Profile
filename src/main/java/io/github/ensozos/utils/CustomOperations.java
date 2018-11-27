package io.github.ensozos.utils;

import com.google.common.base.Function;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Condition;
import org.nd4j.linalg.indexing.conditions.Conditions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Code from AlexDBlack
 * <p>
 * These are some functions which I couldn't find in the ND4J library, so I implemented them myself.
 * You can see the usages in the file NumpyCheatSheat.java
 * <p>
 * Refer to
 * https://github.com/deeplearning4j/dl4j-examples/tree/master/nd4j-examples/
 * src/main/java/org/nd4j/examples/numpy_cheatsheat
 *
 * @author Shams Ul Azeem
 */
interface Predicate<T> {
    boolean test(T t);
}

public class CustomOperations {

    public CustomOperations() {
    }


    public static INDArray elementWiseMin(INDArray arr1, INDArray arr2) {
        if (arr1.length() != arr2.length()) throw new IllegalArgumentException();

        double[] a = arr1.data().asDouble();
        double[] b = arr2.data().asDouble();

        double[] values = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            values[i] = a[i] < b[i] ? a[i] : b[i];
        }

        return Nd4j.create(values, new int[]{1, values.length});
    }

    public static INDArray singlePad(INDArray arr, int length) {
        double[] pad = new double[length];
        Arrays.fill(pad, 0);

        return append(arr, Nd4j.create(pad, new int[]{1, length}));
    }

    public static INDArray centeredMovingMinimum(INDArray arr, int k) {
        INDArray resutl = Nd4j.zeros(1, arr.length());
        int arrSize = (int) arr.length();

        int interval = k / 2;
        double minVal;
        if (k % 2 != 0) {
            for (int i = 0; i < arr.length(); i++) {
                minVal = arr.get(NDArrayIndex.interval(Math.max(0, i - interval), Math.min(i + interval, arrSize), true)).minNumber().doubleValue();
                resutl.put(0, i, minVal);
            }
        } else {
            for (int i = 0; i < arr.length(); i++) {
                minVal = arr.get(NDArrayIndex.interval(Math.max(0, (i - 1) - (interval - 1)), Math.min((i - 1) + interval, arrSize), true)).minNumber().doubleValue();
                resutl.put(0, i, minVal);
            }
        }

        return resutl;
    }

    public static INDArray lessThan(INDArray arr1, INDArray arr2) {
        if (arr1.length() != arr2.length()) throw new IllegalArgumentException();

        float[] a = arr1.data().asFloat();
        float[] b = arr2.data().asFloat();

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            if (a[i] <= b[i])
                indices.add(i);
        }

        double[][] arrIndices = new double[2][indices.size()];
        for (int i = 0; i < arrIndices[0].length; i++) {
            arrIndices[0][i] = 0;
        }

        for (int i = 0; i < arrIndices[0].length; i++) {
            arrIndices[1][i] = indices.get(i);
        }


        return Nd4j.create(arrIndices);
    }

    public static INDArray append(INDArray arr1, INDArray values) {
        return append(arr1, values, -1);
    }

    public static INDArray append(INDArray arr1, INDArray values, int dimension) {
        if (dimension == -1) {
            return Nd4j.toFlattened(arr1, values);
        } else {
            return Nd4j.concat(dimension, arr1, values);
        }
    }

    public static INDArray insert(INDArray arr1, int index, INDArray values) {
        return insert(arr1, index, values, -1);
    }

    public static INDArray insert(INDArray arr1, int index, INDArray values, int dimension) {
        if (dimension == -1) {
            INDArray flat1 = Nd4j.toFlattened(arr1);
            INDArray flatValues = Nd4j.toFlattened(values);
            INDArray firstSlice = flat1.get(NDArrayIndex.interval(0, index));
            INDArray secondSlice = flat1.get(NDArrayIndex.interval(index, flat1.length()));
            return Nd4j.toFlattened(firstSlice, flatValues, secondSlice);
        } else {
            INDArray firstSlice = arr1.get(createIntervalOnDimension(dimension, false,
                    0, index));
            INDArray secondSlice = arr1.get(createIntervalOnDimension(dimension, false,
                    index, arr1.shape()[dimension]));
            return Nd4j.concat(dimension, firstSlice, values, secondSlice);
        }
    }

    public static INDArray delete(INDArray arr1, int... interval) {
        return delete(-1, arr1, interval);
    }

    public static INDArray delete(int dimension, INDArray arr1, int... interval) {
        int length = interval.length;
        int lastIntervalValue = interval[length - 1];

        if (dimension == -1) {
            INDArray array1 = arr1.get(NDArrayIndex.interval(0, interval[0]));
            if (lastIntervalValue == arr1.length() - 1) {
                return Nd4j.toFlattened(array1);
            } else {
                INDArray array2 = arr1.get(NDArrayIndex.interval(lastIntervalValue + 1,
                        arr1.length()));
                return Nd4j.toFlattened(array1, array2);
            }
        } else {
            INDArray array1 = arr1.get(createIntervalOnDimension(dimension, false, 0, interval[0]));
            if (lastIntervalValue == arr1.shape()[dimension] - 1) {
                return array1;
            } else {
                INDArray array2 = arr1.get(createIntervalOnDimension(dimension, false,
                        lastIntervalValue + 1,
                        arr1.shape()[dimension]));
                return Nd4j.concat(dimension, array1, array2);
            }
        }
    }

    public static INDArray[] split(INDArray arr1, int numOfSplits) {
        return split(arr1, numOfSplits, -1);
    }

    public static INDArray[] split(INDArray arr1, int numOfSplits, int dimension) {
        dimension = dimension == -1 ? 0 : dimension;
        INDArray[] splits = new INDArray[numOfSplits];
        long intervalLength = arr1.shape()[dimension] / numOfSplits;

        for (int i = 0; i < numOfSplits; i++) {
            splits[i] = arr1.get(createIntervalOnDimension(dimension,
                    false,
                    intervalLength * i, intervalLength * (i + 1)));
        }
        return splits;
    }


    public static INDArray booleanOp(INDArray arr, Condition condition) {
        INDArray dup = arr.dup();
        BooleanIndexing.applyWhere(dup, condition,
                new Function<Number, Number>() {
                    @Override
                    public Number apply(Number number) {
                        return 1.0;
                    }
                }, new Function<Number, Number>() {
                    @Override
                    public Number apply(Number number) {
                        return 0.0;
                    }
                });
        return dup;
    }

    public static INDArray invert(INDArray arr1) {
        return booleanOp(arr1, Conditions.equals(0));
    }


    public static INDArrayIndex[] createIntervalOnDimension(int dimension, boolean inclusive, long... interval) {
        INDArrayIndex[] indexInterval = new INDArrayIndex[dimension + 1];

        for (int i = 0; i <= dimension; i++) {
            indexInterval[i] = i != dimension ?
                    NDArrayIndex.all() :
                    NDArrayIndex.interval((int) interval[0], (int) interval[1], inclusive);
        }

        return indexInterval;
    }
}