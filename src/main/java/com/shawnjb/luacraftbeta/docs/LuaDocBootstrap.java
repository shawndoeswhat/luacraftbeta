package com.shawnjb.luacraftbeta.docs;

import com.shawnjb.luacraftbeta.lua.BindingCore;
import com.shawnjb.luacraftbeta.lua.BindingMC;
import com.shawnjb.luacraftbeta.lua.api.LuaPlayer;
import com.shawnjb.luacraftbeta.lua.api.LuaVector3;
import com.shawnjb.luacraftbeta.lua.api.LuaWorld;
import com.shawnjb.luacraftbeta.lua.api.LuaMaterial;
import com.shawnjb.luacraftbeta.lua.api.LuaItemStack;
import com.shawnjb.luacraftbeta.lua.api.LuaEntity;
import com.shawnjb.luacraftbeta.lua.api.LuaBlock;

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
    }
}
