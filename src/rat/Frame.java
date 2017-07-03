package rat;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Frame {

	public final int x, y;
	public final BufferedImage image;

	public Frame(final int x, final int y, final BufferedImage image) {
		this.x = x;
		this.y = y;
		this.image = image;
	}

	public static final int SKIP = 6;
	public static final int TOLERANCE = 50;

	public static final int CELLS_WIDE = 6;
	public static final int CELLS_HIGH = 6;
	public static final Frame[] EMPTY_ARRAY = new Frame[0];

	public static final byte INCOMING = 1;
	public static final byte END = 0;

	public static boolean isEqual(final int rgb1, final int rgb2, final int tolerance) {
		final int red1 = (rgb1 >> 16) & 0xff;
		final int green1 = (rgb1 >> 8) & 0xff;
		final int blue1 = rgb1 & 0xff;
		final int red2 = (rgb1 >> 16) & 0xff;
		final int green2 = (rgb2 >> 8) & 0xff;
		final int blue2 = rgb2 & 0xff;
		final int red = Math.abs(red1 - red2);
		final int green = Math.abs(green1 - green2);
		final int blue = Math.abs(blue1 - blue2);

		if (red <= tolerance && green <= tolerance && blue <= tolerance) {
			return true;
		}

		return false;
	}

	public static BufferedImage takeScreenshot() {
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle screenRect = new Rectangle(screen);

		try {
			final Robot robot = new Robot();
			final BufferedImage image = robot.createScreenCapture(screenRect);

			return image;
		} catch (final AWTException ex) {
			ex.printStackTrace();

			return null;
		}
	}

	public static BufferedImage captureScreen() {
		final BufferedImage image = takeScreenshot();

		if (image == null) {
			return null;
		}

		return resize(image, 640, 480);
	}

	public static Frame[] getIFrames(final BufferedImage previous, final BufferedImage next) {
		final int width = previous.getWidth();
		final int height = previous.getHeight();

		if (next.getWidth() != width || next.getHeight() != height) {
			return EMPTY_ARRAY;
		}

		final int cellWidth = width / CELLS_WIDE;
		final int cellHeight = height / CELLS_HIGH;
		final ArrayList<Frame> frames = new ArrayList<Frame>();

		for (int x = 0; x < CELLS_WIDE; x++) {
			for (int y = 0; y < CELLS_HIGH; y++) {
				final int cellX = x * cellWidth;
				final int cellY = y * cellHeight;
				final int cellEndX = cellX + cellWidth;
				final int cellEndY = cellY + cellHeight;

				outer: for (int xx = cellX; xx < cellEndX && xx < width; xx += SKIP) {
					for (int yy = cellY; yy < cellEndY && yy < height; yy += SKIP) {
						final int previousRgb = previous.getRGB(xx, yy);
						final int nextRgb = next.getRGB(xx, yy);
						final boolean equal = isEqual(previousRgb, nextRgb, TOLERANCE);

						if (equal) {
							continue;
						}

						final BufferedImage image = next.getSubimage(cellX, cellY, cellWidth, cellHeight);
						final Frame frame = new Frame(cellX, cellY, image);

						frames.add(frame);

						break outer;
					}
				}

			}
		}

		final Frame[] framesArray = frames.stream().toArray(Frame[]::new);

		return framesArray;
	}

	public static byte[] toByteArray(BufferedImage image) {

		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			ImageIO.write(image, "PNG", out);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}

		final byte[] data = out.toByteArray();

		return data;

	}

	public static byte[] toByteArray(final BufferedImage image, final float quality) {
		final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("JPG");
		final ImageWriter writer = writers.next();
		final ImageWriteParam param = writer.getDefaultWriteParam();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);

		try {
			final ImageOutputStream imageOut = ImageIO.createImageOutputStream(out);

			writer.setOutput(imageOut);
			writer.write(image);

			return out.toByteArray();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	} 

}