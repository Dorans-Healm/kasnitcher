package prism.adapter.operation;

import lombok.extern.java.Log;
import prism.application.service.WriteService;
import prism.configuration.AppStartup;
import prism.configuration.adapter.ParameterAdapter;
import prism.configuration.context.ExecutionerContext;
import prism.adapter.cli.input.Command;
import prism.domain.exception.ArgumentNotFoundException;
import prism.domain.model.Prism;
import prism.domain.vo.ColorCluster;
import prism.infrastructure.filesystem.FileDataWriter;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Operation that serves as the entry point for a one-off client execution.
 * <p>
 * This initializes an {@link ExecutionerContext} to hold configuration needed for sending a
 * single request to the background daemon.
 */
@Log
public class ExecutionerOperation extends AppStartup {

    private final Command[] commands;

    /**
     * Constructs a new executioner operation with the specified commands.
     *
     * @param commands the array of parsed CLI commands
     * @param context the executioner context
     */
    private ExecutionerOperation(Command[] commands, ExecutionerContext context) {
        super(() -> context);
        this.commands = commands;
    }

    /**
     * Starts a single execution with the given commands.
     *
     * @param commands the configuration commands
     */
    public static void execute(Command[] commands) {
        ExecutionerContext context = new ExecutionerContext();
        new ExecutionerOperation(commands, context).startSingleExecution(context);
    }

    /**
     * Process one-off execution call.
     */
    private void startSingleExecution(ExecutionerContext context) {
        try {
            context.updateConfiguration(this.commands);

            WriteService writeService = super.getAppContext().getClass(WriteService.class);

            String filePath = this.getArgumentFile(context);

            List<ColorCluster> imageColors = writeService.processImage(filePath);

            if (Objects.isNull(imageColors)) {
                throw new IllegalStateException("Could not read colors from image %s".formatted(filePath));
            }

            Prism color = writeService.getPrism(imageColors);

            FileDataWriter writer = super.getAppContext().getClass(FileDataWriter.class);

            writer.writeSpectrum(color);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Execution failed: " + e.getMessage(), e);
            System.exit(1);
        }
    }

    private String getArgumentFile(ExecutionerContext executionerContext) {
        ParameterAdapter parameterAdapter = executionerContext.getParameterAdapter();

        if (parameterAdapter == null) {
            throw new ArgumentNotFoundException("No file argument found");
        }

        return parameterAdapter.getFile();
    }
}