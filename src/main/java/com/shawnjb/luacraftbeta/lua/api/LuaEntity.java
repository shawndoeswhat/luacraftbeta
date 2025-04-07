package com.shawnjb.luacraftbeta.lua.api;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import net.minecraft.server.WorldServer;
import net.minecraft.server.Chunk;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

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

        t.set("teleport", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue vecValue) {
                if (!vecValue.istable()) {
                    return LuaValue.error("teleport(Vector3) expects a Vector3 table");
                }

                try {
                    Vector vec = LuaVector3.fromTable(vecValue.checktable());
                    Location loc = entity.getLocation();
                    loc.setX(vec.getX());
                    loc.setY(vec.getY());
                    loc.setZ(vec.getZ());
                    entity.teleport(loc);
                    return LuaValue.NIL;
                } catch (LuaError e) {
                    return LuaValue.error("teleport(Vector3) expects numeric x, y, z fields");
                }
            }
        });

        t.set("getLocation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                Location loc = entity.getLocation();
                return new LuaVector3(loc.getX(), loc.getY(), loc.getZ()).toLuaTable();
            }
        });

        t.set("getEntitiesInChunk", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                try {

                    Location loc = entity.getLocation();
                    int chunkX = loc.getBlockX() >> 4;
                    int chunkZ = loc.getBlockZ() >> 4;

                    org.bukkit.World bukkitWorld = entity.getWorld();
                    org.bukkit.craftbukkit.CraftWorld craftWorld = (org.bukkit.craftbukkit.CraftWorld) bukkitWorld;
                    WorldServer handle = craftWorld.getHandle();

                    Chunk chunk = handle.getChunkAt(chunkX, chunkZ);

                    LuaTable table = new LuaTable();
                    int i = 1;

                    for (int y = 0; y < chunk.entitySlices.length; y++) {
                        List<?> entityList = chunk.entitySlices[y];
                        if (entityList == null)
                            continue;

                        for (Object obj : entityList) {
                            if (obj instanceof net.minecraft.server.Entity) {
                                net.minecraft.server.Entity nmsEntity = (net.minecraft.server.Entity) obj;
                                Entity bukkitEntity = nmsEntity.getBukkitEntity();

                                if (bukkitEntity != null && !bukkitEntity.isDead()) {
                                    table.set(i++, new LuaEntity(bukkitEntity).toLuaTable());
                                }
                            }
                        }
                    }

                    return table;

                } catch (Exception ex) {
                    return LuaValue.error("getEntitiesInChunk() failed: " + ex.getMessage());
                }
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

        LuaDocRegistry.addFunction("LuaEntity", "teleport",
                "Teleports the entity using a Vector3 table.",
                Arrays.asList(new LuaDocRegistry.Param("position", "Vector3")),
                null);

        LuaDocRegistry.addFunction("LuaEntity", "getLocation", "Returns the entity's current location.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Entity position")));

        LuaDocRegistry.addFunction("LuaEntity", "getEntitiesInChunk",
                "Returns all entities in the chunk this entity is currently in.",
                Arrays.asList(),
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
