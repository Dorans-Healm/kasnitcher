package prism.adapter.cli.input;

import lombok.Getter;
import lombok.Setter;
import prism.adapter.cli.AppCommandType;
import prism.utils.ArrayUtils;

/**
 * Represents a parsed command along with its associated sub-commands.
 */
@Getter
@Setter
public class Command {

    /**
     * The main application command type.
     */
    private AppCommandType command;

    /**
     * The list of sub-command strings associated with the main command.
     */
    private String[] subCommands;

    /**
     * Constructs an empty command with no sub-commands.
     */
    public Command() {
        this.subCommands = new String[]{};
    }

    /**
     * Adds a sub-command to the existing list of sub-commands.
     *
     * @param subCommand the sub-command string to add
     */
    public void add(String subCommand) {
        this.subCommands = ArrayUtils
                .add(this.subCommands, subCommand);
    }
}