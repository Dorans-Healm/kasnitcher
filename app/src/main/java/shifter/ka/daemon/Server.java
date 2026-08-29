package shifter.ka.daemon;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Server implements AutoCloseable {

    private ServerSocketChannel serverSocketChannel;

    @Override
    public void close() throws Exception {
        this.interrupt();
    }

    public void open() throws IOException {
        this.serverSocketChannel =
                ServerSocketChannel.open(StandardProtocolFamily.UNIX);
    }

    public void interrupt() throws IOException {
        this.serverSocketChannel.close();
    }

    @SuppressWarnings("EndlessStream")
    public void listen(Consumer<SocketChannel> consumer) {
        Stream.generate(this::accept).forEach(consumer);
    }

    public SocketChannel accept() {
        try {
            return this.serverSocketChannel.accept();
        } catch (IOException e) {
            throw new RuntimeException("Socket acceptance error.");
        }
    }
}