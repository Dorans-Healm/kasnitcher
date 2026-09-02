package prism;

import lombok.extern.java.Log;
import prism.application.processing.ProcessJustifier;
import prism.utils.ObjectUtils;

@Log
public class Main {

    public static final String START_CMD = "start";
    public static final String STOP_CMD = "start";

    static void main(String... args) {
        try {
            if (args.length <= 0) {
                log.info("No arguments provided," +
                        "assuming Daemon initialization with no parameters");

                DaemonOperation.start();
                return;
            }

            String cmd = args[0];
            ProcessJustifier.assertCall(cmd);

            if (ObjectUtils.stringsNotEqualsCaseInsensitive(cmd, START_CMD)) {
                ProcessJustifier.justifyExecutionerCall(args);

                log.info("Single execution mode " +
                        "identified. Starting the process.");

                ExecutionerOperation.execute();
                return;
            }

            ProcessJustifier.justifyExecutionerCall(args);

            log.info("Daemon mode " +
                    "identified. Starting the process.");

            DaemonOperation.start(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}