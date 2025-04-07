package com.shawnjb.luacraftbeta.lua.api;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

        t.set("getVelocity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                Vector velocity = entity.getVelocity();
                return new LuaVector3(velocity).toLuaTable();
            }
        });

        t.set("setVelocity", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue vecValue) {
                if (!vecValue.istable()) {
                    return LuaValue.error("setVelocity(Vector3) expects a Vector3 table");
                }

                try {
                    Vector vec = LuaVector3.fromTable(vecValue.checktable());
                    entity.setVelocity(vec);
                    return LuaValue.NIL;
                } catch (LuaError e) {
                    return LuaValue.error("setVelocity(Vector3) expects numeric x, y, z fields");
                }
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

        t.set("getName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (entity instanceof Player) {
                    return LuaValue.valueOf(((Player) entity).getName());
                }
                return LuaValue.valueOf(entity.getClass().getSimpleName().toLowerCase());
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

        t.set("getWorld", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return new LuaWorld(entity.getWorld()).toLuaTable();
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

        t.set("isOp", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (entity instanceof Player) {
                    return LuaValue.valueOf(((Player) entity).isOp());
                }
                return LuaValue.valueOf(false);
            }
        });

        t.set("getHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (entity instanceof LivingEntity) {
                    return LuaValue.valueOf(((LivingEntity) entity).getHealth());
                }
                return LuaValue.valueOf(-1);
            }
        });

        t.set("getMaxHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(20); // hardcoded for Beta
            }
        });

        t.set("isAlive", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (entity instanceof LivingEntity) {
                    return LuaValue.valueOf(((LivingEntity) entity).getHealth() > 0);
                }
                return LuaValue.FALSE;
            }
        });

        t.set("heal", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).setHealth(20);
                }
                return LuaValue.NIL;
            }
        });

        t.set("damage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue amount) {
                if (entity instanceof LivingEntity && amount.isnumber()) {
                    ((LivingEntity) entity).damage(amount.toint());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: damage(amount) expects a number and a living entity.");
            }
        });

        t.set("kill", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).setHealth(0);
                }
                return LuaValue.NIL;
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaEntity");

        LuaDocRegistry.addFunction("LuaEntity", "getType", "Returns the entity type as a lowercase name.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("string", "Entity type name")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getId", "Returns the unique entity ID.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Entity ID")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getName",
                "Returns the name of the entity. If the entity is a player, this is their username. Otherwise, it falls back to the type name.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("string", "The name or type name of the entity")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "isDead", "Returns whether the entity is dead.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if dead")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getVelocity", "Gets the entity's current velocity vector.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Velocity vector")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "setVelocity", "Sets the entity's velocity.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaEntity"),
                        new LuaDocRegistry.Param("velocity", "Vector3")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaEntity", "setFireTicks", "Sets the fire ticks duration.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaEntity"),
                        new LuaDocRegistry.Param("ticks", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaEntity", "teleport", "Teleports the entity using a Vector3 table.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaEntity"),
                        new LuaDocRegistry.Param("position", "Vector3")),
                Arrays.asList(new LuaDocRegistry.Return("nil", "Always returns nil")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getLocation", "Returns the entity's current location.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Entity position")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getEntitiesInChunk",
                "Returns all entities in the chunk this entity is currently in.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("table", "Array of LuaEntity tables")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "isEmpty",
                "Returns whether the entity is empty (e.g. vehicle without passenger).",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if empty")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getWorld", "Returns the world the entity is in.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("LuaWorld", "The world the entity belongs to")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "eject", "Ejects any passenger from the entity.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if something was ejected")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "remove", "Removes the entity from the world.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaEntity", "isOp",
                "Returns true if the entity is a player and they are an operator.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the player is op; false otherwise")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getHealth",
                "Returns the current health of the entity if it's a living entity.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Current health, or -1 if not a living entity")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "getMaxHealth",
                "Returns the maximum health value for the entity. Always 20 for now.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Max health (20)")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "isAlive",
                "Returns true if the entity is a living entity and has health greater than 0.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if alive, false otherwise")),
                true);

        LuaDocRegistry.addFunction("LuaEntity", "heal",
                "Fully restores the entity's health to 20, if it's a living entity.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaEntity", "damage",
                "Damages the entity by the given amount, if it's a living entity.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "LuaEntity"),
                        new LuaDocRegistry.Param("amount", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaEntity", "kill",
                "Instantly sets the entity's health to 0, if it's a living entity.",
                Arrays.asList(new LuaDocRegistry.Param("self", "LuaEntity")),
                null,
                true);
    }
}
