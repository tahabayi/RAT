package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.Test;

import rat.Frame;

public class FrameTest {

    @Test
    public void testIsEqual() {
        int rgb1 = 0xFF0000; // Red
        int rgb2 = 0xFF0000; // Red
        int tolerance = 0;
        assertTrue(Frame.isEqual(rgb1, rgb2, tolerance));
    }

    @Test
    public void testTakeScreenshot() {
        BufferedImage screenshot = Frame.takeScreenshot();
        assertNotNull(screenshot);
    }

    @Test
    public void testCaptureScreen() {
        BufferedImage screenCapture = Frame.captureScreen();
        assertNotNull(screenCapture);
    }

    @Test
    public void testGetIFrames() {
        BufferedImage img1 = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        Frame[] frames = Frame.getIFrames(img1, img2);
        assertNotNull(frames);
        assertEquals(0, frames.length);
    }

    @Test
    public void testToByteArray() {
        BufferedImage img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        byte[] byteArray = Frame.toByteArray(img);
        assertNotNull(byteArray);
    }

    @Test
    public void testResize() {
        BufferedImage img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        BufferedImage resizedImg = Frame.resize(img, 320, 240);
        assertNotNull(resizedImg);
        assertEquals(320, resizedImg.getWidth());
        assertEquals(240, resizedImg.getHeight());
    }
}
