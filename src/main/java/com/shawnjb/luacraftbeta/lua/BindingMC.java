package com.shawnjb.luacraftbeta.lua;

import com.shawnjb.luacraftbeta.LuaManager;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Return;
import com.shawnjb.luacraftbeta.lua.api.LuaPlayer;
import com.shawnjb.luacraftbeta.lua.api.LuaEntity;
import com.shawnjb.luacraftbeta.lua.api.LuaWorld;
import com.shawnjb.luacraftbeta.lua.api.LuaMaterial;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import java.util.*;

public class BindingMC {
    private static JavaPlugin plugin;
    private static LuaManager luaManager;

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

    public static void init(JavaPlugin pluginInstance, LuaManager manager) {
        plugin = pluginInstance;
        luaManager = manager;
    }

    private static final CommandSender LUA_COMMAND_SENDER = new CommandSender() {
        @Override
        public void sendMessage(String message) {
            Bukkit.getServer().getLogger().info("[LuaCommand] " + message);
        }
    
        @Override
        public Server getServer() {
            return Bukkit.getServer();
        }
    
        @Override
        public String getName() {
            return "LuaScript";
        }
    
        @Override
        public boolean isPermissionSet(String name) {
            return true;
        }
    
        @Override
        public boolean isPermissionSet(org.bukkit.permissions.Permission perm) {
            return true;
        }
    
        @Override
        public boolean hasPermission(String name) {
            return true;
        }
    
        @Override
        public boolean hasPermission(org.bukkit.permissions.Permission perm) {
            return true;
        }
    
        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value) {
            return null;
        }
    
        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin) {
            return null;
        }
    
        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value, int ticks) {
            return null;
        }
    
        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, int ticks) {
            return null;
        }
    
        @Override
        public void removeAttachment(org.bukkit.permissions.PermissionAttachment attachment) {}
    
        @Override
        public void recalculatePermissions() {}
    
        @Override
        public Set<org.bukkit.permissions.PermissionAttachmentInfo> getEffectivePermissions() {
            return new HashSet<>();
        }
    
        @Override
        public boolean isOp() {
            return true;
        }
    
        @Override
        public void setOp(boolean value) {}
    };    

    public static void register(LuaValue globals) {
        LuaTable mc = globals.get("mc").isnil() ? new LuaTable() : globals.get("mc").checktable();

        mc.set("getWorld", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue worldName) {
                if (!worldName.isstring()) {
                    return LuaValue.valueOf("Error: getWorld expects a string.");
                }

                World world = Bukkit.getWorld(worldName.tojstring());
                if (world == null) {
                    return LuaValue.valueOf("Error: world not found.");
                }

                return new LuaWorld(world).toLuaTable();
            }
        });

        mc.set("getVersion", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                PluginDescriptionFile pdf = plugin.getDescription();
                return LuaValue.valueOf(pdf.getVersion());
            }
        });

        mc.set("getLuaJVersion", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf("luaj-jse-3.0.2");
            }
        });

        mc.set("getLoadedScriptCount", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(luaManager.getLoadedScriptCount());
            }
        });        

        mc.set("summon", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 3 || !args.arg(1).isstring()) {
                    return LuaValue.valueOf("Usage: summon(entityName, world, positionOrPlayerName)");
                }

                String entityName = args.arg(1).tojstring().toLowerCase();
                LuaValue worldArg = args.arg(2);
                LuaValue targetArg = args.arg(3);

                Class<? extends Entity> entityClass = ENTITY_TYPES.get(entityName);
                if (entityClass == null) {
                    return LuaValue.valueOf("Error: unknown entity type '" + entityName + "'");
                }

                World world = null;
                if (worldArg.isstring()) {
                    String worldName = worldArg.tojstring().toLowerCase();
                    switch (worldName) {
                        case "overworld":
                            world = Bukkit.getWorld("world");
                            break;
                        case "nether":
                            world = Bukkit.getWorld("world_nether");
                            break;
                        default:
                            world = null;
                            break;
                    }
                } else if (worldArg.istable()) {
                    LuaValue getNameMethod = worldArg.get("getName");
                    if (getNameMethod.isfunction()) {
                        try {
                            String name = getNameMethod.call(worldArg).tojstring();
                            world = Bukkit.getWorld(name);
                        } catch (Exception e) {
                            return LuaValue.valueOf("Error: failed to resolve world from LuaWorld.");
                        }
                    }
                }

                if (world == null) {
                    return LuaValue.valueOf("Error: world could not be resolved.");
                }

                Location loc = null;
                if (targetArg.istable()) {
                    LuaTable t = targetArg.checktable();
                    if (t.get("x").isnumber() && t.get("y").isnumber() && t.get("z").isnumber()) {
                        double x = t.get("x").todouble();
                        double y = t.get("y").todouble();
                        double z = t.get("z").todouble();
                        loc = new Location(world, x, y, z);
                    } else {
                        return LuaValue.valueOf("Error: position table must have numeric x, y, z fields.");
                    }
                } else if (targetArg.isstring()) {
                    Player player = Bukkit.getPlayer(targetArg.tojstring());
                    if (player == null || !player.isOnline()) {
                        return LuaValue.valueOf("Error: player not found or offline.");
                    }
                    loc = player.getLocation();
                }

                if (loc == null) {
                    return LuaValue.valueOf("Error: invalid target location.");
                }

                try {
                    Entity spawned = world.spawn(loc, entityClass);
                    return new LuaEntity(spawned).toLuaTable();
                } catch (Exception ex) {
                    return LuaValue.valueOf("Error: failed to summon entity: " + ex.getMessage());
                }
            }
        });

        mc.set("getAllEntities", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue worldArg) {
                if (!worldArg.istable()) {
                    return LuaValue.valueOf("Error: getAllEntities expects a world table.");
                }

                String worldName = worldArg.get("name").tojstring();
                World world = Bukkit.getWorld(worldName);
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

        mc.set("getEntitiesByType", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue worldArg, LuaValue entityTypeArg) {
                if (!worldArg.istable() || !entityTypeArg.isstring()) {
                    return LuaValue.valueOf(
                            "Error: getEntitiesByType(world, entityType) expects a world table and an entity type string.");
                }

                String worldName = worldArg.get("name").tojstring();
                World world = Bukkit.getWorld(worldName);
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

        mc.set("getPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String name = arg.tojstring();
                Player player = Bukkit.getPlayer(name);
                if (player != null && player.isOnline()) {
                    return new LuaPlayer(player).toLuaTable();
                } else {
                    LuaTable error = new LuaTable();
                    error.set("error", "Player not found.");
                    return error;
                }
            }
        });

        mc.set("getOnlinePlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable playersTable = new LuaTable();
                int index = 1;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playersTable.set(index++, new LuaPlayer(p).toLuaTable());
                }
                return playersTable;
            }
        });

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

        mc.set("broadcast", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Bukkit.broadcastMessage(arg.tojstring());
                return NIL;
            }
        });

        mc.set("sendMessage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue playerName, LuaValue message) {
                Player player = Bukkit.getPlayer(playerName.tojstring());
                if (player != null && player.isOnline()) {
                    player.sendMessage(message.tojstring());
                }
                return NIL;
            }
        });

        mc.set("runCommand", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() == 0 || !args.arg(1).isstring()) {
                    return LuaValue.valueOf("Usage: runCommand(command, [playerName])");
                }
        
                String command = args.arg(1).tojstring();
                CommandSender sender;
        
                if (args.narg() >= 2 && args.arg(2).isstring()) {
                    String playerName = args.arg(2).tojstring();
                    Player player = Bukkit.getPlayer(playerName);
                    if (player == null || !player.isOnline()) {
                        return LuaValue.valueOf("Error: player not found or offline.");
                    }
                    sender = player;
                } else {
                    sender = LUA_COMMAND_SENDER;
                }
        
                boolean success = Bukkit.getServer().dispatchCommand(sender, command);
                return LuaValue.valueOf(success);
            }
        });        

        globals.set("mc", mc);
    }

    public static void registerDocs() {
        LuaDocRegistry.addGlobalClass("mc");

        LuaDocRegistry.addFunction("mc", "getWorld", "Returns a world by its name.",
                Arrays.asList(new Param("worldName", "string")),
                Arrays.asList(new Return("LuaWorld", "LuaWorld table")));

        LuaDocRegistry.addFunction("mc", "getVersion", "Returns the LuaCraftBeta plugin version.",
                null, Arrays.asList(new Return("string", "Version string")));

        LuaDocRegistry.addFunction("mc", "getLuaJVersion", "Returns the LuaJ interpreter version.",
                null, Arrays.asList(new Return("string", "LuaJ version")));

        LuaDocRegistry.addFunction("mc", "getLoadedScriptCount", "Returns the number of loaded Lua scripts.",
                null, Arrays.asList(new Return("number", "Loaded script count")));

        LuaDocRegistry.addFunction("mc", "summon",
                "Spawns an entity in the given world at a position or a player's location.",
                Arrays.asList(
                        new Param("entityName", "string"),
                        new Param("world", "LuaWorld|string"),
                        new Param("target", "Vector3|string")),
                Arrays.asList(new Return("table", "The spawned LuaEntity table")));

        LuaDocRegistry.addFunction("mc", "getAllEntities", "Returns all entities in the given world.",
                Arrays.asList(new Param("world", "table")),
                Arrays.asList(new Return("table", "Array of LuaEntity tables")));

        LuaDocRegistry.addFunction("mc", "getEntitiesByType", "Returns all entities of a given type from a world.",
                Arrays.asList(
                        new Param("world", "table"),
                        new Param("entityType", "string")),
                Arrays.asList(new Return("table", "Array of LuaEntity tables matching the given type")));

        LuaDocRegistry.addFunction(
                "mc",
                "getPlayer",
                "Gets a player by name if they are online. Returns a LuaPlayer or error table.",
                Arrays.asList(new Param("name", "string")),
                Arrays.asList(new Return("LuaPlayer?", "LuaPlayer object or error table if not found")));

        LuaDocRegistry.addFunction(
                "mc",
                "getOnlinePlayers",
                "Returns a list of online players as LuaPlayer tables.",
                Arrays.asList(),
                Arrays.asList(new Return("LuaPlayer[]", "Array of LuaPlayer objects")));

        LuaDocRegistry.addFunction(
                "mc",
                "getMaterial",
                "Returns a material by its numeric ID.",
                Arrays.asList(new Param("id", "integer")),
                Arrays.asList(new Return("table", "LuaMaterial table if found, or an error message string")));

        LuaDocRegistry.addFunction(
                "mc",
                "broadcast",
                "Broadcasts a message to all players on the server.",
                Arrays.asList(new Param("message", "string")),
                null);

        LuaDocRegistry.addFunction(
                "mc",
                "sendMessage",
                "Sends a private message to the specified player.",
                Arrays.asList(
                        new Param("playerName", "string"),
                        new Param("message", "string")),
                null);

        LuaDocRegistry.addFunction(
                "mc",
                "runCommand",
                "Executes a command as the server or a specific player.",
                Arrays.asList(
                        new Param("command", "string"),
                        new Param("playerName", "string? (optional)")),
                Arrays.asList(new Return("boolean", "True if the command executed successfully, false otherwise")));
    }
}
