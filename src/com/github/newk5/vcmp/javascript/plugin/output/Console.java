package com.github.newk5.vcmp.javascript.plugin.output;

import org.pmw.tinylog.Logger;

public class Console {

    public static ConsolePrinter printer;

    public Console() {
        printer = new ConsolePrinter();
    }

    public synchronized void error(Object msg) {
        if (msg == null) {
            printer.error("null");
            return;

        }
        printer.error(msg.toString());
    }

    public synchronized void warn(Object msg) {
        if (msg == null) {
            printer.warn("null");
            return;

        }
        printer.warn(msg.toString());
    }

    public synchronized void log(Object msg) {
        if (msg == null) {
            printer.print("null");
            Logger.info(msg);
            return;
        }
        Logger.info(msg.toString());
        printer.print(msg.toString());
    }

    public synchronized void success(Object msg) {
        if (msg == null) {
            printer.success("null");
            return;

        }
        printer.success(msg.toString());
    }

}
