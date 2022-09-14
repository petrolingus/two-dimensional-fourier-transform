package me.petrolingus.tdft;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import me.petrolingus.tdft.math.*;
import me.petrolingus.tdft.math.core.GaussianParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class TwoDimensionalFourierTransform {

    public static double[][] generateSamples(GaussianParameters[] parameters, int quality, double bound) {

        Sampler sampler = new Sampler(-bound, bound, -bound, bound, quality, quality);

        List<Function<Point, Double>> list = new ArrayList<>();
        for (GaussianParameters gaussianParameter : parameters) {
            list.add(Functions.gaussian(gaussianParameter));
        }

        return sampler.getSamples(list);
    }

    public static void normalize(double[][] samples) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double[] row : samples) {
            for (double sample : row) {
                if (sample > max) {
                    max = sample;
                }
                if (sample < min) {
                    min = sample;
                }
            }
        }
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length; j++) {
                samples[i][j] = normalize(samples[i][j], min, max);
            }
        }
    }

    public static Image generateImage(int width, int height, double[][] samples) {
        int[] pixels = new int[width * height];
        for (int i = 0; i < pixels.length; i++) {
            int col = (i % width) * samples.length / width;
            int row = (i / width) * samples.length / width;
            int value = (int) Math.floor(255.0 * samples[col][samples.length - row - 1]);
            pixels[i] = (0xFF << 24) | (value << 16) | (value << 8) | value;
        }

        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        return image;
    }

    public static Complex[][] transform(double[][] samples) {

        int width = samples.length;

        // Step 1
        Complex[][] complexSamples = new Complex[width][width];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < width; i++) {
                double value = samples[j][i];
                Complex complex = new Complex(value);
                complexSamples[i][j] = complex;
            }
        }

        // Step 2
        Complex[][] fft1 = new Complex[width][width];
        for (int i = 0; i < width; i++) {
            Complex[] column = new Complex[width];
            for (int j = 0; j < width; j++) {
                column[j] = complexSamples[j][i];
            }
            Complex[] columnFft = FastFourierTransform.fft(column);
            for (int j = 0; j < width; j++) {
                fft1[j][i] = columnFft[j];
            }
        }

        // Step 3
        Complex[][] fft2 = new Complex[width][width];
        for (int i = 0; i < width; i++) {
            Complex[] row = new Complex[width];
            System.arraycopy(fft1[i], 0, row, 0, width);
            Complex[] rowFft = FastFourierTransform.fft(row);
            System.arraycopy(rowFft, 0, fft2[i], 0, width);
        }

        Complex[][] result = new Complex[width][width];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < width; i++) {
                Complex value = fft2[j][i];
                if (j < width / 2 && i < width / 2) {
                    result[i + width / 2][j + width / 2] = value;
                } else if (j >= width / 2 && i >= width / 2) {
                    result[i - width / 2][j - width / 2] = value;
                } else if (j < width / 2 && i >= width / 2) {
                    result[i - width / 2][j + width / 2] = value;
                } else {
                    result[i + width / 2][j - width / 2] = value;
                }
            }
        }

        return result;
    }

    public static Complex[][] itransform(Complex[][] samples) {

        int width = samples.length;

        // Step 2
        Complex[][] fft1 = new Complex[width][width];
        for (int i = 0; i < width; i++) {
            Complex[] column = new Complex[width];
            for (int j = 0; j < width; j++) {
                column[j] = samples[i][j];
            }
            Complex[] columnFft = FastFourierTransform.ifft(column);
            for (int j = 0; j < width; j++) {
                fft1[j][i] = columnFft[j];
            }
        }

        // Step 3
        Complex[][] fft2 = new Complex[width][width];
        for (int i = 0; i < width; i++) {
            Complex[] row = new Complex[width];
            System.arraycopy(fft1[i], 0, row, 0, width);
            Complex[] rowFft = FastFourierTransform.ifft(row);
            System.arraycopy(rowFft, 0, fft2[i], 0, width);
        }

        Complex[][] result = new Complex[width][width];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < width; i++) {
                Complex value = fft2[j][i];
                result[i][j] = value;
//                if (j < width / 2 && i < width / 2) {
//                    result[i + width / 2][j + width / 2] = value;
//                } else if (j >= width / 2 && i >= width / 2) {
//                    result[i - width / 2][j - width / 2] = value;
//                } else if (j < width / 2 && i >= width / 2) {
//                    result[i - width / 2][j + width / 2] = value;
//                } else {
//                    result[i + width / 2][j - width / 2] = value;
//                }
            }
        }

        return result;
    }

    public static double[][] process(Complex[][] complexes) {

        double[][] res = new double[complexes.length][complexes.length];

        for (int j = 0; j < complexes.length; j++) {
            for (int i = 0; i < complexes.length; i++) {
                double x = complexes[j][i].getX();
                double y = complexes[j][i].getY();
                double value = Math.log10(Math.sqrt(x * x + y * y));
                int v = complexes.length;
                res[j][i] = value;
//                if (j < v / 2 && i < v / 2) {
//                    res[i + v / 2][j + v / 2] = value;
//                } else if (j >= v / 2 && i >= v / 2) {
//                    res[i - v / 2][j - v / 2] = value;
//                } else if (j < v / 2 && i >= v / 2) {
//                    res[i - v / 2][j + v / 2] = value;
//                } else {
//                    res[i + v / 2][j - v / 2] = value;
//                }
            }
        }
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < complexes.length; i++) {
            for (int j = 0; j < complexes.length; j++) {
                if (res[i][j] > max) {
                    max = res[i][j];
                }
                if (res[i][j] < min) {
                    min = res[i][j];
                }
            }
        }
        for (int i = 0; i < complexes.length; i++) {
            for (int j = 0; j < complexes.length; j++) {
                res[i][j] = TwoDimensionalFourierTransform.normalize(res[i][j], min, max);
            }
        }

        return res;
    }

    public static double[][] highPassFiltering(double threshold, Complex[][] fftData, double[][] data) {
        double[][] result = new double[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                double d = Math.sqrt(Math.pow(i - data.length / 2.0, 2) + Math.pow(j - data.length / 2.0, 2));
                double value = data[i][j];
                result[i][j] = d > threshold ? value : 0;
                fftData[i][j] = d > threshold ? fftData[i][j] : Complex.ZERO;
            }
        }
        return result;
    }

    public static double[][] lowPassFiltering(double threshold, Complex[][] fftData, double[][] data) {
        double[][] result = new double[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                double d = Math.sqrt(Math.pow(i - data.length / 2.0, 2) + Math.pow(j - data.length / 2.0, 2));
                double value = data[i][j];
                result[i][j] = d < threshold ? value : 0;
                fftData[i][j] = d < threshold ? fftData[i][j] : Complex.ZERO;
            }
        }
        return result;
    }

    public static double[][] lowAmplitudeFiltering(double threshold, Complex[][] fftData, double[][] data) {
        double[][] result = new double[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                double value = data[i][j];
                result[i][j] = value > threshold ? value : 0;
                fftData[i][j] = value > threshold ? fftData[i][j] : Complex.ZERO;
            }
        }
        return result;
    }

    public static double[][] highAmplitudeFiltering(double threshold, Complex[][] fftData, double[][] data) {
        double[][] result = new double[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                double value = data[i][j];
                result[i][j] = value < threshold ? value : 0;
                fftData[i][j] = value < threshold ? fftData[i][j] : Complex.ZERO;
            }
        }
        return result;
    }

    /**
     * Calculates a value between 0 and 1, given the precondition that value
     * is between min and max. 0 means value = max, and 1 means value = min.
     */
    static double normalize(double value, double min, double max) {
        return ((value - min) / (max - min));
    }

    public static void noise(double[][] samples, double noiseLevel) {

        double[][] noise = new double[samples.length][samples.length];
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length; j++) {
                noise[i][j] = ThreadLocalRandom.current().nextGaussian();
            }
        }

        // noise energy
        double noiseEnergy = 0;
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length; j++) {
                noiseEnergy += Math.pow(noise[i][j], 2);
            }
        }

        // signal energy
        double signalEnergy = 0;
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length; j++) {
                signalEnergy += Math.pow(samples[i][j], 2);
            }
        }

        double noiseCoefficient = noiseLevel;
        double alpha = Math.sqrt((noiseCoefficient / 100.0) * (signalEnergy / noiseEnergy));
        System.out.println("ALPHA: " + alpha);
//
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length; j++) {
//                double value = samples[i][j];
                double value = samples[i][j] + alpha * noise[i][j];
                samples[i][j] = value;
                if (value > max) {
                    max = value;
                }
                if (value < min) {
                    min = value;
                }
            }
        }
        System.out.println(min);
        System.out.println(max);

        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length; j++) {
                samples[i][j] = normalize(samples[i][j], min, max);
            }
        }
    }
}
