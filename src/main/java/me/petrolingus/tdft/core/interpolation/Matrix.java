package me.petrolingus.tdft.core.interpolation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matrix {

    public int rows;

    public int columns;

    private double matrix[][];

    public Matrix(double[][] matrix) {
        this.rows = matrix.length;
        this.columns = matrix[0].length;
        this.matrix = matrix;
    }

    public static Inline rowMajor(int i, int length, double[] xs) {
        List<Double> data = new ArrayList<>();
        for (int j = 0; j < length; j++) {
            data.add(xs[j]);
        }
        return new Inline(data);
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns;
    }
    public double at(int x, int y) {
        return matrix[x][y];
    }

    public Inline row(int row) {
        List<Double> data = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            data.add(matrix[row][i]);
        }
        return new Inline(data);
    }

    public Inline column(int columns) {
        List<Double> data = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            data.add(matrix[i][columns]);
        }
        return new Inline(data);
    }

    public static class Inline {
        List<Double> data;

        public Inline(List<Double> data) {
            this.data = data;
        }

        public List<Double> elements() {
            return data;
        }

        public List<Double> elementsByRow() {
            return data;
        }
    }
}
