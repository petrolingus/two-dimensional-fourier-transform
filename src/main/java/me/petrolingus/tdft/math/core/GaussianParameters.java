package me.petrolingus.tdft.math.core;

public class GaussianParameters {

    public final double a;

    public final double x0;

    public final double y0;

    public final double sx;

    public final double sy;

    public GaussianParameters(double a, double x0, double y0, double sx, double sy) {
        this.a = a;
        this.x0 = x0;
        this.y0 = y0;
        this.sx = sx;
        this.sy = sy;
    }
}
