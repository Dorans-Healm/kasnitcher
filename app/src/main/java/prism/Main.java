package prism;

import prism.application.ProcessorCall;
import prism.utils.ObjectUtils;

public class Main {

    public static final String START_CMD = "start";
    public static final String STOP_CMD = "start";

    static void main(String... args) {
        try {
            if (args.length <= 0) {
                AppOperation.start();
                return;
            }

            String cmd = args[0];
            ProcessorCall.assertCall(cmd);

            if (ObjectUtils.stringsNotEqualsCaseInsensitive(cmd, START_CMD)) {
                ProcessorCall.justifyExecutionerCall(args);

                ExecutionerOperation.execute();
                return;
            }

            ProcessorCall.justifyExecutionerCall(args);

            AppOperation.start(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}