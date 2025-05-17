package com.shawndoeswhat.luacraftbeta.lua.api;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.shawndoeswhat.luacraftbeta.docs.LuaDocRegistry;
import com.shawndoeswhat.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawndoeswhat.luacraftbeta.docs.LuaDocRegistry.Return;

public class LuaPlayer {
    private final Player player;

    public LuaPlayer(Player player) {
        this.player = player;
    }

    public LuaPlayer(Entity entity) {
        if (entity instanceof Player) {
            this.player = (Player) entity;
        } else {
            throw new IllegalArgumentException("Entity must be a Player.");
        }
    }

    public LuaTable toLuaTable() {
        LuaTable lua = new LuaTable();

        lua.set("getName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(player.getName());
            }
        });

        lua.set("sendMessage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue message) {
                if (message.isstring()) {
                    String msg = message.tojstring().trim();
                    player.sendMessage(msg);
                    Bukkit.getLogger().info("[LuaPlayer] Sent to " + player.getName() + ": " + msg);
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: sendMessage expects a string.");
            }
        });

        lua.set("getLookDirection", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                Vector dir = player.getLocation().getDirection();
                return new LuaVector3(dir).toLuaTable();
            }
        });

        lua.set("teleport", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue vecValue) {
                if (!vecValue.istable()) {
                    return LuaValue.error("teleport(Vector3) expects a Vector3 table");
                }

                LuaTable vec = vecValue.checktable();
                try {
                    Vector vector = LuaVector3.fromTable(vec);
                    Location loc = new Location(player.getWorld(), vector.getX(), vector.getY(), vector.getZ());
                    player.teleport(loc);
                    return LuaValue.NIL;
                } catch (LuaError e) {
                    return LuaValue.error("teleport(Vector3) expects valid x/y/z fields in the table");
                }
            }
        });

        lua.set("getHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(player.getHealth());
            }
        });

        lua.set("setHealth", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue value) {
                if (value.isnumber()) {
                    int health = value.toint();
                    player.setHealth(Math.max(0, Math.min(health, 20)));
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: setHealth expects a number.");
            }
        });

        lua.set("isOp", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(player.isOp());
            }
        });

        lua.set("setOp", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue value) {
                if (value.isboolean()) {
                    player.setOp(value.toboolean());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: setOp expects a boolean.");
            }
        });

        lua.set("kick", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue reason) {
                if (reason.isstring()) {
                    player.kickPlayer(reason.tojstring());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: kick(reason) expects a string.");
            }
        });

        lua.set("getType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf("player");
            }
        });

        lua.set("setItemInHand", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() >= 3 &&
                        args.arg(2).isstring() &&
                        args.arg(3).isnumber()) {

                    String matName = args.arg(2).tojstring();
                    int amount = args.arg(3).toint();
                    Material mat = Material.getMaterial(matName.toUpperCase());

                    if (mat != null) {
                        player.setItemInHand(new ItemStack(mat, amount));
                        return LuaValue.NIL;
                    }
                    return LuaValue.valueOf("Invalid material name: " + matName);
                }
                return LuaValue.valueOf("Usage: setItemInHand(material, amount)");
            }
        });

        lua.set("giveItem", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() >= 3 && args.arg(2).isstring() && args.arg(3).isnumber()) {
                    String input = args.arg(2).tojstring();
                    int amount = args.arg(3).toint();
        
                    ItemStack item = null;
        
                    if (input.contains(":")) {
                        String[] parts = input.split(":");
                        try {
                            int id = Integer.parseInt(parts[0]);
                            short data = Short.parseShort(parts[1]);
        
                            Material mat = Material.getMaterial(id);
                            if (mat != null) {
                                item = new ItemStack(mat, amount, data);
                            }
                        } catch (NumberFormatException e) {
                            return LuaValue.valueOf("Invalid item ID format: " + input);
                        }
                    } else {
                        Material mat = Material.getMaterial(input.toUpperCase());
                        if (mat != null) {
                            item = new ItemStack(mat, amount);
                        }
                    }
        
                    if (item != null) {
                        player.getInventory().addItem(item);
                        return LuaValue.NIL;
                    }
        
                    return LuaValue.valueOf("Invalid material: " + input);
                }
        
                return LuaValue.valueOf("Usage: giveItem(material, amount)");
            }
        });        

        lua.set("getLocation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                Location loc = player.getLocation();
                return new LuaVector3(loc.getX(), loc.getY(), loc.getZ()).toLuaTable();
            }
        });

        lua.set("getDimension", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                String worldName = player.getWorld().getName();

                if ("world".equals(worldName)) {
                    return LuaValue.valueOf("overworld");
                } else if ("world_nether".equals(worldName)) {
                    return LuaValue.valueOf("nether");
                }

                return LuaValue.valueOf("unknown");
            }
        });

        lua.set("getWorld", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                World world = player.getWorld();
                LuaWorld luaWorld = new LuaWorld(world);
                return luaWorld.toLuaTable();
            }
        });

        lua.set("getItemInHand", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                ItemStack item = player.getItemInHand();
                return item != null ? new LuaItemStack(item).toLuaTable() : LuaValue.NIL;
            }
        });

        lua.set("setItemInHand", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue arg) {
                if (arg.istable()) {
                    LuaTable t = arg.checktable();
                    LuaValue typeFunc = t.get("getType");
                    LuaValue amountFunc = t.get("getAmount");

                    if (typeFunc.isfunction() && amountFunc.isfunction()) {
                        String type = typeFunc.call().tojstring();
                        int amount = amountFunc.call().toint();

                        Material mat = Material.getMaterial(type.toUpperCase());
                        if (mat != null) {
                            player.setItemInHand(new ItemStack(mat, amount));
                            return LuaValue.NIL;
                        }
                        return LuaValue.error("Invalid material type: " + type);
                    }

                    return LuaValue.error("LuaItemStack is missing getType or getAmount function");
                }

                return LuaValue.error("Expected LuaItemStack table");
            }
        });

        lua.set("getVelocity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                Vector vec = player.getVelocity();
                return new LuaVector3(vec).toLuaTable();
            }
        });

        lua.set("setVelocity", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue vecValue) {
                if (!vecValue.istable()) {
                    return LuaValue.error("setVelocity(Vector3) expects a Vector3 table");
                }

                try {
                    Vector vec = LuaVector3.fromTable(vecValue.checktable());
                    player.setVelocity(vec);
                    return LuaValue.NIL;
                } catch (LuaError e) {
                    return LuaValue.error("setVelocity(Vector3) expects numeric x, y, z fields");
                }
            }
        });

        lua.set("heal", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                player.setHealth(20);
                return LuaValue.NIL;
            }
        });

        lua.set("setHealth", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue value) {
                if (value.isnumber()) {
                    int health = Math.max(0, Math.min(value.toint(), 20));
                    player.setHealth(health);
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: setHealth expects a number.");
            }
        });

        lua.set("getMaxHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(20);
            }
        });

        lua.set("isAlive", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(player.getHealth() > 0);
            }
        });

        lua.set("damage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue amount) {
                if (amount.isnumber()) {
                    player.damage(amount.toint());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: damage(amount) expects a number.");
            }
        });

        lua.set("kill", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                player.setHealth(0);
                return LuaValue.NIL;
            }
        });

        lua.set("setFireTicks", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue ticks) {
                if (ticks.isnumber()) {
                    player.setFireTicks(ticks.toint());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: setFireTicks expects a number.");
            }
        });

        return lua;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaPlayer");

        LuaDocRegistry.addFunction("LuaPlayer", "getName", "Returns the name of the player.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("string", "The player's name")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "sendMessage", "Sends a message to the player.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("message", "string")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getLookDirection",
                "Returns the direction the player is looking as a Vector3.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("Vector3", "Direction vector the player is facing")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "teleport", "Teleports the player to the given Vector3 position.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("position", "Vector3")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getHealth", "Gets the player's current health.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("number", "Current health value")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "setHealth", "Sets the player's health, clamped between 0 and 20.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("health", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "isOp", "Checks if the player is an operator.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("boolean", "True if the player is op")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "setOp", "Sets the player's operator status.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("value", "boolean")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "kick", "Kicks the player with the given reason.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("reason", "string")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getType",
                "Returns the entity type name for compatibility with LuaEntity.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("string", "Always returns 'player'")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getItemInHand", "Gets the item in the player's hand.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("LuaItemStack|nil", "LuaItemStack table or nil if empty")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "setItemInHand", "Sets the item in the player's hand.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("material", "string"),
                        new Param("amount", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "giveItem", "Gives the player an item.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("material", "string"),
                        new Param("amount", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getLocation", "Returns the player's current position as a Vector3.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("Vector3", "Player position vector")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getDimension", "Gets the dimension name the player is currently in.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("string", "Dimension name (overworld, nether, unknown)")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getWorld", "Gets the LuaWorld object the player is in.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("LuaWorld", "The world the player is currently in")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getVelocity",
                "Returns the player's current velocity as a Vector3.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("Vector3", "Velocity vector")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "setVelocity",
                "Sets the player's velocity using a Vector3.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("velocity", "Vector3")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "heal", "Fully restores the player's health.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "setHealth", "Sets the player's health, clamped between 0 and 20.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("health", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "getMaxHealth",
                "Returns the maximum health value for the player (always 20 in Beta).",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("number", "Maximum health value")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "isAlive", "Returns whether the player is alive (health > 0).",
                Arrays.asList(new Param("self", "LuaPlayer")),
                Arrays.asList(new Return("boolean", "True if the player is alive")),
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "damage", "Damages the player by the given amount.",
                Arrays.asList(new Param("self", "LuaPlayer"), new Param("amount", "number")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "kill", "Kills the player by setting their health to 0.",
                Arrays.asList(new Param("self", "LuaPlayer")),
                null,
                true);

        LuaDocRegistry.addFunction("LuaPlayer", "setFireTicks", "Sets the number of fire ticks the player is burning.",
                Arrays.asList(
                        new Param("self", "LuaPlayer"),
                        new Param("ticks", "number")),
                null,
                true);
    }
}
