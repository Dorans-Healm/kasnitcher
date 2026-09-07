package prism.adapter.operation;

import prism.adapter.cli.AppSubCommandType;
import prism.configuration.apdater.WriterAdapter;
import prism.configuration.context.ExecutionerContext;
import prism.domain.model.Command;
import prism.utils.ArrayUtils;
import prism.utils.FileUtils;

import java.util.Objects;

import static prism.adapter.cli.AppSubCommandType.*;

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
                this.getExecutionerContext(this.commands);

    }

    private ExecutionerContext getExecutionerContext(Command[] commands) {
        ExecutionerContext context = new ExecutionerContext();

        for (Command command : commands) {
            switch (command.getCommand()) {
                case WRITE -> context.setWritingConfiguration(
                        this.getWriterAdapter(command.getSubCommands()));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }

        return context;
    }

    private WriterAdapter getWriterAdapter(String[] subCommands) {
        WriterAdapter writerAdapter = new WriterAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            AppSubCommandType subCommand = AppSubCommandType.getByCommand(command);
            if (Objects.isNull(subCommand)) {
                throw new IllegalArgumentException(
                        "%s is not a valid Sub command.".formatted(command));
            }

            if (ArrayUtils.contains(DIRECTORY.getSubCommands(), subCommand)) {
                if (!DIRECTORY.isNullable()) {
                    String dir = subCommands[i++];

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    writerAdapter.setDirectory(dir);
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), subCommand)) {
                if (!FILE.isNullable()) {
                    writerAdapter.setFile(subCommands[i++]);
                }
            }
        }

        return writerAdapter;
    }
}