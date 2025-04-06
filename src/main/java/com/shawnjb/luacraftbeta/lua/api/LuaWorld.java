package com.shawnjb.luacraftbeta.lua.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

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

        t.set("strikeLightning", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() >= 4 &&
                        args.arg(2).isnumber() &&
                        args.arg(3).isnumber() &&
                        args.arg(4).isnumber()) {

                    Location loc = new Location(world,
                            args.arg(2).todouble(),
                            args.arg(3).todouble(),
                            args.arg(4).todouble());

                    world.strikeLightning(loc);
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: strikeLightning(x, y, z) expects numbers.");
            }
        });

        t.set("createExplosion", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {

                if (args.narg() == 3 && args.arg(2).istable() && args.arg(3).isnumber()) {
                    LuaTable vec = args.arg(2).checktable();
                    double x = vec.get("x").todouble();
                    double y = vec.get("y").todouble();
                    double z = vec.get("z").todouble();
                    float power = (float) args.arg(3).todouble();

                    world.createExplosion(x, y, z, power);
                    return LuaValue.NIL;
                }

                if (args.narg() >= 5 &&
                        args.arg(2).isnumber() &&
                        args.arg(3).isnumber() &&
                        args.arg(4).isnumber() &&
                        args.arg(5).isnumber()) {

                    double x = args.arg(2).todouble();
                    double y = args.arg(3).todouble();
                    double z = args.arg(4).todouble();
                    float power = (float) args.arg(5).todouble();

                    world.createExplosion(x, y, z, power);
                    return LuaValue.NIL;
                }

                return LuaValue.valueOf("Usage: createExplosion(x, y, z, power) or createExplosion(Vector3, power)");
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
                if (args.narg() < 5 || !args.arg(2).isnumber() || !args.arg(3).isnumber() || !args.arg(4).isnumber()
                        || !args.arg(5).isstring()) {
                    return LuaValue.valueOf("Usage: setBlock(x, y, z, blockType [, data])");
                }

                int x = args.arg(2).toint();
                int y = args.arg(3).toint();
                int z = args.arg(4).toint();
                String blockType = args.arg(5).tojstring().toUpperCase();

                Material mat = Material.getMaterial(blockType);
                if (mat == null) {
                    return LuaValue.valueOf("Invalid material: " + blockType);
                }

                Block block = world.getBlockAt(x, y, z);
                block.setType(mat);

                if (args.narg() >= 6 && args.arg(6).isnumber()) {
                    byte data = (byte) args.arg(6).toint();
                    block.setData(data);
                }

                return LuaValue.NIL;
            }
        });

        t.set("getBlockAt", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() >= 4 &&
                        args.arg(2).isnumber() &&
                        args.arg(3).isnumber() &&
                        args.arg(4).isnumber()) {

                    int x = args.arg(2).toint();
                    int y = args.arg(3).toint();
                    int z = args.arg(4).toint();

                    Block block = world.getBlockAt(x, y, z);
                    return new LuaBlock(block).toLuaTable();
                }
                return LuaValue.valueOf("Usage: getBlockAt(x, y, z)");
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaWorld");

        LuaDocRegistry.addFunction("LuaWorld", "getName", "Returns the name of the world.", List.of(),
                List.of(new LuaDocRegistry.Return("string", "")));

        LuaDocRegistry.addFunction("LuaWorld", "getTime", "Gets the current time in the world.", List.of(),
                List.of(new LuaDocRegistry.Return("number", "")));

        LuaDocRegistry.addFunction("LuaWorld", "setTime", "Sets the current time in the world.", List.of(
                new LuaDocRegistry.Param("time", "number")), null);

        LuaDocRegistry.addFunction("LuaWorld", "hasStorm",
                "Returns whether the world is currently experiencing a storm.", List.of(),
                List.of(new LuaDocRegistry.Return("boolean", "")));

        LuaDocRegistry.addFunction("LuaWorld", "setStorm", "Enables or disables stormy weather in the world.", List.of(
                new LuaDocRegistry.Param("value", "boolean")), null);

        LuaDocRegistry.addFunction("LuaWorld", "strikeLightning", "Strikes lightning at the given coordinates.",
                List.of(
                        new LuaDocRegistry.Param("x", "number"),
                        new LuaDocRegistry.Param("y", "number"),
                        new LuaDocRegistry.Param("z", "number")),
                null);

        LuaDocRegistry.addFunction("LuaWorld", "createExplosion",
                "Creates an explosion at a location. Can accept x, y, z, power or (Vector3, power).", List.of(
                        new LuaDocRegistry.Param("xOrVector", "number|table"),
                        new LuaDocRegistry.Param("yOrPower", "number"),
                        new LuaDocRegistry.Param("z", "number") // optional depending on overload (yes)
                ), null);

        LuaDocRegistry.addFunction("LuaWorld", "getSeed", "Returns the seed used to generate the world.", List.of(),
                List.of(new LuaDocRegistry.Return("number", "")));

        LuaDocRegistry.addFunction("LuaWorld", "getPlayers", "Returns a list of players in this world.", List.of(),
                List.of(new LuaDocRegistry.Return("table", "Array of LuaPlayer objects")));

        LuaDocRegistry.addFunction("LuaWorld", "getEntities", "Returns all entities in the world.", List.of(),
                List.of(new LuaDocRegistry.Return("table", "Array of LuaEntity objects")));

        LuaDocRegistry.addFunction("LuaWorld", "setBlock",
                "Sets a block at a given location to the specified type and optional data.", List.of(
                        new LuaDocRegistry.Param("x", "number"),
                        new LuaDocRegistry.Param("y", "number"),
                        new LuaDocRegistry.Param("z", "number"),
                        new LuaDocRegistry.Param("blockType", "string"),
                        new LuaDocRegistry.Param("data", "number?")),
                null);

        LuaDocRegistry.addFunction("LuaWorld", "getBlockAt", "Returns the block at the given location.", List.of(
                new LuaDocRegistry.Param("x", "number"),
                new LuaDocRegistry.Param("y", "number"),
                new LuaDocRegistry.Param("z", "number")),
                List.of(new LuaDocRegistry.Return("table", "LuaBlock representing the block")));
    }
}