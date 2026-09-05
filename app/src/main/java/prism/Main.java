package prism;


import lombok.extern.java.Log;
import prism.adapter.operation.DaemonOperation;
import prism.adapter.operation.ExecutionerOperation;
import prism.adapter.cli.procedure.ProcedureJustifier;
import prism.domain.exception.CommandNotFoundException;
import prism.domain.exception.DaemonDownOnCommandException;
import prism.domain.exception.OrphanSubCommandTypeException;
import prism.infrastructure.daemon.SocketServer;

import java.util.Objects;
import java.util.logging.Level;

@Log
public class Main {

    public static final String START_CMD = "start";
    public static final String STOP_CMD = "stop";

    static void main(String... args) {
        try {
            if (args.length <= 0) {
                log.info("No arguments provided," +
                        "assuming Daemon initialization with no parameters");

                DaemonOperation.start();
                return;
            }

            String cmd = args[0];
            ProcedureJustifier.assertCall(cmd);

            if (START_CMD.equalsIgnoreCase(cmd)) {
                ProcedureJustifier.justifyExecutionerCall(args);

                log.info("Daemon mode " +
                        "identified. Starting the process.");

                DaemonOperation.start(args);
                return;
            }

            ProcedureJustifier.justifyExecutionerCall(args);

            log.info("Single execution mode " +
                    "identified. Starting the process.");

            ExecutionerOperation.execute();
        } catch (DaemonDownOnCommandException
                 | CommandNotFoundException | OrphanSubCommandTypeException e) {

            log.warning(e.getMessage());

        } catch (Exception e) {
            if (Objects.nonNull(SocketServer.getSocketStatusType()))
                log.log(Level.SEVERE, ("System error, " +
                        "exiting program with: %s").formatted(e.getCause()), e);

            log.severe(e.getMessage());

            System.exit(1);
        }
    }
}