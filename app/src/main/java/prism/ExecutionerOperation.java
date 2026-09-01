package prism;

public class ExecutionerOperation {

    private final String[] args;

    private ExecutionerOperation() {
        this.args = new String[0];
    }

    private ExecutionerOperation(String... args) {
        this.args = args;
    }

    public static void execute(String... args) {
        new ExecutionerOperation(args)
                .startSingleExecution();
    }

    private void startSingleExecution() {

    }
}