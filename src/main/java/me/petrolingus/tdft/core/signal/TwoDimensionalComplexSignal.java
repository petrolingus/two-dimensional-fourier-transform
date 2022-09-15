package me.petrolingus.tdft.core.signal;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import me.petrolingus.tdft.core.math.Complex;
import me.petrolingus.tdft.core.math.FastFourierTransform;

public class TwoDimensionalComplexSignal {

    private final int width;
    private final int height;

    private Complex[][] samples;

    public TwoDimensionalComplexSignal(TwoDimensionalRealSignal signal) {
        this.width = signal.getWidth();
        this.height = signal.getHeight();
        transform(signal.getSamples());
    }

    private void transform(double[][] signal) {

        // Step 1
        Complex[][] complexSamples = new Complex[width][width];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < width; i++) {
                double value = signal[j][i];
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

        samples = new Complex[width][width];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < width; i++) {
                Complex value = fft2[j][i];
                if (j < width / 2 && i < width / 2) {
                    samples[i + width / 2][j + width / 2] = value;
                } else if (j >= width / 2 && i >= width / 2) {
                    samples[i - width / 2][j - width / 2] = value;
                } else if (j < width / 2 && i >= width / 2) {
                    samples[i - width / 2][j + width / 2] = value;
                } else {
                    samples[i + width / 2][j - width / 2] = value;
                }
            }
        }

    }

    public Image getImage() {

        int[] pixels = new int[width * height];

//        double[][] data = new double[height][width];
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                data[i][j] = Math.log1p(samples[i][j].magnitude());
//            }
//        }
//        normalize(data);

        double[][] data = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double d = Math.sqrt(Math.pow(width / 2.0 - i, 2) + Math.pow(height / 2.0 - j, 2));
                double value = samples[i][j].magnitude();
                data[i][j] = d < 30 ? 0 : value;
            }
        }
        normalize(data);

        for (int i = 0; i < pixels.length; i++) {
            int col = (i % width);
            int row = (i / width);
            double value = data[height - row - 1][col];
            int color = (int) Math.round(255.0 * value);
            pixels[i] = (0xFF << 24) | (color << 16) | (color << 8) | color;
        }

        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        return image;
    }

    private void normalize(double[][] data) {

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double[] row : data) {
            for (double sample : row) {
                max = Math.max(sample, max);
                min = Math.min(sample, min);
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                data[i][j] = (data[i][j] - min) / (max - min);
            }
        }
    }
}
