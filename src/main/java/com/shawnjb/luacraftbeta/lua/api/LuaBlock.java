package com.shawnjb.luacraftbeta.lua.api;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

public class LuaBlock {
    private final Block block;

    public LuaBlock(Block block) {
        this.block = block;
    }

    public LuaTable toLuaTable() {
        LuaTable t = new LuaTable();

        t.set("getType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getType().toString().toLowerCase());
            }
        });

        t.set("getTypeId", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getTypeId());
            }
        });

        t.set("setType", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue typeName) {
                if (typeName.isstring()) {
                    Material mat = Material.getMaterial(typeName.tojstring().toUpperCase());
                    if (mat != null) {
                        block.setType(mat);
                        return LuaValue.NIL;
                    }
                    return LuaValue.valueOf("Invalid material: " + typeName.tojstring());
                }
                return LuaValue.valueOf("Usage: setType(materialName)");
            }
        });

        t.set("setData", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue val) {
                if (val.isnumber()) {
                    block.setData((byte) val.toint());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Usage: setData(byte)");
            }
        });

        t.set("setTypeId", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue id) {
                if (id.isnumber()) {
                    return LuaValue.valueOf(block.setTypeId(id.toint()));
                }
                return LuaValue.valueOf("Usage: setTypeId(id)");
            }
        });

        t.set("getPosition", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return new LuaVector3(block.getX(), block.getY(), block.getZ()).toLuaTable();
            }
        });

        t.set("getX", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getX());
            }
        });
        t.set("getY", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getY());
            }
        });
        t.set("getZ", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getZ());
            }
        });

        t.set("isEmpty", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.isEmpty());
            }
        });

        t.set("isLiquid", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.isLiquid());
            }
        });

        t.set("getData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getData());
            }
        });

        t.set("getLightLevel", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getLightLevel());
            }
        });

        t.set("getRelative", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue faceName) {
                if (!faceName.isstring()) {
                    return LuaValue.valueOf("Usage: getRelative(faceName)");
                }

                try {
                    BlockFace face = BlockFace.valueOf(faceName.tojstring().toUpperCase());
                    Block relative = block.getRelative(face);
                    return new LuaBlock(relative).toLuaTable();
                } catch (IllegalArgumentException e) {
                    return LuaValue.valueOf("Invalid BlockFace: " + faceName.tojstring());
                }
            }
        });

        t.set("isBlockFacePowered", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue faceName) {
                if (!faceName.isstring()) {
                    return LuaValue.valueOf("Usage: isBlockFacePowered(faceName)");
                }

                try {
                    BlockFace face = BlockFace.valueOf(faceName.tojstring().toUpperCase());
                    return LuaValue.valueOf(block.isBlockFacePowered(face));
                } catch (IllegalArgumentException e) {
                    return LuaValue.valueOf("Invalid BlockFace: " + faceName.tojstring());
                }
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaBlock");

        LuaDocRegistry.addFunction("LuaBlock", "getPosition",
                "Returns the block's position as a Vector3.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Block position vector")));

        LuaDocRegistry.addFunction("LuaBlock", "getType", "Gets the block's material name.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "Block material name")));

        LuaDocRegistry.addFunction("LuaBlock", "getTypeId", "Gets the block's legacy type ID.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Type ID")));

        LuaDocRegistry.addFunction("LuaBlock", "setType", "Sets the block type by name.",
                Arrays.asList(new LuaDocRegistry.Param("materialName", "string")),
                null);

        LuaDocRegistry.addFunction("LuaBlock", "setTypeId", "Sets the block type using a numeric ID.",
                Arrays.asList(new LuaDocRegistry.Param("id", "number")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the block type was set successfully")));

        LuaDocRegistry.addFunction("LuaBlock", "setData", "Sets the block's data byte.",
                Arrays.asList(new LuaDocRegistry.Param("byte", "number")),
                null);

        LuaDocRegistry.addFunction("LuaBlock", "getX", "Gets the block's X coordinate.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "X position")));
        LuaDocRegistry.addFunction("LuaBlock", "getY", "Gets the block's Y coordinate.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Y position")));
        LuaDocRegistry.addFunction("LuaBlock", "getZ", "Gets the block's Z coordinate.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Z position")));

        LuaDocRegistry.addFunction("LuaBlock", "isEmpty", "Returns whether the block is considered air or empty.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if empty")));

        LuaDocRegistry.addFunction("LuaBlock", "isLiquid", "Returns whether the block is a liquid.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if liquid")));

        LuaDocRegistry.addFunction("LuaBlock", "getData", "Gets the block's data byte.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Data byte")));

        LuaDocRegistry.addFunction("LuaBlock", "getLightLevel", "Returns the block's current light level.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Light level (0-15)")));

        LuaDocRegistry.addFunction("LuaBlock", "getRelative", "Gets the relative block in a given direction.",
                Arrays.asList(new LuaDocRegistry.Param("faceName", "string")),
                Arrays.asList(new LuaDocRegistry.Return("LuaBlock", "The block relative to this one")));

        LuaDocRegistry.addFunction("LuaBlock", "isBlockFacePowered",
                "Checks if a specific face of the block is powered.",
                Arrays.asList(new LuaDocRegistry.Param("faceName", "string")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if powered")));
    }
}
