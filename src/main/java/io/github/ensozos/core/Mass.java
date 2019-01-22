package io.github.ensozos.core;

import io.github.ensozos.utils.CustomOperations;
import org.jtransforms.fft.DoubleFFT_1D;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.indexing.BooleanIndexing;
import static org.nd4j.linalg.ops.transforms.Transforms.pow;
import static org.nd4j.linalg.ops.transforms.Transforms.sqrt;


public class Mass {

    /** small epsilon value used to avoid dividing by 0 */
    private static final double EPS = 1e-40;

    public Mass() {
    }

    /**
     *  Mueen's algorithm for similarity search (MASS 2.0). Mass uses a convolution based method
     *  to calculate sliding dot products in O(n logn). In addition MASS 2.0 use half convolution to compute only
     *  the necessary half. Fast fourier transformation is used as subroutine. The main computation is
     *
     *          dots = ifft( fft(ts) * fft(query))
     *
     *  www.cs.unm.edu/~mueen/FastestSimilaritySearch.html
     *
     * @param ts time series
     * @param query time series
     * @return INDArray with MASS result
     */
    public INDArray mass(INDArray ts, INDArray query) {
        query = zNormalize(query);

        int m = (int) query.length();
        int n = (int) ts.length();

        INDArray stdv = movStd(ts, m);

        //padding query (note nd4j pad method is only for two dimension)

        query = Nd4j.reverse(query);
        if (n - m > 0)
            query = CustomOperations.singlePad(query, n - m);

        Complex[] complexTs = new Complex[n];
        Complex[] complexQuery = new Complex[n];

        for (int i = 0; i < n; i++) {
            complexTs[i] = new Complex(ts.getDouble(i), 0);
            complexQuery[i] = new Complex(query.getDouble(i), 0);
        }

        complexTs = fft1D(complexTs);
        complexQuery = fft1D(complexQuery);


        //multiply two fft results
        Complex[] complexDot = new Complex[n];
        for (int i = 0; i < n; i++) {
            complexDot[i] = complexTs[i].times(complexQuery[i]);
        }

        // inverse fft for dot computation
        complexDot = ifft1D(complexDot);
        double[] realDot = new double[complexDot.length];

        for (int i = 0; i < complexDot.length; i++) {
            realDot[i] = complexDot[i].re;
        }

        INDArray dot = Nd4j.create(realDot, new int[]{1, realDot.length});
        INDArray divRes = dot.get(NDArrayIndex.interval(m - 1, dot.length())).div(stdv);
        INDArray res = divRes.neg().add(m).mul(2);
        BooleanIndexing.replaceWhere(res, EPS, Conditions.lessThanOrEqual(EPS));
        return sqrt(res);
    }


    /**
     * z-normalization of time series with bias corrected param
     * to false.
     *
     * @param ts time series
     * @return INDArray with z normalized time series
     */
    private INDArray zNormalize(INDArray ts) {
        ts = ts.sub(ts.mean());
        double stdev = ts.stdNumber(false).doubleValue();

        if (stdev != 0.0)
            ts = ts.div(stdev);

        return ts;
    }

    /**
     * just in time z-normalization. In one pass calculate cumulative sums of ts
     * and ts^2 and store. Subtract two cumulative sums to obtain the sum over any
     * window. Use the sums to calculate standard deviations of all windows in
     * linear time. Calculated in one linear scan.
     *
     * @param ts the time series to calculate standard deviation
     * @param w  window
     * @return INDArray representing standard deviation of moving window
     */
    private INDArray movStd(INDArray ts, int w) {
        if (w < 1)
            throw new NumberFormatException();

        INDArray cs = CustomOperations.append(Nd4j.create(new int[]{0}, new int[]{1, 1}), ts.cumsum(0));
        INDArray cs2 = CustomOperations.append(Nd4j.create(new int[]{0}, new int[]{1, 1}), pow(ts, 2).cumsum(0));

        INDArray wSum =
                cs.get(NDArrayIndex.interval(w, cs.length())).sub(cs.get(NDArrayIndex.interval(0, cs.length() - w)));
        INDArray wSum2 =
                cs2.get(NDArrayIndex.interval(w, cs2.length())).sub(cs2.get(NDArrayIndex.interval(0, cs2.length() - w)));

        INDArray subResult = wSum2.div(w).sub(pow(wSum.div(w),2));
        BooleanIndexing.replaceWhere(subResult, EPS, Conditions.lessThanOrEqual(EPS));
        return sqrt(subResult);
    }

    /**
     *  wrapper method for JTransform's fast fourier transform.
     *
     * @param signal the series to find fft of.
     * @return Complex array of fft
     */
    private Complex[] fft1D(Complex[] signal) {
        int n = signal.length;
        Complex[] fourier = new Complex[n];

        double[] coeff = new double[2 * n];
        int i = 0;
        for (Complex c : signal) {
            coeff[i++] = c.getReal();
            coeff[i++] = c.getImaginary();
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.complexForward(coeff);

        for (i = 0; i < 2 * n; i += 2) {
            Complex c = new Complex(coeff[i], coeff[i + 1]);
            fourier[i / 2] = c;
        }
        return fourier;
    }

    /**
     *  wrapper method for JTransform's inverse fast fourier
     *  transform.
     *
     * @param fourier
     * @return Complex array of ifft
     */
    private Complex[] ifft1D(Complex[] fourier) {
        int n = fourier.length;
        double s = (1.0 / (double) n);

        Complex[] signal = new Complex[n];
        double[] coeff = new double[2 * n];

        int i = 0;
        for (Complex c : fourier) {
            coeff[i++] = c.getReal();
            coeff[i++] = c.getImaginary();
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.complexInverse(coeff, false);

        for (i = 0; i < 2 * n; i += 2) {
            Complex c = new Complex(s * coeff[i], s * coeff[i + 1]);
            signal[i / 2] = c;
        }
        return signal;
    }
}
