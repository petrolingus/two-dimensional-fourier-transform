package me.petrolingus.tdft.core.math;

import javafx.scene.control.TextField;

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

    public static GaussianParameters getGaussianParameters(TextField amplitude, TextField x0, TextField y0, TextField sx, TextField sy) {
        return new GaussianParameters(
                Double.parseDouble(amplitude.getText()),
                Double.parseDouble(x0.getText()),
                Double.parseDouble(y0.getText()),
                Double.parseDouble(sx.getText()),
                Double.parseDouble(sy.getText())
        );
    }
}
