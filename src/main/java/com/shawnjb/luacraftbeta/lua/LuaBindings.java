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
        BindingCore.register(globals); // print, wait, Vector3
        BindingChat.register(globals); // mc.broadcast, mc.sendMessage
        BindingPlayers.register(globals); // mc.getPlayer, mc.getOnlinePlayers
        BindingEntities.register(globals); // mc.summon
        BindingWorld.register(globals); // mc.getWorld, world:setTime()
        BindingMaterials.register(globals); // mc.getMaterial, mc.isBlockSolid, etc.
    }
}
