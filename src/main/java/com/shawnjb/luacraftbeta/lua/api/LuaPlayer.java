package com.shawnjb.luacraftbeta.lua.api;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Param;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry.Return;

public class LuaPlayer {
    private final Player player;

    public LuaPlayer(Player player) {
        this.player = player;
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

        lua.set("teleport", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() == 2 && args.arg(2).istable()) {
                    LuaTable vec = args.arg(2).checktable();
                    double x = vec.get("x").todouble();
                    double y = vec.get("y").todouble();
                    double z = vec.get("z").todouble();
                    player.teleport(new Location(player.getWorld(), x, y, z));
                    return LuaValue.NIL;
                } else if (args.narg() >= 4 &&
                        args.arg(2).isnumber() &&
                        args.arg(3).isnumber() &&
                        args.arg(4).isnumber()) {

                    double x = args.arg(2).todouble();
                    double y = args.arg(3).todouble();
                    double z = args.arg(4).todouble();
                    player.teleport(new Location(player.getWorld(), x, y, z));
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Error: teleport(x, y, z) or teleport(Vector3)");
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

        lua.set("getItemInHand", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                ItemStack item = player.getItemInHand();
                if (item != null) {
                    return LuaValue.valueOf(item.getType().toString().toLowerCase() + ":" + item.getAmount());
                }
                return LuaValue.NIL;
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
                if (args.narg() >= 3 &&
                        args.arg(2).isstring() &&
                        args.arg(3).isnumber()) {

                    String matName = args.arg(2).tojstring();
                    int amount = args.arg(3).toint();

                    Material mat = Material.getMaterial(matName.toUpperCase());

                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, amount);
                        player.getInventory().addItem(item);
                        return LuaValue.NIL;
                    }
                    return LuaValue.valueOf("Invalid material name: " + matName);
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
                } else if ("world_the_end".equals(worldName)) {
                    return LuaValue.valueOf("the_end");
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

        return lua;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaPlayer");

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "getName",
                "Returns the name of the player.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("string", "The player's name")));

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "sendMessage",
                "Sends a message to the player.",
                List.of(
                        new Param("self", "LuaPlayer"),
                        new Param("message", "string")),
                null);

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "getLookDirection",
                "Returns the direction the player is looking as a Vector3.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("Vector3", "Direction vector the player is facing")));

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "teleport",
                "Teleports the player to the given coordinates or Vector3.",
                List.of(
                        new Param("self", "LuaPlayer"),
                        new Param("x", "number"),
                        new Param("y", "number"),
                        new Param("z", "number")),
                null);

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "getHealth",
                "Gets the player's current health.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("number", "Current health value")));

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "setHealth",
                "Sets the player's health, clamped between 0 and 20.",
                List.of(
                        new Param("self", "LuaPlayer"),
                        new Param("health", "number")),
                null);

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "isOp",
                "Checks if the player is an operator.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("boolean", "True if the player is op")));

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "setOp",
                "Sets the player's operator status.",
                List.of(
                        new Param("self", "LuaPlayer"),
                        new Param("value", "boolean")),
                null);

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "kick",
                "Kicks the player with the given reason.",
                List.of(
                        new Param("self", "LuaPlayer"),
                        new Param("reason", "string")),
                null);

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "getItemInHand",
                "Gets the item in the player's hand.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("string", "Item in format 'material:amount' or nil")));

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "setItemInHand",
                "Sets the item in the player's hand.",
                List.of(
                        new Param("self", "LuaPlayer"),
                        new Param("material", "string"),
                        new Param("amount", "number")),
                null);

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "giveItem",
                "Gives the player an item.",
                List.of(
                        new Param("self", "LuaPlayer"),
                        new Param("material", "string"),
                        new Param("amount", "number")),
                null);

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "getLocation",
                "Returns the player's current position as a Vector3.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("Vector3", "Player position vector")));

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "getDimension",
                "Gets the dimension name the player is currently in.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("string", "Dimension name (overworld, nether, the_end, unknown)")));

        LuaDocRegistry.addFunction(
                "LuaPlayer",
                "getWorld",
                "Gets the LuaWorld object the player is in.",
                List.of(new Param("self", "LuaPlayer")),
                List.of(new Return("LuaWorld", "The world the player is currently in")));
    }
}
