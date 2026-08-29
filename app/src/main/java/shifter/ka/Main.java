package shifter.ka;

import shifter.ka.application.ProcessorCall;
import shifter.ka.utils.ObjectUtils;

public class Main {

    public static final String START_CMD = "start";

    static void main(String... args) {
        try {
            if (args.length <= 0) {
                Daemon.start();
                return;
            }

            String cmd = args[0];
            ProcessorCall.assertCall(cmd);

            if (ObjectUtils.stringsNotEqualsCaseInsensitive(cmd, START_CMD)) {
                ProcessorCall.justifyExecutionerCall(args);

                Executioner.execute();
                return;
            }

            ProcessorCall.justifyExecutionerCall(args);

            Daemon.start(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}