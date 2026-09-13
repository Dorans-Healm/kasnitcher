package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.WriterAdapter;

/**
 * Configuration context for a one-off command execution process (the client).
 * <p>
 * Holds the parsed adapter settings required by the executioner (e.g. writer
 * configurations) to send the correct state to the call.
 */
@Setter
@Getter
public class ExecutionerContextAbstract extends AbstractServiceContextConfiguration {

    private WriterAdapter writingConfiguration;

    /**
     * Updates the executioner's internal adapter configurations based on the
     * provided CLI commands.
     *
     * @param commands an array of parsed {@link Command}s from the CLI
     * @throws IllegalArgumentException if an unsupported command is encountered
     */
    public void updateConfiguration(Command[] commands) {
        for (Command command : commands) {
            switch (command.getCommand()) {
                case WRITE -> this.setWritingConfiguration(
                        super.getWriterAdapter(command.getSubCommands()));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }
    }
}