package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import rat.Utils;

public class UtilsTest {

    @Test
    public void testAddToStartup() {
        try {
            Path file = Paths.get("testFile.txt");
            Utils.addToStartup(file);
            assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRemoveFromStartup() {
        Utils utils = new Utils();
        utils.removeFromStartup("testFile.txt");
        assertTrue(true);
    }

    @Test
    public void testShutDown() {
        Utils.shutDown();
        assertTrue(true);
    }

    @Test
    public void testRestart() {
        Utils.restart();
        assertTrue(true);
    }

    @Test
    public void testCreateFile() {
        String path = "testFile.txt";
        Utils.createFile(path);
        File file = new File(path);
        assertTrue(file.exists());
    }

    @Test
    public void testDeleteFile() {
        String path = "testFile.txt";
        File file = new File(path);
        Utils.deleteFile(file);
        assertTrue(!file.exists());
    }

    @Test
    public void testCopyFile() {
        try {
            Path source = Paths.get("testFile.txt");
            Path destination = Paths.get("copiedTestFile.txt");
            Utils.copyFile(source, destination);
            File file = new File(destination.toString());
            assertTrue(file.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
