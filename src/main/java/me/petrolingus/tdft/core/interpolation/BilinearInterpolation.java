package me.petrolingus.tdft.core.interpolation;

import me.petrolingus.tdft.core.signal.TwoDimensionalRealSignal;

import java.awt.*;

public class BilinearInterpolation {

    public static TwoDimensionalRealSignal interpolate(TwoDimensionalRealSignal signal, int width, int height) {

        double[] xs = TwoDimensionalRealSignal.createSamplingPoints(-1, 1, signal.getWidth());
        double[] ys = TwoDimensionalRealSignal.createSamplingPoints(-1, 1, signal.getHeight());
        double[] newXs = TwoDimensionalRealSignal.createSamplingPoints(-1, 1, width);
        double[] newYs = TwoDimensionalRealSignal.createSamplingPoints(-1, 1, height);

        double[][] samples = signal.getSamples();
        double[][] newSamples = new double[height][width];

        Matrix z = new Matrix(samples);
        System.out.println("XS size " + xs.length);
        System.out.println("YS size " + ys.length);
        System.out.println(samples.length);

        for (int i = 0; i < height; i++) {
            double y = newYs[i];
//            System.out.println(i);
            for (int j = 0; j < width; j++) {
                double x = newXs[j];

                newSamples[j][i] = Interpolation.bilinearInterpolate(xs, ys, z, x, y, false, false);
//                newSamples[j][i] = Interpolation.bilinearInterpolate(-1, 1, -1, 1, z, x, y, false, false);

//                int[] res = foo(ys, xs, x, y);
//                if (res == null) {
//                    newSamples[i][j] = 0;
//                    continue;
//                }
//
//                // indices
//                int x1 = res[0];
//                int y1 = res[1];
//                int x2 = res[2];
//                int y2 = res[3];
//
//                double vx1 = xs[x1];
//                double vy1 = ys[y1];
//                double vx2 = xs[x2];
//                double vy2 = ys[x2];
//
//                double c0 = (vx2 - x) / (vx2 - vx1);
//                double c1 = (x - vx1) / (vx2 - vx1);
//
//                double r11 = c0 * samples[y1][x1];
//                double r12 = c1 * samples[y1][x2];
//                double r1 = r11 + r12;
//
//                double r21 = c0 * samples[y2][x1];
//                double r22 = c1 * samples[y2][x2];
//                double r2 = r21 + r22;
//
//                double v0 = r1 * ((vy2 - y) / (vy2 - vy1));
//                double v1 = r2 * ((y - vy1) / (vy2 - vy1));
//                double value = v0 + v1;
//                newSamples[i][j] = value;
//                System.out.println(value);

            }
        }

        TwoDimensionalRealSignal result = new TwoDimensionalRealSignal(width, height, newSamples);
//        result.normalize();
        result.calcEnergy();

        return result;
    }

    private static int[] foo(double[] ys, double[] xs, double x, double y) {

        for (int i = 0; i < ys.length - 1; i++) {
            double y0 = ys[i];
            double y1 = ys[i + 1];
            for (int j = 0; j < xs.length - 1; j++) {
                double x0 = xs[j];
                double x1 = xs[j + 1];
                if (x0 < x && x < x1 && y0 < y && y < y1) {
                    int xx2 = j + 1;
                    int yy2 = i + 1;
                    return new int[] {j, i, xx2, yy2};
                }
            }
        }

        return null;
    }

}
