package me.petrolingus.tdft.core.math;

import java.util.function.Function;

public class Functions {

    public static Function<Point, Double> gaussian(GaussianParameters parameters) {
        double a = parameters.a;
        double x0 = parameters.x0;
        double y0 = parameters.y0;
        double sx = parameters.sx;
        double sy = parameters.sy;
        double sxsx2 = 2.0 * sx * sx;
        double sysy2 = 2.0 * sy * sy;
        return (p) -> a * Math.exp(-(Math.pow(p.x - x0, 2) / sxsx2 + Math.pow(p.y - y0, 2) / sysy2));
    }

    public static Function<Point, Double> rect(GaussianParameters parameters) {
        double a = parameters.a;
        double x0 = parameters.x0;
        double y0 = parameters.y0;
        double sx = parameters.sx;
        double sy = parameters.sy;
        return (p) -> (p.x > x0 - sx && p.x < x0 + sx && p.y > y0 - sy && p.y < y0 + sy) ? a : 0.0;
    }
}
