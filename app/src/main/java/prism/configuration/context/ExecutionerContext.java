package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.ParameterAdapter;
import prism.configuration.adapter.WriterAdapter;

/**
 * Configuration context for a one-off command execution process (the client).
 * <p>
 * Holds the parsed adapter settings required by the executioner (e.g. writer
 * configurations) to send the correct state to the call.
 */
@Setter
@Getter
public class ExecutionerContext extends AbstractServiceContextConfiguration {

    private WriterAdapter writerAdapter = new WriterAdapter();

    private ParameterAdapter parameterAdapter;

    /**
     * Updates the executioner's internal adapter configurations based on the
     * provided CLI commands.
     *
     * @param commands an array of parsed {@link Command}s from the CLI
     * @throws IllegalArgumentException if an unsupported command is encountered
     */
    @Override
    public void updateConfiguration(@NonNull Command[] commands) {
        for (Command command : commands) {
            switch (command.getCommand()) {
                case WRITE -> this.setWriterAdapter(
                        super.getWriterAdapter(this.writerAdapter, command.getSubCommands()));

                case PARAMETER -> this.setParameterAdapter(
                        super.getParameterAdapter(command.getSubCommands()));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }
    }

    public ParameterAdapter getParameterAdapter() {
        ParameterAdapter parameterAdapter = this.parameterAdapter;
        this.parameterAdapter = null;

        return parameterAdapter;
    }
}