package prism.adapter.operation;

import lombok.extern.java.Log;
import prism.application.service.WriteService;
import prism.configuration.adapter.ArgumentAdapter;
import prism.configuration.context.ExecutionerContext;
import prism.adapter.cli.input.Command;
import prism.domain.exception.ArgumentNotFoundException;
import prism.domain.model.Prism;
import prism.infrastructure.filesystem.FileDataWriter;

import java.util.logging.Level;

/**
 * Operation that serves as the entry point for a on-off client execution.
 * <p>
 * This initializes an {@link ExecutionerContext} to hold configuration needed for sending a
 * single request to the background daemon.
 */
@Log
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

        try {
            context.updateConfiguration(this.commands);

            WriteService writeService =
                    new WriteService(() -> context);

            String filePath = this.getArgumentFile(context);

            Integer[][] imageColors =
                    writeService.processImage(filePath);

            Prism color = writeService
                    .getPrism(imageColors);

            FileDataWriter writer =
                    new FileDataWriter(() -> context);

            writer.writeSpectrum(color);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String getArgumentFile(ExecutionerContext executionerContext) {
        ArgumentAdapter argumentAdapter =
                executionerContext.getArgumentAdapter();

        if (argumentAdapter == null) {
            throw new ArgumentNotFoundException("No file argument found");
        }

        return argumentAdapter.getFile();
    }
}