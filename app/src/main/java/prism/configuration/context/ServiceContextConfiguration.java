package prism.configuration.context;

import prism.adapter.cli.input.Command;

public interface ServiceContextConfiguration {

    void updateConfiguration(Command[] commands);
}