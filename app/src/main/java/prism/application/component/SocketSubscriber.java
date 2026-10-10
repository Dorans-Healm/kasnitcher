package prism.application.component;

import lombok.extern.java.Log;
import org.jspecify.annotations.NonNull;
import prism.configuration.adapter.ListenerAdapter;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.domain.model.Listened;
import prism.utils.FileUtils;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Component responsible for establishing and managing a Unix Domain Socket connection,
 * allowing the application to listen for incoming {@link Listened} events.
 */
@Log
public class SocketSubscriber implements AutoCloseable {

    private final Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext;

    private SocketChannel socketChannel;

    ObjectMapper mapper;

    /**
     * Constructs a new {@code SocketSubscriber} with the provided application context.
     *
     * @param appExecutionContext a supplier providing the application configuration
     */
    public SocketSubscriber(@NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext) {
        this.appExecutionContext = appExecutionContext;

        this.mapper = new ObjectMapper();
    }

    /**
     * Closes the socket subscriber, ensuring the underlying socket connection is terminated.
     */
    @Override
    public void close() {
        this.unsubscribe();
    }

    /**
     * Establishes a Unix Domain Socket connection based on the path configured
     * in the application context.
     *
     * @throws IOException if the socket connection cannot be opened or connected
     */
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

    /**
     * Closes the active socket connection if it is currently open, swallowing
     * any I/O exceptions that occur during closure but wrapping them in a runtime exception.
     */
    public void unsubscribe() {
        if (this.socketChannel != null && this.socketChannel.isOpen()) {
            try {
                this.socketChannel.close();
            } catch (IOException e) {
                throw new RuntimeException("An error " +
                        "was thrown while trying to end connection", e);
            } finally {
                this.socketChannel = null;
            }
        }
    }

    /**
     * Listens continuously on the connected socket, parsing incoming JSON messages
     * into {@link Listened} objects and passing the extracted file commands to the consumer.
     *
     * @param consumer the consumer to process the extracted commands
     * @throws IllegalStateException if the socket has not been successfully subscribed to
     */
    public void listen(Consumer<String> consumer) {
        if (this.socketChannel == null) {
            throw new IllegalStateException("Cannot listen before subscribing");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Channels.newInputStream(this.socketChannel),
                        StandardCharsets.UTF_8))) {

            String message;

            while ((message = reader.readLine()) != null) {
                try {
                    Listened listened = this.mapper
                            .readValue(message, Listened.class);

                    String ext = listened.extractFirstImageFileExt();
                    if (ext.isBlank()) {
                        continue;
                    }

                    consumer.accept(
                            listened.extractCmdFile(ext));
                } catch (Exception e) {
                    log.log(Level.WARNING, "Ignored invalid or malformed listened message: " + e.getMessage());
                }
            }
        } catch (ClosedChannelException e) {
            log.log(Level.INFO, "Connection stopped.");
        } catch (IOException e) {
            log.log(Level.SEVERE,
                    "Connection dropped or error reading: " + e.getMessage());
        }
    }
}