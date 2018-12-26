package com.github.newk5.vcmp.javascript.plugin.internals;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8ScriptExecutionException;
import static com.github.newk5.vcmp.javascript.plugin.internals.Runtime.console;
import com.github.newk5.vcmp.javascript.plugin.internals.result.AsyncResult;
import com.github.newk5.vcmp.javascript.plugin.internals.result.ResultBuilder;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.pmw.tinylog.Logger;

public class EventLoop {

    public Queue<AsyncResult> queue = new ConcurrentLinkedQueue<>();
    public ConcurrentHashMap<String, ResultBuilder> resultBuilder = new ConcurrentHashMap<>();

    public void process() {
        Iterator<AsyncResult> it = queue.iterator();

        while (it.hasNext()) {
            try {
                AsyncResult result = it.next();
                it.remove();
                V8Array args = result.build();
                if (result.getCallback() != null) {
                    result.getCallback().call(null, args);
                    if (!result.isMaintainCallback()) {
                        result.getCallback().release();
                    }
                }

            } catch (Exception e) {
                Logger.error(e);

                if (e instanceof V8ScriptExecutionException) {
                    exception((V8ScriptExecutionException) e);
                } else {

                    if (e.getCause() != null) {
                        console.error(e.getCause().getMessage());
                    } else {
                        console.error(e.getMessage());
                    }
                }
            }
        }

    }

    public static void exception(V8ScriptExecutionException e) {
        String msg = e.getJSMessage();
        if (!msg.equals("TypeError: undefined is not a function")) {
            e.printStackTrace();
            String method = "";
            if (e.getStackTrace().length > 0) {
                method = e.getStackTrace()[e.getStackTrace().length - 1].getMethodName();
            }
            Logger.error("(" + e.getFileName() + ":" + e.getLineNumber() + ":" + e.getStartColumn() + ") " + msg + " :: " + method);
        }

    }

}
