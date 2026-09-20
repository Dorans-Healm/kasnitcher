package prism.application.component;

import org.jspecify.annotations.NonNull;
import prism.configuration.adapter.ListenerAdapter;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class SocketSubscriber implements AutoCloseable {

    private final Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext;

    private SocketChannel socketChannel;

    public SocketSubscriber(@NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext) {
        this.appExecutionContext = appExecutionContext;
    }

    @Override
    public void close() {
        this.unsubscribe();
    }

    public void subscribe() throws IOException {
        ListenerAdapter adapter =
                appExecutionContext.get().getListenerAdapter();

        String file = ListenerAdapter.FILE;
        String dir = ListenerAdapter.DIR;

        if (adapter != null) {
            file = adapter.getFile();
            dir = adapter.getDirectory();
        }

        String path = FileUtils.buildPath(dir, file);
        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(path);

        this.socketChannel =
                SocketChannel.open(StandardProtocolFamily.UNIX);

        this.socketChannel.connect(address);
    }

    public void unsubscribe() {
        if (this.socketChannel != null && this.socketChannel.isConnected()) {
            try {
                this.socketChannel.finishConnect();
                this.socketChannel.close();

                this.socketChannel = null;
            } catch (IOException e) {
                throw new RuntimeException("An error " +
                        "was thrown while trying to end connection", e);
            }
        }
    }

    public void listen() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Channels.newInputStream(this.socketChannel),
                            StandardCharsets.UTF_8));

            String message;

            while ((message = reader.readLine()) != null) {

            }
        } catch (IOException e) {
            System.err.println("Connection dropped or error reading: " + e.getMessage());
        }
    }
}