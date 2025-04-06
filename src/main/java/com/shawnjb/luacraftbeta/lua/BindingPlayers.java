package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Return;
import com.shawnjb.luacraftbeta.lua.api.LuaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.List;

public class BindingPlayers {
    public static void register(LuaValue globals) {
        LuaTable mc = globals.get("mc").isnil() ? new LuaTable() : globals.get("mc").checktable();

        mc.set("getPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String name = arg.tojstring();
                Player player = Bukkit.getPlayer(name);
                if (player != null && player.isOnline()) {
                    return new LuaPlayer(player).toLuaTable();
                } else {
                    LuaTable error = new LuaTable();
                    error.set("error", "Player not found.");
                    return error;
                }
            }
        });

        mc.set("getOnlinePlayers", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue unused) {
                LuaTable playersTable = new LuaTable();
                int index = 1;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playersTable.set(index++, p.getName());
                }
                return playersTable;
            }
        });

        globals.set("mc", mc);
    }

    public static void registerDocs() {
        LuaDocRegistry.addFunction(
                "mc",
                "getPlayer",
                "Gets a player by name if they are online. Returns a LuaPlayer or error table.",
                List.of(new Param("name", "string")),
                List.of(new Return("table", "LuaPlayer or error table")));

        LuaDocRegistry.addFunction(
                "mc",
                "getOnlinePlayers",
                "Returns a list of online player names.",
                List.of(),
                List.of(new Return("string[]", "Array of online player names")));
    }
}
