package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Return;
import com.shawnjb.luacraftbeta.lua.api.LuaVector3;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import java.util.Arrays;


public class BindingCore {
    private static JavaPlugin plugin;

    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    public static void register(LuaValue globals) {
        globals.set("print", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String message = arg.tojstring();
                Bukkit.broadcastMessage("[Lua] " + message);
                return NIL;
            }
        });

        globals.set("wait", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaValue secondsArg = args.arg(1);
                if (!secondsArg.isnumber()) {
                    return LuaValue.error("Usage: wait(seconds)");
                }

                double seconds = secondsArg.todouble();
                int delayTicks = (int) (seconds * 20);

                final LuaThread thread = (LuaThread) args.arg(0);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    thread.resume(LuaValue.NIL);
                }, delayTicks);

                return LuaValue.NONE;
            }
        });

        globals.set("Vector3", new LuaTable() {
            {
                set("new", new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        if (args.narg() >= 3 &&
                                args.arg(1).isnumber() &&
                                args.arg(2).isnumber() &&
                                args.arg(3).isnumber()) {

                            double x = args.arg(1).todouble();
                            double y = args.arg(2).todouble();
                            double z = args.arg(3).todouble();

                            return new LuaVector3(x, y, z).toLuaTable();
                        }
                        return LuaValue.error("Usage: Vector3.new(x, y, z)");
                    }
                });
            }
        });
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("Vector3");

        LuaDocRegistry.addFunction(
                "core",
                "print",
                "Prints a message to the server chat prefixed with [Lua].",
                Arrays.asList(new Param("message", "string")),
                null);

        LuaDocRegistry.addFunction(
                "core",
                "wait",
                "Yields the coroutine for a number of seconds, then resumes.",
                Arrays.asList(new Param("seconds", "number")),
                null);

        LuaDocRegistry.addFunction(
                "core",
                "Vector3.new",
                "Creates a new 3D vector with x, y, z coordinates.",
                Arrays.asList(
                        new Param("x", "number"),
                        new Param("y", "number"),
                        new Param("z", "number")),
                Arrays.asList(new Return("Vector3", "A new vector object")));
    }
}