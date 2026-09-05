package prism.adapter.cli.procedure;


import prism.Main;
import prism.adapter.cli.AppCommandType;
import prism.adapter.cli.AppSubCommandType;
import prism.domain.exception.CommandNotFoundException;
import prism.domain.exception.DaemonDownOnCommandException;
import prism.domain.exception.OrphanSubCommandTypeException;
import prism.infrastructure.daemon.SocketServer;
import prism.infrastructure.daemon.SocketStatusType;
import prism.utils.ArrayUtils;

import java.util.Objects;

public class ProcedureJustifier {

    public static void assertCall(String arg) {
        if (Main.START_CMD.equalsIgnoreCase(arg)) {
            return;
        }

        if (Objects.nonNull(SocketServer.getSocketStatusType())
                && SocketServer.getSocketStatusType().equals(SocketStatusType.READY)) {
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

    public static void justifyExecutionerCall(String... args) {

    }

    public static void justifyDaemonCall(String... args) {

    }
}