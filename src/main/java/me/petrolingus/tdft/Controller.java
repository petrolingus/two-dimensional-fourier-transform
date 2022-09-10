package me.petrolingus.tdft;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import me.petrolingus.tdft.math.core.GaussianParameters;

public class Controller {

    public Canvas canvas;
    public Canvas canvas1;

    public TextField amplitude1;
    public TextField x01;
    public TextField y01;
    public TextField  sx1;
    public TextField  sy1;

    public TextField qualityLabel;

    private final int padding = 4;

    public void initialize() {
        onButton();
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
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        int imageSize = width - padding;

        GaussianParameters gaussianParameters = new GaussianParameters(
                Double.parseDouble(amplitude1.getText()),
                Double.parseDouble(x01.getText()),
                Double.parseDouble(y01.getText()),
                Double.parseDouble(sx1.getText()),
                Double.parseDouble(sy1.getText())
        );
        double[][] samples = TwoDimensionalFourierTransform.generateSamples(gaussianParameters, Integer.parseInt(qualityLabel.getText()));
        double[][] transform = TwoDimensionalFourierTransform.transform(samples);

        Image image0 = TwoDimensionalFourierTransform.generateImage(imageSize, imageSize, samples);
        drawImage(canvas, image0);

        Image image1 = TwoDimensionalFourierTransform.generateImage(imageSize, imageSize, transform);
        drawImage(canvas1, image1);
    }
}
