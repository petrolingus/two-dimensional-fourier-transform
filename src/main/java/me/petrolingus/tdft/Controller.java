package me.petrolingus.tdft;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.Pane;
import me.petrolingus.tdft.core.Algorithm;
import me.petrolingus.tdft.core.interpolation.BilinearInterpolation;
import me.petrolingus.tdft.core.math.*;
import me.petrolingus.tdft.core.signal.TwoDimensionalComplexSignal;
import me.petrolingus.tdft.core.signal.TwoDimensionalRealSignal;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Controller {

    public Pane originalImagePane;
    public Pane noiseImagePane;
    public Pane canvasPane1;
    public Pane canvasPane2;
    public Pane canvasPane3;
    public Pane canvasPane4;

    public ResizableCanvas originalImageCanvas;
    public ResizableCanvas noiseImageCanvas;
    public ResizableCanvas canvas1;
    public ResizableCanvas canvas2;
    public ResizableCanvas canvas3;
    public ResizableCanvas canvas4;

    public TextField amplitude1;
    public TextField x01;
    public TextField y01;
    public TextField sx1;
    public TextField sy1;

    public TextField amplitude2;
    public TextField x02;
    public TextField y02;
    public TextField sx2;
    public TextField sy2;

    public TextField amplitude3;
    public TextField x03;
    public TextField y03;
    public TextField sx3;
    public TextField sy3;

    public TextField boundSizeLabel;

    public TextField qualityLabel;
    public TextField thresholdLabel;
    public TextField noiseLevelLabel;
    public ChoiceBox<String> chooseBox;

    public TextField diffLabel;

    public void createCanvas(Pane canvasPane, Canvas canvas) {
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
        canvasPane.getChildren().add(canvas);
    }

    public void initialize() {

        originalImageCanvas = new ResizableCanvas();
        noiseImageCanvas = new ResizableCanvas();
        canvas1 = new ResizableCanvas();
        canvas2 = new ResizableCanvas();
        canvas3 = new ResizableCanvas();
        canvas4 = new ResizableCanvas();

        createCanvas(originalImagePane, originalImageCanvas);
        createCanvas(noiseImagePane, noiseImageCanvas);
        createCanvas(canvasPane1, canvas1);
        createCanvas(canvasPane2, canvas2);
        createCanvas(canvasPane3, canvas3);
        createCanvas(canvasPane4, canvas4);

        chooseBox.getItems().addAll("Low Pass", "High Pass", "High Amplitude", "Low Amplitude");
        chooseBox.setValue("Low Pass");

//        onButton();
    }

    private void drawImage(Canvas canvas, Image image) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.drawImage(image, 0, 0);
    }

    public void onButton() {

        int width = (int) originalImageCanvas.getWidth();
        int height = (int) originalImageCanvas.getHeight();

        List<Function<Point, Double>> functions = List.of(
                Functions.gaussian(GaussianParameters.getGaussianParameters(amplitude1, x01, y01, sx1, sy1)),
                Functions.gaussian(GaussianParameters.getGaussianParameters(amplitude2, x02, y02, sx2, sy2)),
                Functions.gaussian(GaussianParameters.getGaussianParameters(amplitude3, x03, y03, sx3, sy3))
        );


        // Generate signal
        TwoDimensionalRealSignal signal = new TwoDimensionalRealSignal(width, height, functions);
        TwoDimensionalRealSignal originalSignal = BilinearInterpolation.interpolate(signal, width, height);
        drawImage(originalImageCanvas, originalSignal.getImage());

        // Generate noise
//        double noiseLevel = Double.parseDouble(noiseLevelLabel.getText());
//        Algorithm.noise(originalImageSamples, noiseLevel);
//        Image image0 = Algorithm.generateImage(width, height, originalImageSamples);
//        drawImage(noiseImageCanvas, image0);

        // Forward FFT
        TwoDimensionalComplexSignal spectrumSignal = new TwoDimensionalComplexSignal(originalSignal);
        drawImage(canvas1, spectrumSignal.getImage());

//        // Filtering
//        double[][] processedPixels;
//        double threshold = Double.parseDouble(thresholdLabel.getText());
//        if (chooseBox.getValue().equals("Low Pass")) {
//            processedPixels = Algorithm.lowPassFiltering(threshold, transform, transformPixels);
//        } else if (chooseBox.getValue().equals("High Pass")) {
//            processedPixels = Algorithm.highPassFiltering(threshold, transform, transformPixels);
//        } else if (chooseBox.getValue().equals("High Amplitude")) {
//            processedPixels = Algorithm.highAmplitudeFiltering(threshold, transform, transformPixels);
//        } else {
//            processedPixels = Algorithm.lowAmplitudeFiltering(threshold, transform, transformPixels);
//        }
//        Image image2 = Algorithm.generateImage(width, height, processedPixels);
//        drawImage(canvas2, image2);
//
//        // Backward FFT
//        Complex[][] restored = Algorithm.itransform(transform);
//        double[][] restoredPixels = new double[transform.length][transform.length];
//        for (int i = 0; i < restoredPixels.length; i++) {
//            for (int j = 0; j < restoredPixels.length; j++) {
//                Complex value = restored[i][j];
//                restoredPixels[i][j] = Math.sqrt(value.getX() * value.getX() + value.getY() * value.getY());
//            }
//        }
//        Image image3 = Algorithm.generateImage(width, height, restoredPixels);
//        drawImage(canvas3, image3);
//
//        double diff = 0;
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                double d = originalImageSamples[i][j] - restoredPixels[i][j];
////                double d = originalImageSamples[i][j] - processedPixels[i][j];
//                diff += d * d;
//            }
//        }
//        diffLabel.setText(diff + "");
    }
}
