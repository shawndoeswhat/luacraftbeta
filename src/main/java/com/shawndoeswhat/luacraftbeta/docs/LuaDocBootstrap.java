package com.shawndoeswhat.luacraftbeta.docs;

import com.shawndoeswhat.luacraftbeta.lua.BindingCore;
import com.shawndoeswhat.luacraftbeta.lua.BindingMC;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaPlayer;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaVector3;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaWorld;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaMaterial;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaItemStack;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaEntity;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaBlock;

public class LuaDocBootstrap {
    public static void registerAll() {
        BindingCore.registerDocs();
        BindingMC.registerDocs();

        LuaPlayer.registerDocs();
        LuaVector3.registerDocs();
        LuaWorld.registerDocs();
        LuaMaterial.registerDocs();
        LuaItemStack.registerDocs();
        LuaEntity.registerDocs();
        LuaBlock.registerDocs();

        LuaDocRegistry.registerMC_ACTIONDocs();
    }
}
