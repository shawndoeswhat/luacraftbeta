package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Return;
import com.shawnjb.luacraftbeta.lua.api.LuaEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class BindingEntities {
    private static final Map<String, Class<? extends Entity>> ENTITY_TYPES = new HashMap<>();

    static {
        ENTITY_TYPES.put("pig", Pig.class);
        ENTITY_TYPES.put("cow", Cow.class);
        ENTITY_TYPES.put("chicken", Chicken.class);
        ENTITY_TYPES.put("sheep", Sheep.class);
        ENTITY_TYPES.put("squid", Squid.class);
        ENTITY_TYPES.put("wolf", Wolf.class);

        ENTITY_TYPES.put("zombie", Zombie.class);
        ENTITY_TYPES.put("skeleton", Skeleton.class);
        ENTITY_TYPES.put("creeper", Creeper.class);
        ENTITY_TYPES.put("spider", Spider.class);
        ENTITY_TYPES.put("slime", Slime.class);
        ENTITY_TYPES.put("ghast", Ghast.class);

        ENTITY_TYPES.put("player", Player.class);
        ENTITY_TYPES.put("giant", Giant.class);
        ENTITY_TYPES.put("pigzombie", PigZombie.class);
    }

    public static void register(LuaValue globals) {
        LuaTable mc = globals.get("mc").isnil() ? new LuaTable() : globals.get("mc").checktable();

        mc.set("summon", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue entityArg, LuaValue targetArg) {
                if (!entityArg.isstring()) {
                    return LuaValue.valueOf("Error: summon(entityName, position/playerName)");
                }

                String entityName = entityArg.tojstring().toLowerCase();
                Class<? extends Entity> entityClass = ENTITY_TYPES.get(entityName);
                if (entityClass == null) {
                    return LuaValue.valueOf("Error: unknown entity type '" + entityName + "'");
                }

                Location loc = null;

                if (targetArg.istable()) {
                    LuaTable t = targetArg.checktable();

                    if (t.get("x").isnumber() && t.get("y").isnumber() && t.get("z").isnumber()) {
                        double x = t.get("x").todouble();
                        double y = t.get("y").todouble();
                        double z = t.get("z").todouble();
                        String worldName = t.get("world").optjstring(null);

                        World world = (worldName != null)
                                ? Bukkit.getWorld(worldName)
                                : Bukkit.getWorlds().get(0);

                        if (world == null) {
                            return LuaValue.valueOf("Error: world not found: " + worldName);
                        }

                        loc = new Location(world, x, y, z);
                    } else {
                        return LuaValue.valueOf("Error: Vector3 table must have numeric x, y, z fields.");
                    }

                } else if (targetArg.isstring()) {
                    Player player = Bukkit.getPlayer(targetArg.tojstring());
                    if (player == null || !player.isOnline()) {
                        return LuaValue.valueOf("Error: player not found or offline.");
                    }
                    loc = player.getLocation();
                } else {
                    return LuaValue.valueOf("Error: second argument must be a table or player name.");
                }

                try {
                    Entity spawned = loc.getWorld().spawn(loc, entityClass);
                    LuaEntity luaEntity = new LuaEntity(spawned);
                    return luaEntity.toLuaTable();
                } catch (Exception ex) {
                    return LuaValue.valueOf("Error: failed to summon entity: " + ex.getMessage());
                }
            }
        });

        LuaDocRegistry.addFunction(
                "mc",
                "summon",
                "Spawns an entity at a location or at a player's position.",
                List.of(
                        new Param("entityName", "string"),
                        new Param("positionOrPlayer", "Vector3|string")),
                List.of(new Return("table", "The spawned LuaEntity table")));

        mc.set("getAllEntities", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue worldArg) {
                if (!worldArg.istable()) {
                    return LuaValue.valueOf("Error: getAllEntities expects a world table.");
                }

                LuaTable worldTable = worldArg.checktable();
                World world = Bukkit.getWorld(worldTable.get("name").tojstring());
                if (world == null) {
                    return LuaValue.valueOf("Error: world not found.");
                }

                List<Entity> entities = world.getEntities();
                LuaTable entityTable = new LuaTable();
                int index = 1;
                for (Entity entity : entities) {
                    entityTable.set(index++, new LuaEntity(entity).toLuaTable());
                }

                return entityTable;
            }
        });

        LuaDocRegistry.addFunction(
                "mc",
                "getAllEntities",
                "Returns all entities in the given world.",
                List.of(new Param("world", "table")),
                List.of(new Return("table", "Array of LuaEntity tables")));

        mc.set("getEntitiesByType", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue worldArg, LuaValue entityTypeArg) {
                if (!worldArg.istable() || !entityTypeArg.isstring()) {
                    return LuaValue.valueOf(
                            "Error: getEntitiesByType(world, entityType) expects a world table and an entity type string.");
                }

                LuaTable worldTable = worldArg.checktable();
                World world = Bukkit.getWorld(worldTable.get("name").tojstring());
                if (world == null) {
                    return LuaValue.valueOf("Error: world not found.");
                }

                String entityType = entityTypeArg.tojstring().toLowerCase();
                Class<? extends Entity> entityClass = ENTITY_TYPES.get(entityType);
                if (entityClass == null) {
                    return LuaValue.valueOf("Error: unknown entity type '" + entityType + "'");
                }

                List<Entity> entities = world.getEntities();
                LuaTable entityTable = new LuaTable();
                int index = 1;
                for (Entity entity : entities) {
                    if (entity.getClass().equals(entityClass)) {
                        entityTable.set(index++, new LuaEntity(entity).toLuaTable());
                    }
                }

                return entityTable;
            }
        });

        LuaDocRegistry.addFunction(
                "mc",
                "getEntitiesByType",
                "Returns all entities of a given type from a world.",
                List.of(
                        new Param("world", "table"),
                        new Param("entityType", "string")),
                List.of(new Return("table", "Array of LuaEntity tables matching the given type")));

        globals.set("mc", mc);
    }

    public static void registerDocs() {
        LuaDocRegistry.addFunction(
                "mc",
                "summon",
                "Spawns an entity at a location or at a player's position.",
                List.of(
                        new Param("entityName", "string"),
                        new Param("positionOrPlayer", "Vector3|string")),
                List.of(new Return("table", "The spawned LuaEntity table")));

        LuaDocRegistry.addFunction(
                "mc",
                "getAllEntities",
                "Returns all entities in the given world.",
                List.of(new Param("world", "table")),
                List.of(new Return("table", "Array of LuaEntity tables")));

        LuaDocRegistry.addFunction(
                "mc",
                "getEntitiesByType",
                "Returns all entities of a given type from a world.",
                List.of(
                        new Param("world", "table"),
                        new Param("entityType", "string")),
                List.of(new Return("table", "Array of LuaEntity tables matching the given type")));
    }
}
