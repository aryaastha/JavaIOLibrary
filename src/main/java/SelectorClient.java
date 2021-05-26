import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SelectorClient {
    private static SelectorClient client;

    private static SocketChannel channel;

    ByteBuffer buffer;

    public static SelectorClient getInstance() throws IOException {
        if (client == null) {
            client = new SelectorClient();
        }
        return client;
    }

    private SelectorClient() throws IOException {
        channel = SocketChannel.open(new InetSocketAddress("localhost", 5454));
        buffer = ByteBuffer.allocate(256);
    }

    public void close() throws IOException {
        channel.close();
        buffer = null;
    }

    public void sendMessage(String message) throws IOException {
        buffer = ByteBuffer.wrap(message.getBytes());

        channel.write(buffer);
        buffer.clear();
        channel.read(buffer);
        String response = new String(buffer.array()).trim();
        System.out.println("Response from server is = " + response);
    }
}
