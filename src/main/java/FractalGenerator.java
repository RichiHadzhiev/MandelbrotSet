import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.Pair;

public class FractalGenerator {

	private static final double default_X_start = -2.0;
	private static final double default_X_end = 2.0;
	private static final double default_Y_start = -2.0;
	private static final double default_Y_end = 2.0;
	private static final int defaultWidth = 640;
	private static final int defaultHeight = 480;
	private static final int defaultWorkingThreads = 1;
	private static final String outputName = "zad19.png";
	private static final boolean isQuiet = false;

	private double x1;
	private double x2;
	private double y1;
	private double y2;
	private int width;
	private int height;
	private int numberOfThreads;
	private String outputFileName;
	private boolean quietMode;

	public FractalGenerator() {
		this.x1 = default_X_start;
		this.x2 = default_X_end;
		this.y1 = default_Y_start;
		this.y2 = default_Y_end;
		this.width = defaultWidth;
		this.height = defaultHeight;
		this.numberOfThreads = defaultWorkingThreads;
		this.outputFileName = outputName;
		this.quietMode = isQuiet;
	}

	/*
	 * public FractalGenerator(int _width, int _height, int _numberOfThreads,
	 * String outputName, boolean quiet) { this.x1 = default_X_start; this.x2 =
	 * default_X_end; this.y1 = default_Y_start; this.y2 = default_Y_end;
	 * System.out.println("Enter width: "); this.width = _width;
	 * System.out.println("Enter height: "); this.height = _height;
	 * System.out.println("Enter number of threads: "); this.numberOfThreads =
	 * _numberOfThreads; System.out.println("Enter filename: ");
	 * this.outputFileName = outputName;
	 * System.out.println("Enter quiet mode: "); this.quietMode = quiet; }
	 */

	public void generateFractal() throws IOException, InterruptedException {

		BufferedImage bufferedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_USHORT_565_RGB);
		int rowsPerThread = bufferedImage.getHeight() / this.numberOfThreads;

		// Equal work for all threads
		List<Thread> threads = new ArrayList<Thread>();
		int startingRow;
		Pair<Double, Double> rangeForX = new Pair<Double, Double>(this.x1, this.x2);
		Pair<Double, Double> rangeForY = new Pair<Double, Double>(this.y1, this.y2);
		for (int i = 0; i < this.numberOfThreads; i++) {
			startingRow = i * rowsPerThread;
			Thread thread = new Thread(new ImageThread(startingRow, startingRow + rowsPerThread, bufferedImage,
					rangeForX, rangeForY, this.quietMode));
			threads.add(thread);
		}

		long time = System.currentTimeMillis();
		System.out.println("Generating fractal...\n");
		for (Thread thread : threads) {
			thread.start();
		}

		// Waiting for all threads to finish their work
		for (Thread thread : threads) {
			thread.join();
		}

		System.out.println("Fractal generated!\n");
		time = System.currentTimeMillis() - time;

		// Saving the image
		ImageIO.write(bufferedImage, "png", new File("C:\\Users\\Richi\\Desktop\\" + outputFileName));
		System.out.println("Program finished.\nOutput: " + this.outputFileName + "(" + bufferedImage.getWidth() + "px, "
				+ bufferedImage.getHeight() + "px)");

		// Each thread execution time
		System.out.println("Threads used in current run: " + numberOfThreads);
		System.out.println("Total execution time for current run: " + (time) / 1000 + "s (" + time + "ms)");

	}

	public void setWidth(int imgWidth) {
		this.width = imgWidth;
	}

	public void setHeight(int imgHeight) {
		this.height = imgHeight;
	}

	public void setX1(double xa) {
		this.x1 = xa;
	}

	public void setX2(double xb) {
		this.x2 = xb;
	}

	public void setY1(double ya) {
		this.y1 = ya;
	}

	public void setY2(double yb) {
		this.y2 = yb;
	}

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public void setOutputFileName(String outputFile) {
		this.outputFileName = outputFile;
	}

	public void setQuietMode(boolean quietMode) {
		this.quietMode = quietMode;
	}
}
