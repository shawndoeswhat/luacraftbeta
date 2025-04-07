package com.shawnjb.luacraftbeta.lua.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

import java.util.Arrays;
import java.util.List;

public class LuaWorld {
    private final World world;

    public LuaWorld(World world) {
        this.world = world;
    }

    public LuaTable toLuaTable() {
        LuaTable t = new LuaTable();

        t.set("getName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(world.getName());
            }
        });

        t.set("getTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(world.getTime());
            }
        });

        t.set("setTime", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue time) {
                if (time.isnumber()) {
                    world.setTime(time.tolong());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: setTime expects a number.");
            }
        });

        t.set("hasStorm", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(world.hasStorm());
            }
        });

        t.set("setStorm", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue value) {
                if (value.isboolean()) {
                    world.setStorm(value.toboolean());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: setStorm expects a boolean.");
            }
        });

        t.set("strikeLightning", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue vecValue) {
                if (!vecValue.istable()) {
                    return LuaValue.error("strikeLightning(Vector3) expects a Vector3 table");
                }

                try {
                    Vector vec = LuaVector3.fromTable(vecValue.checktable());
                    world.strikeLightning(new Location(world, vec.getX(), vec.getY(), vec.getZ()));
                    return LuaValue.NIL;
                } catch (LuaError e) {
                    return LuaValue.error("strikeLightning(Vector3) expects numeric x, y, z fields");
                }
            }
        });

        t.set("createExplosion", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() == 3 && args.arg(2).istable() && args.arg(3).isnumber()) {
                    try {
                        Vector vec = LuaVector3.fromTable(args.arg(2).checktable());
                        float power = (float) args.arg(3).todouble();
                        world.createExplosion(vec.getX(), vec.getY(), vec.getZ(), power);
                        return LuaValue.NIL;
                    } catch (LuaError e) {
                        return LuaValue.error("createExplosion(Vector3, power) expects valid numeric x/y/z");
                    }
                }
                return LuaValue.error("Usage: createExplosion(Vector3, power)");
            }
        });

        t.set("getSeed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(world.getSeed());
            }
        });

        t.set("getPlayers", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                List<Player> players = world.getPlayers();
                LuaTable table = new LuaTable();
                int i = 1;
                for (Player p : players) {
                    table.set(i++, new LuaPlayer(p).toLuaTable());
                }
                return table;
            }
        });

        t.set("getEntities", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                List<Entity> entities = world.getEntities();
                LuaTable table = new LuaTable();
                int i = 1;
                for (Entity e : entities) {
                    table.set(i++, new LuaEntity(e).toLuaTable());
                }
                return table;
            }
        });

        t.set("setBlock", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 4 || !args.arg(2).istable() || !args.arg(3).isstring()) {
                    return LuaValue.error("Usage: setBlock(Vector3, blockType [, data])");
                }

                try {
                    Vector vec = LuaVector3.fromTable(args.arg(2).checktable());
                    String blockType = args.arg(3).tojstring().toUpperCase();
                    Material mat = Material.getMaterial(blockType);
                    if (mat == null)
                        return LuaValue.error("Invalid material: " + blockType);

                    Block block = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
                    block.setType(mat);

                    if (args.narg() >= 4 && args.arg(4).isnumber()) {
                        block.setData((byte) args.arg(4).toint());
                    }

                    return LuaValue.NIL;
                } catch (LuaError e) {
                    return LuaValue.error("setBlock(Vector3, blockType [, data]) requires valid x/y/z");
                }
            }
        });

        t.set("getBlockAt", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue vecValue) {
                if (!vecValue.istable()) {
                    return LuaValue.error("getBlockAt(Vector3) expects a Vector3 table");
                }

                try {
                    Vector vec = LuaVector3.fromTable(vecValue.checktable());
                    Block block = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
                    return new LuaBlock(block).toLuaTable();
                } catch (LuaError e) {
                    return LuaValue.error("getBlockAt(Vector3) expects numeric x, y, z fields");
                }
            }
        });

        t.set("isRaining", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(world.hasStorm() && world.getWeatherDuration() > 0);
            }
        });
        
        t.set("getDimension", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                String name = world.getName().toLowerCase();
                if (name.contains("nether")) {
                    return LuaValue.valueOf("nether");
                } else if (name.contains("world") || name.contains("overworld")) {
                    return LuaValue.valueOf("overworld");
                }
                return LuaValue.valueOf("unknown");
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaWorld");

        LuaDocRegistry.addFunction("LuaWorld", "getName", "Returns the name of the world.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("string", "The name of the world")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "getTime", "Gets the current time in the world.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Current world time")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "setTime", "Sets the current time in the world.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaWorld"),
                        new LuaDocRegistry.Param("time", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaWorld", "hasStorm",
                "Returns whether the world is currently experiencing a storm.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if storming")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "setStorm", "Enables or disables stormy weather in the world.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaWorld"),
                        new LuaDocRegistry.Param("value", "boolean")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaWorld", "strikeLightning", "Strikes lightning at the given position.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaWorld"),
                        new LuaDocRegistry.Param("position", "Vector3")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaWorld", "createExplosion", "Creates an explosion at a location.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaWorld"),
                        new LuaDocRegistry.Param("position", "Vector3"),
                        new LuaDocRegistry.Param("power", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaWorld", "getSeed", "Returns the seed used to generate the world.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("number", "World generation seed")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "getPlayers", "Returns a list of players in this world.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("table", "Array of LuaPlayer objects")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "getEntities", "Returns all entities in the world.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("table", "Array of LuaEntity objects")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "setBlock",
                "Sets a block at a given location to the specified type and optional data.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaWorld"),
                        new LuaDocRegistry.Param("position", "Vector3"),
                        new LuaDocRegistry.Param("blockType", "string"),
                        new LuaDocRegistry.Param("data", "number?")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaWorld", "getBlockAt",
                "Returns the block at the given location. May return nil if the position is invalid or the block could not be resolved.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaWorld"),
                        new LuaDocRegistry.Param("position", "Vector3")),
                Arrays.asList(
                        new LuaDocRegistry.Return("LuaBlock?", "LuaBlock representing the block, or nil if invalid")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "isRaining",
                "Returns whether it is currently raining in the world.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if it is raining")),
                true);

        LuaDocRegistry.addFunction("LuaWorld", "getDimension",
                "Returns the dimension name the world represents.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaWorld")),
                Arrays.asList(new LuaDocRegistry.Return("string", "One of: 'overworld', 'nether', or 'unknown'")),
                true);
    }
}