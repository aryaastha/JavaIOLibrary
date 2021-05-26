import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SelectorTest {

    static Process server;
    static SelectorClient client;

    @Before
    public void setup() throws IOException, InterruptedException {
        server = SelectorServer.start();
        System.out.printf("Is server running?" + server.isAlive());
        client = SelectorClient.getInstance();
    }

    @After
    public void teardown() throws IOException {
        client.close();
        server.destroyForcibly();
    }

    @Test
    public void testSelector() throws IOException {
        client.sendMessage("Hey");
        client.sendMessage("Baby");
    }
}
