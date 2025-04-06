package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.List;

public class BindingChat {
    public static void register(LuaValue globals) {
        LuaTable mc = globals.get("mc").isnil() ? new LuaTable() : globals.get("mc").checktable();

        mc.set("broadcast", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Bukkit.broadcastMessage(arg.tojstring());
                return NIL;
            }
        });

        mc.set("sendMessage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue playerName, LuaValue message) {
                Player player = Bukkit.getPlayer(playerName.tojstring());
                if (player != null && player.isOnline()) {
                    player.sendMessage(message.tojstring());
                }
                return NIL;
            }
        });

        globals.set("mc", mc);
    }

    public static void registerDocs() {
        LuaDocRegistry.addFunction(
                "mc",
                "broadcast",
                "Broadcasts a message to all players on the server.",
                List.of(new Param("message", "string")),
                null);

        LuaDocRegistry.addFunction(
                "mc",
                "sendMessage",
                "Sends a private message to the specified player.",
                List.of(
                        new Param("playerName", "string"),
                        new Param("message", "string")),
                null);
    }
}
