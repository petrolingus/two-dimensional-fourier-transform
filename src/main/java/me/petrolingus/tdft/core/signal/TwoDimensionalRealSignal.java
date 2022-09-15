package me.petrolingus.tdft.core.signal;

import javafx.scene.image.*;
import me.petrolingus.tdft.Main;
import me.petrolingus.tdft.core.Algorithm;
import me.petrolingus.tdft.core.math.Point;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Representation of a two-dimensional signal (image) as a set of samples in the range from 0 to 1
 */
public class TwoDimensionalRealSignal {

    private int width;
    private int height;

    private double[][] samples;

    public TwoDimensionalRealSignal(int width, int height, double[][] samples) {
        this.width = width;
        this.height = height;
        this.samples = new double[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(samples[i], 0, this.samples[i], 0, width);
        }
    }

    public TwoDimensionalRealSignal(String name) {
        load(name);
        normalize();
    }

    public TwoDimensionalRealSignal(int width, int height, List<Function<Point, Double>> functions) {
        this.width = width;
        this.height = height;
        this.samples = new double[height][width];
        generate(functions);
        normalize();
    }

    public void load(String name) {
        URL resource = Main.class.getResource(name);
        File file = null;
        try {
            assert resource != null;
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Image image = new Image(file.toURI().toString());
        width = (int) image.getWidth();
        height = (int) image.getHeight();
        PixelReader pixelReader = image.getPixelReader();

        int[] pixels = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), pixels, 0, width);

        samples = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int value = pixels[j + width * i];
                int r = value & 0xFF0000;
                int g = value & 0xFF00;
                int b = value & 0xFF;
                samples[height - i - 1][j] = 0.299 * r + 0.587 * g + 0.144 * b;
            }
        }
    }

    private void generate(List<Function<Point, Double>> functions) {

        double[] xs = createSamplingPoints(-1, 1, width);
        double[] ys = createSamplingPoints(-1, 1, height);

        for (int i = 0; i < height; i++) {
            double y = ys[i];
            for (int j = 0; j < width; j++) {
                double x = xs[j];
                for (Function<Point, Double> function : functions) {
                    samples[i][j] += function.apply(new Point(x, y));
//                    samples[i][j] += 1.0;
                }
            }
        }
    }

    public void normalize() {

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double[] row : samples) {
            for (double sample : row) {
                max = Math.max(sample, max);
                min = Math.min(sample, min);
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                samples[i][j] = (samples[i][j] - min) / (max - min);
            }
        }
    }

    public Image getImage() {

        int[] pixels = new int[width * height];

        for (int i = 0; i < pixels.length; i++) {
            int col = (i % width);
            int row = (i / width);
            int value = (int) Math.round(255.0 * samples[height - row - 1][col]);
            pixels[i] = (0xFF << 24) | (value << 16) | (value << 8) | value;
        }

        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        return image;
    }

    public static double[] createSamplingPoints(double from, double to, int count) {
        double h = (to - from) / (count - 1);
        double[] points = new double[count];
        for (int i = 0; i < count; i++) {
            points[i] = from + h * i;
        }
        return points;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double[][] getSamples() {
        double[][] result = new double[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(samples[i], 0, result[i], 0, width);
        }
        return result;
    }

    public void calcEnergy() {
        double result = 0;
        for (double[] row : samples) {
            for (double value : row) {
                result += value * value;
            }
        }
        System.out.println(result);
    }
}
