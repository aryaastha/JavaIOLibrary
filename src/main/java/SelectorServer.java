import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class SelectorServer {

    private static final String POISON_PILL = "poison-pill";

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        // configure channel
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress("localhost", 5454));

        channel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            selector.select();
            System.out.println("Selected for Accepting connections");

            Set<SelectionKey> acceptedChannels = selector.selectedKeys();
            Iterator<SelectionKey> acceptedChannelIterator = acceptedChannels.iterator();

            while (acceptedChannelIterator.hasNext()) {
                SelectionKey selectionKey = acceptedChannelIterator.next();

                if (selectionKey.isAcceptable()) {
                    // register channel
                    register(channel, selector);
                }

                if (selectionKey.isReadable()) {
                    // read channel
                    readChannel(selectionKey, buffer);
                }
                acceptedChannelIterator.remove();
            }
        }
    }

    private static void register(
            ServerSocketChannel channel, Selector selector) throws IOException {
        SocketChannel accept = channel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private static void readChannel(SelectionKey selectionKey, ByteBuffer buffer)
            throws IOException {
        SocketChannel client = (SocketChannel) selectionKey.channel();
        client.read(buffer);

        if (new String(buffer.array()).trim().equalsIgnoreCase(POISON_PILL)) {
            client.close();
            System.out.println("Not reading any more messages from client");
        } else {
            buffer.flip();
            client.write(buffer);
            buffer.clear();
        }
    }

    public static Process start() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = SelectorServer.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);

        Process start = builder.start();
        Thread.sleep(2000);
        return start;
    }
}
