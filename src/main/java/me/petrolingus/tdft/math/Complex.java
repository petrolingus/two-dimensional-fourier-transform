package me.petrolingus.tdft.math;

public class Complex {

    private final double x;

    private final double y;

    public Complex(double x) {
        this.x = x;
        this.y = 0;
    }

    public Complex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Complex plus(Complex complex) {
        return new Complex(this.x + complex.x, this.y + complex.y);
    }

    public Complex minus(Complex complex) {
        return new Complex(this.x - complex.x, this.y - complex.y);
    }

    public Complex times(Complex complex) {
        double x = this.x * complex.x - this.y * complex.y;
        double y = this.x * complex.y + this.y * complex.x;
        return new Complex(x, y);
    }

    public Complex scale(double scale) {
        return new Complex(scale * this.x, scale * this.y);
    }

    public Complex conjugate() {
        return new Complex(this.x, -this.y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
