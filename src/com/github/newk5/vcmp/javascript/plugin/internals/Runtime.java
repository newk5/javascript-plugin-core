package com.github.newk5.vcmp.javascript.plugin.internals;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.github.newk5.vcmp.javascript.plugin.module.Module;
import com.github.newk5.vcmp.javascript.plugin.output.Console;
import io.alicorn.v8.V8JavaAdapter;
import io.alicorn.v8.V8JavaObjectUtils;
import io.alicorn.v8.annotations.JSIgnore;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.pmw.tinylog.Logger;

public class Runtime {

    public static V8 v8;
    public static EventLoop eventLoop;
    public static Console console;

    @JSIgnore
    public static V8Value toJavascript(Object o) {
        return (V8Value) V8JavaObjectUtils.translateJavaArgumentToJavascript(o, Runtime.v8, V8JavaAdapter.getCacheForRuntime(Runtime.v8));
    }

    @JSIgnore
    public static Object toJava(Class klass, Object o, V8Object receiver) {
        return V8JavaObjectUtils.translateJavascriptArgumentToJava(klass, o, receiver, V8JavaAdapter.getCacheForRuntime(Runtime.v8));
    }

    @JSIgnore
    public static V8Array toJavascriptArgs(Object o) {
        return V8JavaObjectUtils.translateJavaArgumentsToJavascript(new Object[]{o}, Runtime.v8, V8JavaAdapter.getCacheForRuntime(Runtime.v8));
    }

    @JSIgnore
    public static void load() {
        try {

            Runtime.eventLoop = new EventLoop();
            console = new Console();
            long startV8 = System.currentTimeMillis();
            Runtime.v8 = V8.createV8Runtime();
            long endV8 = System.currentTimeMillis();

            PluginManager pluginManager = new DefaultPluginManager(new File("modules"));
            pluginManager.loadPlugins();
            pluginManager.startPlugins();

            List<PluginWrapper> plugins = pluginManager.getPlugins();
            for (PluginWrapper p : plugins) {
                if (p.getPluginState() == PluginState.STARTED) {

                    String path = System.getProperty("user.dir") + "/src/node_modules/" + p.getPluginId();
                    Module module = pluginManager.getExtensions(Module.class, p.getPluginId()).get(0);
                    File f = new File(path);

                    if (!f.exists() || !f.isDirectory()) {
                        f.mkdirs();
                        String code = "/*" + p.getDescriptor().getVersion() + "*/\n\n" + module.javascript();
                        Files.write(new File(path + "/index.js").toPath(), code.getBytes(), StandardOpenOption.CREATE);
                    }
                    module.inject();

                    console.printer.green(">>> Loaded module " + p.getPluginId() + "-" + p.getDescriptor().getVersion() + " by " + p.getDescriptor().getProvider());
                    Logger.info(">>> Loaded module " + p.getPluginId() + "-" + p.getDescriptor().getVersion() + " by " + p.getDescriptor().getProvider());
                } else {
                    console.printer.error(">>> Failed to load module " + p.getPluginId() + "-" + p.getDescriptor().getVersion() + " by " + p.getDescriptor().getProvider());
                    Logger.error(">>> Failed to load module " + p.getPluginId() + "-" + p.getDescriptor().getVersion() + " by " + p.getDescriptor().getProvider());

                }
            }

            Logger.info("V8 Runtime initialized (" + (endV8 - startV8) + "ms)");

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}
