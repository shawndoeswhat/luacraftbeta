package com.shawnjb.luacraftbeta.docs;

import com.shawnjb.luacraftbeta.lua.BindingCore;
import com.shawnjb.luacraftbeta.lua.BindingEntities;
import com.shawnjb.luacraftbeta.lua.BindingPlayers;
import com.shawnjb.luacraftbeta.lua.BindingWorld;
import com.shawnjb.luacraftbeta.lua.BindingMaterials;
import com.shawnjb.luacraftbeta.lua.BindingChat;
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
        BindingEntities.registerDocs();
        BindingPlayers.registerDocs();
        BindingWorld.registerDocs();
        BindingMaterials.registerDocs();
        BindingChat.registerDocs();

        LuaPlayer.registerDocs();
        LuaVector3.registerDocs();
        LuaWorld.registerDocs();
        LuaMaterial.registerDocs();
        LuaItemStack.registerDocs();
        LuaEntity.registerDocs();
        LuaBlock.registerDocs();
    }
}
