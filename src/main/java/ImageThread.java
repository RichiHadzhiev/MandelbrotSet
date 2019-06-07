import java.awt.Color;
import java.awt.image.BufferedImage;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;

public class ImageThread implements Runnable {
	
	private final static int MAX_ITERATIONS = 800;
	private static int numberOfWorkers = 1;

	private int threadNumber;
	private int startingRow;
	private int endingRow;
	private double xa;
	private double xb;
	private double ya;
	private double yb;
	private boolean quietMode;
	private BufferedImage image;

	public ImageThread(int startingRow, int endingRow, BufferedImage image, Pair<Double, Double> x,
			Pair<Double, Double> y, boolean quietMode) {
		this.threadNumber = ImageThread.numberOfWorkers++;
		this.startingRow = startingRow;
		this.endingRow = endingRow;
		this.image = image;

		this.xa = x.getKey();
		this.xb = x.getValue();

		this.ya = y.getKey();
		this.yb = y.getValue();

		this.quietMode = quietMode;
	}

	private Complex equation(Complex c, Complex z) {
		return (z.exp()).subtract(c);
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		if (!this.quietMode) {
			System.out.println("Thread-" + threadNumber + " started.");
		}

		double c_re;
		double c_im;

		int iterator;
		for (int row = startingRow; row < endingRow && row < image.getHeight(); row++) {

			c_im = (row - image.getHeight() / yb) * ((yb - ya) / image.getHeight());

			for (int col = 0; col < image.getWidth(); col++) {

				c_re = (col - image.getWidth() / xb) * ((xb - xa) / image.getWidth());

				Complex c = new Complex(c_re, c_im);
				Complex z = new Complex(0, 0);

				for (iterator = 0; iterator < MAX_ITERATIONS; iterator++) {
					if (z.abs() > (this.xb - this.xa)) {
						break;
					}
					z = equation(c, z);
				}

				if (iterator < MAX_ITERATIONS) {
					image.setRGB(col, row, Color.HSBtoRGB(iterator / 256.0f, 1, iterator / (iterator + 8.0f)));
				} else {
					image.setRGB(col, row, 0);
				}
			}
		}
		if (this.quietMode) {
			return;
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Thread-" + threadNumber + " stopped.");
		System.out.println("Thread-" + threadNumber + " execution time was: " + time + "ms");
	}

}
