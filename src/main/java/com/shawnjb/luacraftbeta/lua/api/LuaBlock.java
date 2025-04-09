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

    private boolean isBlockSolid(Block block) {
        int blockId = block.getTypeId();

        // Define solid block IDs based on Beta 1.7.3 data
        switch (blockId) {
            case 1: // Stone
            case 2: // Grass Block
            case 3: // Dirt
            case 4: // Cobblestone
            case 5: // Wooden Plank
            case 7: // Bedrock
            case 12: // Sand
            case 13: // Gravel
            case 14: // Gold Ore
            case 15: // Iron Ore
            case 16: // Coal Ore
            case 17: // Log
            case 19: // Sponge
            case 20: // Glass
            case 21: // Lapis Lazuli Ore
            case 22: // Lapis Lazuli Block
            case 23: // Dispenser
            case 24: // Sandstone
            case 25: // Note Block
            case 35: // Wool
            case 41: // Gold Block
            case 42: // Iron Block
            case 43: // Double Stone Slab
            case 44: // Stone Slab
            case 45: // Brick Block
            case 46: // TNT
            case 47: // Bookshelf
            case 48: // Moss Stone
            case 49: // Obsidian
            case 56: // Diamond Ore
            case 57: // Diamond Block
            case 58: // Crafting Table
            case 60: // Farmland
            case 61: // Furnace
            case 62: // Burning Furnace
            case 73: // Redstone Ore
            case 74: // Glowing Redstone Ore
            case 79: // Ice
            case 80: // Snow Block
            case 82: // Clay
            case 84: // Jukebox
            case 86: // Pumpkin
            case 87: // Netherrack
            case 88: // Soul Sand
            case 89: // Glowstone
            case 91: // Jack o'Lantern
            case 97: // Monster Egg
            case 98: // Stone Brick
                return true;
            default:
                return false;
        }
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
                    block.setTypeId(id.toint());
                    return LuaValue.NIL;
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

        t.set("isSolid", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(isBlockSolid(block));
            }
        });

        t.set("getId", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(block.getTypeId());
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaBlock");

        LuaDocRegistry.addFunction("LuaBlock", "getType", "Returns the block's material name in lowercase.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("string", "Block material name")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getTypeId", "Returns the block's type ID (legacy numeric ID).",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Legacy block ID")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "setType", "Sets the block type using a material name.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaBlock"),
                        new LuaDocRegistry.Param("material", "string")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaBlock", "setTypeId", "Sets the block's legacy numeric ID.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaBlock"),
                        new LuaDocRegistry.Param("id", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaBlock", "setData", "Sets the block's legacy data value (0-15).",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaBlock"),
                        new LuaDocRegistry.Param("data", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getData", "Gets the block's legacy data value.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Data value")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getPosition", "Returns the block's position as a Vector3.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Block position")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getX", "Gets the block's X coordinate.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("number", "X coordinate")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getY", "Gets the block's Y coordinate.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Y coordinate")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getZ", "Gets the block's Z coordinate.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Z coordinate")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "isEmpty", "Returns true if the block is air.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if block is air")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "isLiquid", "Returns true if the block is a liquid.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if block is water or lava")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getLightLevel", "Returns the light level at this block.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Light level from 0 to 15")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getRelative",
                "Gets the block relative to this one in a given direction.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaBlock"),
                        new LuaDocRegistry.Param("face", "string")),
                Arrays.asList(new LuaDocRegistry.Return("LuaBlock", "Block in the given direction")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "isBlockFacePowered",
                "Checks if the specified face of this block is powered.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaBlock"),
                        new LuaDocRegistry.Param("face", "string")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if that face is powered")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "isSolid",
                "Returns true if the block is solid (can be stood on or blocks movement).",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if block is solid")),
                true);

        LuaDocRegistry.addFunction("LuaBlock", "getId", "Returns the block's legacy numeric ID (alias of getTypeId).",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaBlock")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Legacy block ID")),
                true);
    }
}
