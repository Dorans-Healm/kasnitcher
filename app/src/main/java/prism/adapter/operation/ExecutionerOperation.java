package prism.adapter.operation;

import prism.configuration.context.ExecutionerContext;
import prism.adapter.cli.input.Command;

public class ExecutionerOperation {

    private final Command[] commands;

    private ExecutionerOperation() {
        this.commands = new Command[]{};
    }

    private ExecutionerOperation(Command[] commands) {
        this.commands = commands;
    }

    public static void execute(Command[] commands) {
        new ExecutionerOperation(
                commands).startSingleExecution();
    }

    private void startSingleExecution() {
        ExecutionerContext context =
                new ExecutionerContext();

        context.updateConfiguration(this.commands);
    }
}