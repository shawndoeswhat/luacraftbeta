package com.shawnjb.luacraftbeta.lua.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

import java.util.Arrays;
import java.util.List;

public class LuaEntity {
    private final Entity entity;

    public LuaEntity(Entity entity) {
        this.entity = entity;
    }

    public LuaTable toLuaTable() {
        LuaTable t = new LuaTable();

        t.set("getType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                String simpleName = entity.getClass().getSimpleName();
                return LuaValue.valueOf(simpleName.toLowerCase());
            }
        });

        t.set("getId", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(entity.getEntityId());
            }
        });

        t.set("isDead", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(entity.isDead());
            }
        });

        t.set("setFireTicks", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue ticks) {
                if (ticks.isnumber()) {
                    entity.setFireTicks(ticks.toint());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: setFireTicks expects a number.");
            }
        });

        t.set("teleport", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                Location loc = entity.getLocation();

                if (args.narg() == 2 && args.arg(2).istable()) {
                    LuaTable vec = args.arg(2).checktable();
                    loc.setX(vec.get("x").todouble());
                    loc.setY(vec.get("y").todouble());
                    loc.setZ(vec.get("z").todouble());

                    entity.teleport(loc);
                    return LuaValue.NIL;
                }

                if (args.narg() >= 4 &&
                        args.arg(2).isnumber() &&
                        args.arg(3).isnumber() &&
                        args.arg(4).isnumber()) {

                    loc.setX(args.arg(2).todouble());
                    loc.setY(args.arg(3).todouble());
                    loc.setZ(args.arg(4).todouble());

                    entity.teleport(loc);
                    return LuaValue.NIL;
                }

                return LuaValue.valueOf("Usage: teleport(x, y, z) or teleport(Vector3)");
            }
        });

        t.set("getLocation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                Location loc = entity.getLocation();
                return new LuaVector3(loc.getX(), loc.getY(), loc.getZ()).toLuaTable();
            }
        });

        t.set("getNearbyEntities", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() >= 4 &&
                        args.arg(2).isnumber() &&
                        args.arg(3).isnumber() &&
                        args.arg(4).isnumber()) {

                    double dx = args.arg(2).todouble();
                    double dy = args.arg(3).todouble();
                    double dz = args.arg(4).todouble();

                    List<Entity> entities = entity.getNearbyEntities(dx, dy, dz);
                    LuaTable table = new LuaTable();
                    int i = 1;
                    for (Entity e : entities) {
                        table.set(i++, new LuaEntity(e).toLuaTable());
                    }
                    return table;
                }
                return LuaValue.valueOf("Usage: getNearbyEntities(x, y, z)");
            }
        });

        t.set("isEmpty", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(entity.isEmpty());
            }
        });

        t.set("eject", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(entity.eject());
            }
        });

        t.set("remove", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                entity.remove();
                return LuaValue.NIL;
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaEntity");

        LuaDocRegistry.addFunction("LuaEntity", "getType", "Returns the entity type as a lowercase name.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "Entity type name")));

        LuaDocRegistry.addFunction("LuaEntity", "getId", "Returns the unique entity ID.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Entity ID")));

        LuaDocRegistry.addFunction("LuaEntity", "isDead", "Returns whether the entity is dead.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if dead")));

        LuaDocRegistry.addFunction("LuaEntity", "setFireTicks", "Sets the fire ticks duration.",
                Arrays.asList(new LuaDocRegistry.Param("ticks", "number")),
                null);

        LuaDocRegistry.addFunction("LuaEntity", "teleport", "Teleports the entity to a position.",
                Arrays.asList(
                        new LuaDocRegistry.Param("x", "number"),
                        new LuaDocRegistry.Param("y", "number"),
                        new LuaDocRegistry.Param("z", "number")),
                null);
        LuaDocRegistry.addFunction("LuaEntity", "teleport", "Teleports the entity using a Vector3 table.",
                Arrays.asList(new LuaDocRegistry.Param("vec", "Vector3")),
                null);

        LuaDocRegistry.addFunction("LuaEntity", "getLocation", "Returns the entity's current location.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Entity position")));

        LuaDocRegistry.addFunction("LuaEntity", "getNearbyEntities", "Returns a list of entities nearby the entity.",
                Arrays.asList(
                        new LuaDocRegistry.Param("x", "number"),
                        new LuaDocRegistry.Param("y", "number"),
                        new LuaDocRegistry.Param("z", "number")),
                Arrays.asList(new LuaDocRegistry.Return("table", "Array of LuaEntity tables")));

        LuaDocRegistry.addFunction("LuaEntity", "isEmpty",
                "Returns whether the entity is empty (e.g. vehicle without passenger).", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if empty")));

        LuaDocRegistry.addFunction("LuaEntity", "eject", "Ejects any passenger from the entity.", Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if something was ejected")));

        LuaDocRegistry.addFunction("LuaEntity", "remove", "Removes the entity from the world.", Arrays.asList(),
                null);
    }
}
