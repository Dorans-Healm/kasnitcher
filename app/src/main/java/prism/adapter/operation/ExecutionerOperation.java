package prism.adapter.operation;

import prism.configuration.context.ExecutionerContext;
import prism.adapter.cli.input.Command;

/**
 * Operation that serves as the entry point for a on-off client execution.
 * <p>
 * This initializes an {@link ExecutionerContext} to hold configuration needed for sending a
 * single request to the background daemon.
 */
public class ExecutionerOperation {

    private final Command[] commands;

    /**
     * Constructs a new executioner operation with no commands.
     */
    private ExecutionerOperation() {
        this.commands = new Command[]{};
    }

    /**
     * Constructs a new executioner operation with the specified commands.
     *
     * @param commands the array of parsed CLI commands
     */
    private ExecutionerOperation(Command[] commands) {
        this.commands = commands;
    }

    /**
     * Starts a single execution with the given commands.
     *
     * @param commands the configuration commands
     */
    public static void execute(Command[] commands) {
        new ExecutionerOperation(
                commands).startSingleExecution();
    }

    /**
     * Process on-off execution call.
     */
    private void startSingleExecution() {
        ExecutionerContext context =
                new ExecutionerContext();

        context.updateConfiguration(this.commands);


    }
}