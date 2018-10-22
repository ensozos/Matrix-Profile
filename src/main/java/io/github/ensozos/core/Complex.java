package io.github.ensozos.core;

import java.lang.Math;

public class Complex {

    protected double re;
    protected double im;

    /**
     * Data type for complex numbers.
     *
     * @param real real part of complex number
     * @param imag imaginary part of complex number
     */
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    /**
     * Get the real part of complex number
     *
     * @return real number
     */
    public double getReal() {
        return re;
    }

    /**
     * Get imaginary part of complex number
     *
     * @return imaginary part
     */
    public double getImaginary() {
        return im;
    }

    /**
     * Add complex number.
     * <p>
     * a = 7.0 + 6.0i
     * b = -1.0 + 2.0i
     * <p>
     * a + b = 6.0 + 8.0i
     *
     * @param b complex number to add
     * @return result complex number
     */
    public Complex add(Complex b) {
        re = re + b.re;
        im = im + b.im;
        return this;
    }

    /**
     * Multiply complex number.
     * <p>
     * a = 1+4i
     * b = 5+i
     * <p>
     * a*b = 1+21i
     *
     * @param b complex number to multiply
     * @return result complex number
     */
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    @Override
    public String toString() {
        return re + ((Math.signum(im) >= 0) ? " + " : " - ") + Math.abs(im) + "i";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Complex that = (Complex) obj;
        return (that.re == this.re) && (that.im == this.im);
    }


}