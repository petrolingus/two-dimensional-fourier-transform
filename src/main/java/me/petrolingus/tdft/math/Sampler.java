package me.petrolingus.tdft.math;

import java.util.List;
import java.util.function.Function;

public class Sampler {

    private double ax;
    private double bx;
    private double ay;
    private double by;
    private int nx;
    private int ny;

    public Sampler(double ax, double bx, double ay, double by, int nx, int ny) {
        this.ax = ax;
        this.bx = bx;
        this.ay = ay;
        this.by = by;
        this.nx = nx;
        this.ny = ny;
    }

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

    public double[][] getSamples(List<Function<Point, Double>> functions) {

        double hx = (bx - ax) / (nx - 1);
        double hy = (by - ay) / (ny - 1);

        double[] xs = new double[nx];
        for (int i = 0; i < nx; i++) {
            xs[i] = ax + hx * i;
        }

        double[] ys = new double[ny];
        for (int i = 0; i < ny; i++) {
            ys[i] = ay + hy * i;
        }

        double[][] samples = new double[nx][ny];
        for (int j = 0; j < ny; j++) {
            double y = ys[j];
            for (int i = 0; i < nx; i++) {
                double x = xs[i];
                for (Function<Point, Double> pointDoubleFunction : functions) {
                    samples[i][j] += pointDoubleFunction.apply(new Point(x, y));
                }
            }
        }

        return samples;
    }
}
