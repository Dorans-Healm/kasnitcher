package prism;


import lombok.extern.java.Log;
import prism.adapter.operation.DaemonOperation;
import prism.adapter.operation.ExecutionerOperation;
import prism.adapter.cli.procedure.ProcedureJustifier;

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}