package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Return;
import com.shawnjb.luacraftbeta.lua.api.LuaWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.List;

public class BindingWorld {
    public static void register(LuaValue globals) {
        LuaTable mc = globals.get("mc").isnil() ? new LuaTable() : globals.get("mc").checktable();

        mc.set("getWorld", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue worldName) {
                if (!worldName.isstring()) {
                    return LuaValue.valueOf("Error: getWorld expects a string.");
                }

                World world = Bukkit.getWorld(worldName.tojstring());
                if (world == null) {
                    return LuaValue.valueOf("Error: world not found.");
                }

                LuaWorld luaWorld = new LuaWorld(world);
                return luaWorld.toLuaTable();
            }
        });

        globals.set("mc", mc);
    }

    public static void registerDocs() {
        LuaDocRegistry.addFunction(
                "mc",
                "getWorld",
                "Returns a world by its name.",
                List.of(new Param("worldName", "string")),
                List.of(new Return("table", "LuaWorld table")));
    }
}
