package me.petrolingus.tdft;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.petrolingus.tdft.math.Complex;
import me.petrolingus.tdft.math.core.GaussianParameters;
import org.w3c.dom.Text;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static me.petrolingus.tdft.math.core.GaussianParameters.getGaussianParameters;

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
        double imageWidth = image.getWidth();
        double canvasWidth = canvas.getWidth();
        double shift = (canvasWidth - imageWidth) / 2.0;
//        context.setFill(Color.LIGHTBLUE);
//        context.fillRect(0, 0, canvasWidth, canvasWidth);
        context.setStroke(Color.color(0, 0.47, 0.84));
        context.setLineWidth(4);
        context.strokeRect(0, 0, canvasWidth, canvasWidth);

//        double h = imageWidth / 16;
//        double center = canvasWidth / 2;
//        double gridLength = padding / 4.0;
//        context.strokeLine(center, 0, center, gridLength);
//        context.strokeLine(center, canvasWidth, center, canvasWidth - gridLength);
//        for (int i = 1; i < 8; i++) {
//            double x1 = center + h * i;
//            double x2 = center - h * i;
//            // TOP
//            context.strokeLine(x1, 0, x1, gridLength);
//            context.strokeLine(x2, 0, x2, gridLength);
//            // BOTTOM
//            context.strokeLine(x1, canvasWidth, x1, canvasWidth - gridLength);
//            context.strokeLine(x2, canvasWidth, x2, canvasWidth - gridLength);
//            // LEFT
//            context.strokeLine(0, x1, gridLength, x1);
//            context.strokeLine(0, x2, gridLength, x2);
//            // RIGHT
//            context.strokeLine(canvasWidth, x1, canvasWidth - gridLength, x1);
//            context.strokeLine(canvasWidth, x2, canvasWidth - gridLength, x2);
//        }

        context.drawImage(image, shift, shift);
    }

    public void onButton() {

        int width = (int) noiseImageCanvas.getWidth();
        int height = (int) noiseImageCanvas.getHeight();

        GaussianParameters gaussianParameters1 = getGaussianParameters(amplitude1, x01, y01, sx1, sy1);
        GaussianParameters gaussianParameters2 = getGaussianParameters(amplitude2, x02, y02, sx2, sy2);
        GaussianParameters gaussianParameters3 = getGaussianParameters(amplitude3, x03, y03, sx3, sy3);
        GaussianParameters[] gaussianParameters = new GaussianParameters[]{
                gaussianParameters1,
                gaussianParameters2,
                gaussianParameters3
        };

        // Generate samples
        int quality = Integer.parseInt(qualityLabel.getText());
        double boundSize = Double.parseDouble(boundSizeLabel.getText());
        double[][] samples = TwoDimensionalFourierTransform.generateSamples(gaussianParameters, quality, boundSize);
        TwoDimensionalFourierTransform.normalize(samples);
        Image originalImage = TwoDimensionalFourierTransform.generateImage(width, height, samples);
        drawImage(originalImageCanvas, originalImage);
        drawImage(canvas4, originalImage);

        // Generate noise
        double noiseLevel = Double.parseDouble(noiseLevelLabel.getText());
        TwoDimensionalFourierTransform.noise(samples, noiseLevel);
        Image image0 = TwoDimensionalFourierTransform.generateImage(width, height, samples);
        drawImage(noiseImageCanvas, image0);

        // Forward FFT
        Complex[][] transform = TwoDimensionalFourierTransform.transform(samples);
        double[][] transformPixels = TwoDimensionalFourierTransform.process(transform);
        Image image1 = TwoDimensionalFourierTransform.generateImage(width, height, transformPixels);
        drawImage(canvas1, image1);

        // Filtering
        double[][] processedPixels;
        double threshold = Double.parseDouble(thresholdLabel.getText());
        if (chooseBox.getValue().equals("Low Pass")) {
            processedPixels = TwoDimensionalFourierTransform.lowPassFiltering(threshold, transform, transformPixels);
        } else if (chooseBox.getValue().equals("High Pass")) {
            processedPixels = TwoDimensionalFourierTransform.highPassFiltering(threshold, transform, transformPixels);
        } else if (chooseBox.getValue().equals("High Amplitude")) {
            processedPixels = TwoDimensionalFourierTransform.highAmplitudeFiltering(threshold, transform, transformPixels);
        } else {
            processedPixels = TwoDimensionalFourierTransform.lowAmplitudeFiltering(threshold, transform, transformPixels);
        }
        Image image2 = TwoDimensionalFourierTransform.generateImage(width, height, processedPixels);
        drawImage(canvas2, image2);

        // Backward FFT
        Complex[][] restored = TwoDimensionalFourierTransform.itransform(transform);
        double[][] restoredPixels = new double[transform.length][transform.length];
        for (int i = 0; i < restoredPixels.length; i++) {
            for (int j = 0; j < restoredPixels.length; j++) {
                Complex value = restored[i][j];
                restoredPixels[i][j] = Math.sqrt(value.getX() * value.getX() + value.getY() * value.getY());
            }
        }
        Image image3 = TwoDimensionalFourierTransform.generateImage(width, height, restoredPixels);
        drawImage(canvas3, image3);

        double diff = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double d = samples[i][j] - restoredPixels[i][j];
//                double d = samples[i][j] - processedPixels[i][j];
                diff += d * d;
            }
        }
        diffLabel.setText(diff + "");
    }

    private double[][] getImageSamples() throws URISyntaxException {
        URL resource = Main.class.getResource("rose.jpeg");
        File file = new File(resource.toURI());
        Image image = new Image(file.toURI().toString());
        int[] pixels = new int[512 * 512];
        image.getPixelReader().getPixels(0, 0, 512, 512, PixelFormat.getIntArgbInstance(), pixels, 0, 512);
        double mx = Arrays.stream(pixels).mapToDouble(Double::valueOf).max().orElse(Double.NaN);
        double mn = Arrays.stream(pixels).mapToDouble(Double::valueOf).min().orElse(Double.NaN);
        System.out.println("The maximum is " + mx);
        System.out.println("The maximum is " + mn);
        double[][] imageSamples = new double[512][512];
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                int value = pixels[j + 512 * i];
                double valued = TwoDimensionalFourierTransform.normalize(value, mn, mx);
                imageSamples[j][511 - i] = valued;
            }
        }
        return imageSamples;
    }
}
