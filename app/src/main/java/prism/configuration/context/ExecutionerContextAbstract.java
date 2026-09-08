package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.WriterAdapter;

@Setter
@Getter
public class ExecutionerContextAbstract extends AbstractServiceContextConfiguration {

    private WriterAdapter writingConfiguration;

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