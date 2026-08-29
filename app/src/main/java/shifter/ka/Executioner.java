package shifter.ka;

public class Executioner {

    private final String[] args;

    private Executioner() {
        this.args = new String[0];
    }

    private Executioner(String... args) {
        this.args = args;
    }

    public static void execute(String... args) {
        new Executioner(args)
                .startSingleExecution();
    }

    private void startSingleExecution() {

    }
}