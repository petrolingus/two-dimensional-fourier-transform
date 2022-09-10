package me.petrolingus.tdft.math;

import me.petrolingus.tdft.math.core.GaussianParameters;

import java.util.function.Function;

public class Functions {

    public static Function<Point, Double> gaussian(GaussianParameters parameters) {
        double a = parameters.a;
        double x0 = parameters.x0;
        double y0 = parameters.y0;
        double sx = parameters.sx;
        double sy = parameters.sy;
        return (p) -> a * Math.exp(-(Math.pow(p.x - x0, 2) / (2.0 * sx * sx) + Math.pow(p.y - y0, 2) / (2.0 * sy * sy)));
    }

    public static Function<Point, Double> rect(GaussianParameters parameters) {
        double a = parameters.a;
        double x0 = parameters.x0;
        double y0 = parameters.y0;
        double sx = parameters.sx;
        double sy = parameters.sy;
        return (p) -> (p.x > x0 - sx && p.x < x0 + sx && p.y > y0 - sy && p.y < y0 + sy) ? 1.0 : 0.0;
    }
}
