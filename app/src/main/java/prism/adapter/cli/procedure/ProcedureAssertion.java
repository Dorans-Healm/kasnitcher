package prism.adapter.cli.procedure;


import prism.Main;
import prism.adapter.cli.AppCommandType;
import prism.adapter.cli.AppSubCommandType;
import prism.domain.exception.CommandNotFoundException;
import prism.domain.exception.DaemonDownOnCommandException;
import prism.domain.exception.OrphanSubCommandTypeException;
import prism.adapter.cli.input.Command;
import prism.infrastructure.daemon.SocketServer;
import prism.infrastructure.daemon.SocketStatusType;
import prism.utils.ArrayUtils;

import java.util.Objects;

public class ProcedureAssertion {

    public static void assertCall(String arg) {
        if (Main.START_CMD.equalsIgnoreCase(arg)) {
            return;
        }

        if (Objects.nonNull(SocketServer.getSocketStatusType())
                && !SocketServer.getSocketStatusType().equals(SocketStatusType.READY)) {
            String[] daemonCmds = AppCommandType.getDaemonCmds();
            if (ArrayUtils.contains(daemonCmds, arg)) {
                throw new DaemonDownOnCommandException(("Daemon command %s, " +
                        "should only be used after process is active and ready").formatted(arg));
            }
        }

        String[] exeCmd = AppCommandType.getExeCmds();
        if (ArrayUtils.contains(exeCmd, arg)) {
            return;
        }

        String[] subCmds = AppSubCommandType.getSubCmds();
        if (ArrayUtils.contains(subCmds, arg)) {
            throw new OrphanSubCommandTypeException(("Sub command %s, should " +
                    "not be used alone. Check --help for system usages.").formatted(arg));
        }

        throw new CommandNotFoundException(("Command %s, " +
                "not found. Check --help for system usages.").formatted(arg));
    }

    public static Command[] assertAndGetExecutionerCall(String... args) {
        return assertAndGetCommands(
                AppCommandType.getDaemonNonPolymathCmds(), "Daemon command %s, should not be " +
                        "used as a single execution system command. Check --help for system usages.", args);
    }

    public static Command[] assertAndGetDaemonCall(String... args) {
        return assertAndGetCommands(
                AppCommandType.getExeNonPolymathCmds(), "Single execution command %s, " +
                        "should not be used as a Daemon system command. Check --help for system usages.", args);
    }

    private static Command[] assertAndGetCommands(
            String[] rejectedCommands,
            String rejectedMessage,
            String[] args
    ) {
        Command[] commandsArray = new Command[]{};
        Command crrCommand = new Command();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            AppCommandType commandArg = AppCommandType.getByCommand(arg);

            if (Objects.nonNull(commandArg)) {
                if (!ArrayUtils.contains(rejectedCommands, arg)) {
                    throw new IllegalStateException(
                            rejectedMessage.formatted(arg));
                }

                if (Objects.nonNull(crrCommand.getCommand())) {
                    commandsArray = (Command[])
                            ArrayUtils.add(commandsArray, crrCommand);

                    crrCommand = new Command();
                    crrCommand.setCommand(commandArg);
                }

                crrCommand.setCommand(commandArg);

                continue;
            }

            AppSubCommandType subCmdArg = AppSubCommandType.getByCommand(arg);

            if (Objects.nonNull(subCmdArg)) {
                AppCommandType appCommandArg = crrCommand.getCommand();

                if (Objects.isNull(appCommandArg)) {
                    throw new IllegalStateException(("Sub command %s, " +
                            "found when no base command was given").formatted(arg));
                }

                AppSubCommandType[] commandArgSubCommands = appCommandArg.getSubCommands();
                String[] subCmdArgs = AppSubCommandType.getCommandsByArray(commandArgSubCommands);

                if (!ArrayUtils.contains(subCmdArgs, arg)) {
                    throw new IllegalArgumentException(("Sub command %s, can " +
                            "not be used with %s, command").formatted(arg, appCommandArg));
                }

                crrCommand.add(arg);
                if (i + 1 < args.length) {
                    if (isAnyCommandType(args[i + 1])) {
                        if (subCmdArg.isNullable()) {
                            continue;
                        }

                        throw new IllegalStateException(
                                "Sub command %s, used without a value.".formatted(arg));
                    }

                    crrCommand.add(args[i + 1]);
                    i++;
                }

                continue;
            }

            throw new CommandNotFoundException(("Command %s, " +
                    "not found. Check --help for system usages.").formatted(args[i]));
        }

        return commandsArray;
    }

    private static Boolean isAnyCommandType(String arg) {
        AppCommandType appCommandType = AppCommandType.getByCommand(arg);
        if (Objects.nonNull(appCommandType)) {
            return true;
        }

        AppSubCommandType appSubCommandType = AppSubCommandType.getByCommand(arg);
        return Objects.nonNull(appSubCommandType);
    }
}