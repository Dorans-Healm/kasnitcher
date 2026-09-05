package prism.infrastructure.daemon;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import prism.domain.exception.SocketInterruptionException;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SocketServer implements AutoCloseable {

    @Getter
    @Setter
    private static SocketStatusType socketStatusType;

    @Getter
    private ServerSocketChannel serverSocketChannel;

    @Override
    public void close() {
        this.interrupt();
    }

    public void open() throws IOException {
        this.serverSocketChannel =
                ServerSocketChannel.open(StandardProtocolFamily.UNIX);

        socketStatusType = SocketStatusType.ACTIVATING;
    }

    public void interrupt()  {
        if (Objects.isNull(this.serverSocketChannel)) {
            return;
        }

        try {
            if (this.serverSocketChannel.isOpen()) {
                this.serverSocketChannel.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("EndlessStream")
    public void listen(@NotNull Consumer<@Nullable SocketChannel> consumer) {
        Stream.generate(this::accept).forEach(consumer);
    }

    public @Nullable SocketChannel accept() {
        try {
            return this.serverSocketChannel.accept();
        } catch (AsynchronousCloseException e) {
            if (Objects.equals(SocketStatusType.GRACEFULL_INTERRUPTION, socketStatusType)) {
                this.interrupt();
                return null;
            }

            throw new SocketInterruptionException(
                    "Interrupted socket communication", SocketStatusType.FORCEFULL_INTERRUPTION);
        } catch (IOException e) {
            throw new RuntimeException("Socket acceptance error.");
        }
    }
}