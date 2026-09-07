package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import prism.adapter.cli.AppSubCommandType;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.WriterAdapter;
import prism.utils.ArrayUtils;
import prism.utils.FileUtils;

import java.util.Objects;

import static prism.adapter.cli.AppSubCommandType.*;

@Setter
@Getter
public class ExecutionerContext implements ServiceContextConfiguration {

    private WriterAdapter writingConfiguration;

    public void updateConfiguration(Command[] commands) {
        for (Command command : commands) {
            switch (command.getCommand()) {
                case WRITE -> this.setWritingConfiguration(
                        this.getWriterAdapter(command.getSubCommands()));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }
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

            if (ArrayUtils.contains(DIRECTORY.getSubCommands(), command)) {
                if (!DIRECTORY.isNullable()) {
                    String dir = subCommands[i + 1];
                    i++;

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    writerAdapter.setDirectory(dir);
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    writerAdapter.setFile(subCommands[i + 1]);
                    i++;
                }
            }

            if (ArrayUtils.contains(TYPE.getSubCommands(), command)) {
                if (!TYPE.isNullable()) {
                    String type  = subCommands[i + 1];

                    if (!WriterAdapter.RGB.equals(type) && !WriterAdapter.HEX.equals(type)) {
                        throw new IllegalArgumentException(("%s is not a " +
                                "valid type for color writing.").formatted(type));
                    }

                    writerAdapter.setType(type);
                    i++;
                }
            }
        }

        return writerAdapter;
    }
}