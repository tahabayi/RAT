package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import rat.Client;

public class ClientTest {

    @Test
    public void testClientConstructor() {
        try {
            Client client = new Client();
            assertNotNull(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClientMain() {
        try {
            Client.main(new String[]{});
            assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
