package com.shawnjb.luacraftbeta.lua;

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

    public void registerAll() {
        BindingCore.register(globals);
        BindingMC.register(globals);
    }
}
