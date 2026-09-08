package prism.adapter.operation;

import prism.configuration.context.ExecutionerContextAbstract;
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
        ExecutionerContextAbstract context =
                new ExecutionerContextAbstract();

        context.updateConfiguration(this.commands);
    }
}