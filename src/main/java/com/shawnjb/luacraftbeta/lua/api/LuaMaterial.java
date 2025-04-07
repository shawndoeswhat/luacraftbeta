package com.shawnjb.luacraftbeta.lua.api;

import java.util.Arrays;

import org.bukkit.Material;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

public class LuaMaterial {
    private final Material material;

    public LuaMaterial(Material material) {
        this.material = material;
    }

    public LuaTable toLuaTable() {
        LuaTable t = new LuaTable();

        t.set("getId", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(material.getId());
            }
        });

        t.set("getMaxStackSize", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(material.getMaxStackSize());
            }
        });

        t.set("getMaxDurability", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(material.getMaxDurability());
            }
        });

        t.set("getName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(material.name().toLowerCase());
            }
        });

        t.set("isBlock", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(material.isBlock());
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaMaterial");
    
        LuaDocRegistry.addFunction("LuaMaterial", "getId",
                "Gets the numeric ID of the material.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaMaterial")),
                Arrays.asList(new LuaDocRegistry.Return("number", "The Bukkit material ID")),
                true);
    
        LuaDocRegistry.addFunction("LuaMaterial", "getMaxStackSize",
                "Returns the maximum stack size for this material.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaMaterial")),
                Arrays.asList(new LuaDocRegistry.Return("number", "")),
                true);
    
        LuaDocRegistry.addFunction("LuaMaterial", "getMaxDurability",
                "Returns the max durability of the material.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaMaterial")),
                Arrays.asList(new LuaDocRegistry.Return("number", "")),
                true);
    
        LuaDocRegistry.addFunction("LuaMaterial", "getName",
                "Gets the lowercase name of the material.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaMaterial")),
                Arrays.asList(new LuaDocRegistry.Return("string", "")),
                true);
    
        LuaDocRegistry.addFunction("LuaMaterial", "isBlock",
                "Returns true if this material is a placeable block.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaMaterial")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "")),
                true);
    }    
}