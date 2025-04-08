package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.LuaManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaBindings {
    private final Globals globals;

    public LuaBindings() {
        this.globals = JsePlatform.standardGlobals();
    }

    public Globals getGlobals() {
        return globals;
    }

    public void registerAll(JavaPlugin plugin, LuaManager manager) {
        BindingCore.register(globals);

        BindingMC.init(plugin, manager);
        BindingMC.register(globals);
    }
}
