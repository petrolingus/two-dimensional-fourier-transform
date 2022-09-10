package me.petrolingus.tdft.math;

import java.awt.*;
import java.util.function.Function;

public class Sampler {

    public static double[] getSamples(Function<Double, Double> function, double a, double b, int n) {

        if (a >= b) {
            throw new IllegalArgumentException("Parameter a must be less than b!");
        }

        if (n < 2) {
            throw new IllegalArgumentException("Parameter n must be more than 2!");
        }

        double h = (b - a) / (n - 1);

        double[] samples = new double[n];
        for (int i = 0; i < n; i++) {
            double x = a + h * i;
            samples[i] = function.apply(x);
        }

        return samples;
    }

    public static double[][] getSamples(Function<Point, Double> function, double ax, double bx, double ay, double by, int nx, int ny) {

        double hx = (bx - ax) / (nx - 1);
        double hy = (by - ay) / (ny - 1);

        double[][] samples = new double[nx][ny];
        for (int j = 0; j < ny; j++) {
            double y = ay + hy * j;
            for (int i = 0; i < nx; i++) {
                double x = ax + hx * i;
                samples[j][i] = function.apply(new Point(x, y));
            }
        }

        return samples;
    }
}
