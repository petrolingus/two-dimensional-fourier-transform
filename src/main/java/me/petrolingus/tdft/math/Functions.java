package me.petrolingus.tdft.math;

import javafx.util.Pair;
import me.petrolingus.tdft.math.core.GaussianParameters;

import java.awt.*;
import java.util.function.Function;

public class Functions {

    /**
     * @param a double value
     * @param b double value
     * @param c double value
     * @param d double value
     * @return a + b * sin(c * x + d)
     */
    public static Function<Double, Double> sin(double a, double b, double c, double d) {
        return x -> a + b * Math.sin(c * x + d);
    }

    public static Function<Point, Double> gaussian(GaussianParameters parameters) {
        double a = parameters.a;
        double x0 = parameters.x0;
        double y0 = parameters.y0;
        double sx = parameters.sx;
        double sy = parameters.sy;
        return (p) -> a * Math.exp(-(Math.pow(p.x - x0, 2) / (2.0 * sx * sx) + Math.pow(p.y - y0, 2) / (2.0 * sy * sy)));
    }


    public static double g(double x, double y) {

        double A = 2.0;
        double a = 0.1;
        double b = 0;
        double c = 0.1;

        double x0 = 0;
        double y0 = 0;

        double p0 = a * (x - x0) * (x - x0);
        double p1 = 2 * b * (x - x0) * (y - y0);
        double p2 = c * (y - y0) * (y - y0);

        return A * Math.exp(-(p0 + p1 + p2));
    }


}
