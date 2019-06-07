import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.util.Pair;

public class Main {

	private static double x1 = -2.0;
	private static double x2 = 2.0;
	private static double y1 = -2.0;
	private static double y2 = 2.0;
	private static int width = 640;
	private static int height = 480;
	private static int numberOfThreads = 1;
	private static String outputFile = "zad19.png";
	private static boolean quietMode = false;

	public static void main(String[] args) throws Exception {

		parseArgs(args);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
		int rowsPerThread = bufferedImage.getHeight() / numberOfThreads;

		// Equal work for all threads
		List<Thread> threads = new ArrayList<Thread>();
		int startingRow;
		Pair<Double, Double> rangeForX = new Pair<Double, Double>(x1, x2);
		Pair<Double, Double> rangeForY = new Pair<Double, Double>(y1, y2);
		for (int i = 0; i < numberOfThreads; i++) {
			startingRow = i * rowsPerThread;
			Thread thread = new Thread(new ImageThread(startingRow, startingRow + rowsPerThread, bufferedImage,
					rangeForX, rangeForY, quietMode));
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
		ImageIO.write(bufferedImage, "png", new File("C:\\Users\\Richi\\Desktop\\" + outputFile));
		System.out.println("Program finished.\nOutput: " + outputFile + "(" + bufferedImage.getWidth() + "px, "
				+ bufferedImage.getHeight() + "px)");

		// Each thread execution time
		System.out.println("Threads used in current run: " + numberOfThreads);
		System.out.println("Total execution time for current run: " + (time) / 1000 + "s (" + time + "ms)");

	}

	private static void parseArgs(String[] args) throws Exception {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();

		options.addOption("s", "size", true, "image resolution");
		options.addOption("r", "rect", true, "complex plane range");
		options.addOption("t", "tasks", true, "number of threads");
		options.addOption("o", "output", true, "output file name");
		options.addOption("q", "quiet", false, "quiet mode");

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("q")) {
				quietMode = true;
			}
			if (cmd.hasOption("r")) {
				String[] planeCoordinates = cmd.getOptionValue("r").split(":");
				x1 = Double.parseDouble(planeCoordinates[0]);
				x2 = Double.parseDouble(planeCoordinates[1]);
				y1 = Double.parseDouble(planeCoordinates[2]);
				y2 = Double.parseDouble(planeCoordinates[3]);
			}
			if (cmd.hasOption("o")) {
				outputFile = cmd.getOptionValue("o");
			}
			if (cmd.hasOption("t")) {
				numberOfThreads = Integer.parseInt(cmd.getOptionValue("t"));
			}
			if (cmd.hasOption("s")) {
				String[] resolution = cmd.getOptionValue("s").split("x");
				width = Integer.parseInt(resolution[0]);
				height = Integer.parseInt(resolution[1]);
			}

		} catch (ParseException e) {
			throw new Exception(e);
		}
	}
}
