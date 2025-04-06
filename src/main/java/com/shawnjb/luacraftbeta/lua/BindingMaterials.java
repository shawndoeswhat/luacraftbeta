package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Return;
import com.shawnjb.luacraftbeta.lua.api.LuaMaterial;
import org.bukkit.Material;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.Arrays;

public class BindingMaterials {
    public static void register(LuaValue globals) {
        LuaTable mc = globals.get("mc").isnil() ? new LuaTable() : globals.get("mc").checktable();

        mc.set("getMaterial", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue materialId) {
                if (materialId.isnumber()) {
                    int id = materialId.toint();
                    Material material = Material.getMaterial(id);
                    if (material != null) {
                        return new LuaMaterial(material).toLuaTable();
                    }
                    return LuaValue.valueOf("Invalid material ID: " + id);
                }
                return LuaValue.valueOf("Usage: getMaterial(id) where id is a numeric ID.");
            }
        });

        globals.set("mc", mc);
    }

    public static void registerDocs() {
        LuaDocRegistry.addFunction(
                "mc",
                "getMaterial",
                "Returns a material by its numeric ID.",
                Arrays.asList(new Param("id", "integer")),
                Arrays.asList(new Return("table", "LuaMaterial table if found, or an error message string")));
    }
}
