package me.petrolingus.tdft.core.math;

public class FastFourierTransform {

    // Compute the FFT of x[], assuming its length n is a power of 2
    public static Complex[] fft(Complex[] x) {

        int n = x.length;

        if (n == 1) return new Complex[]{x[0]};

        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        Complex[] buffer = new Complex[n / 2];

        for (int k = 0; k < n / 2; k++) {
            buffer[k] = x[2 * k];
        }
        Complex[] evenFFT = fft(buffer);

        for (int k = 0; k < n / 2; k++) {
            buffer[k] = x[2 * k + 1];
        }
        Complex[] oddFFT = fft(buffer);

        Complex[] result = new Complex[n];
        double h = -2.0 * Math.PI / n;
        for (int k = 0; k < n / 2; k++) {
            double kth = k * h;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            result[k] = evenFFT[k].plus(wk.times(oddFFT[k]));
            result[k + n / 2] = evenFFT[k].minus(wk.times(oddFFT[k]));
        }
        return result;
    }

    public static Complex[] ifft(Complex[] x) {
        int n = x.length;
        Complex[] result = new Complex[n];
        for (int i = 0; i < n; i++) {
            result[i] = x[i].conjugate();
        }
        result = fft(result);
        for (int i = 0; i < n; i++) {
            result[i] = result[i].conjugate();
        }
        for (int i = 0; i < n; i++) {
            result[i] = result[i].scale(1.0 / n);
        }
        return result;
    }
}
