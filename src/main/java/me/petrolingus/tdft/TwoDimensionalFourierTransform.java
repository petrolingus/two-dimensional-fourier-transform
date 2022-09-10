package me.petrolingus.tdft;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import me.petrolingus.tdft.math.*;
import me.petrolingus.tdft.math.core.GaussianParameters;

import java.util.function.Function;

public class TwoDimensionalFourierTransform {

    public static double[][] generateSamples(GaussianParameters parameters, int quality) {

        double s = 1;
        Function<Point, Double> gaussian = Functions.gaussian(parameters);
        double[][] samples = Sampler.getSamples(gaussian, -s, s, -s, s, quality, quality);

        return samples;
    }

    public static Image generateImage(int width, int height, double[][] samples) {
        int[] pixels = new int[width * height];
        for (int i = 0; i < pixels.length; i++) {
            int col = (i % width) * samples.length / width;
            int row = (i / width) * samples.length / width;
            int value = (int) Math.round(255 * samples[col][samples.length - row - 1]);
            pixels[i] = (0xFF << 24) | (value << 16) | (value << 8) | value;
        }

        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        return image;
    }

    public static double[][] transform(double[][] samples) {

        int width = samples.length;
        int height = samples.length;
        System.out.println(width);

        Complex[][] complexSamples = new Complex[width][height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double value = samples[j][i];
                Complex complex = new Complex(value);
                complexSamples[j][i] = complex;
            }
        }

        Complex[][] fft1 = new Complex[width][height];
        for (int j = 0; j < height; j++) {
            fft1[j] = FastFourierTransform.fft(complexSamples[j]);
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (fft1[i][j] == null) {
                    System.err.println("wtf!");
                }
            }
        }

        Complex[][] fft2 = new Complex[width][height];
        for (int i = 0; i < width; i++) {
            Complex[] column = new Complex[height];
            for (int j = 0; j < height; j++) {
                column[j] = new Complex(fft1[j][i].getX(), fft1[j][i].getY());
            }
            fft2[i] = FastFourierTransform.fft(column);
        }

        double[][] result = new double[width][height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double x = fft2[j][i].getX();
                double y = fft2[j][i].getY();
                result[j][i] = Math.sqrt(x * x + y * y);
            }
        }

        double max = -1;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (result[i][j] > max) {
                    max = result[i][j];
                }
                if (result[i][j] < min) {
                    min = result[i][j];
                }
            }
        }


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = normalize(result[i][j], min, max);
                if (result[i][j] < 0) {
                    System.err.println("<0");
                }
                if (result[i][j] > 1) {
                    System.err.println(">1");
                }
            }
        }

        System.out.println("min "+min);
        System.out.println("max "+max);

        return result;
    }

    /**
     * Calculates a value between 0 and 1, given the precondition that value
     * is between min and max. 0 means value = max, and 1 means value = min.
     */
    static double normalize(double value, double min, double max) {
        return 1 - ((value - min) / (max - min));
    }

}
