package prism.domain.model;

import lombok.Getter;
import lombok.Setter;
import prism.adapter.cli.AppCommandType;
import prism.utils.ArrayUtils;

@Getter
@Setter
public class Command {

    private AppCommandType command;

    private String[] subCommands;

    public Command() {
        this.subCommands = new String[]{};
    }

    public void add(String subCommand) {
        this.subCommands = (String[])
                ArrayUtils.add(this.subCommands, subCommand);
    }
}