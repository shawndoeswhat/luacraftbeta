package com.shawnjb.luacraftbeta;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import org.luaj.vm2.LuaValue;

import com.shawnjb.luacraftbeta.lua.api.LuaBlock;
import com.shawnjb.luacraftbeta.lua.api.LuaEntity;
import com.shawnjb.luacraftbeta.lua.api.LuaPlayer;
import com.shawnjb.luacraftbeta.lua.api.LuaWorld;

public class LuaCraftListener implements Listener {
    private final LuaCraftBetaPlugin plugin;
    private final LuaCraftConfig config;

    public LuaCraftListener(LuaCraftBetaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    private void tryExecuteScript(String fileName, LuaValue... args) {
        plugin.getLuaManager().executeScriptWithArgsInsensitive(fileName, true, args);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config.isAutoLoadScripts()) {
            LuaPlayer luaPlayer = new LuaPlayer(event.getPlayer());
            tryExecuteScript("playerjoining.lua", luaPlayer.toLuaTable());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (config.isAutoLoadScripts()) {
            LuaPlayer luaPlayer = new LuaPlayer(event.getPlayer());
            tryExecuteScript("playerquitting.lua", luaPlayer.toLuaTable());
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                LuaEntity luaEntity = new LuaEntity(event.getEntity());
                tryExecuteScript("entityspawned.lua", LuaValue.userdataOf(luaEntity));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to execute entityspawned.lua: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!config.isAutoLoadScripts())
            return;

        try {
            LuaWorld luaWorld = new LuaWorld(event.getWorld());
            LuaValue toState = LuaValue.valueOf(event.toWeatherState());

            tryExecuteScript("weatherchanged.lua",
                    luaWorld.toLuaTable(),
                    toState);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to execute weatherchanged.lua: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!config.isAutoLoadScripts())
            return;

        LuaPlayer luaPlayer = new LuaPlayer(event.getPlayer());

        if (event.getClickedBlock() != null) {
            LuaBlock luaBlock = new LuaBlock(event.getClickedBlock());

            tryExecuteScript("playerinteract.lua",
                    luaPlayer.toLuaTable(),
                    LuaValue.valueOf(event.getAction().name()),
                    luaBlock.toLuaTable());
        } else {
            tryExecuteScript("playerinteract.lua",
                    luaPlayer.toLuaTable(),
                    LuaValue.valueOf(event.getAction().name()),
                    LuaValue.NIL);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (config.isAutoLoadScripts()) {
            LuaPlayer luaPlayer = new LuaPlayer(event.getPlayer());

            tryExecuteScript("blockbroken.lua",
                    luaPlayer.toLuaTable(),
                    LuaValue.valueOf(event.getBlock().getType().name()),
                    LuaValue.valueOf(event.getBlock().getX()),
                    LuaValue.valueOf(event.getBlock().getY()),
                    LuaValue.valueOf(event.getBlock().getZ()));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (config.isAutoLoadScripts()) {
            LuaPlayer luaPlayer = new LuaPlayer(event.getPlayer());

            tryExecuteScript("blockplaced.lua",
                    luaPlayer.toLuaTable(),
                    LuaValue.valueOf(event.getBlock().getType().name()),
                    LuaValue.valueOf(event.getBlock().getX()),
                    LuaValue.valueOf(event.getBlock().getY()),
                    LuaValue.valueOf(event.getBlock().getZ()));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                LuaEntity entity = new LuaEntity(event.getEntity());
                LuaValue attackerValue;

                if (event instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

                    if (damager instanceof Player) {
                        attackerValue = new LuaPlayer((Player) damager).toLuaTable();
                    } else {
                        attackerValue = new LuaEntity(damager).toLuaTable();
                    }
                } else {
                    attackerValue = new LuaWorld(event.getEntity().getWorld()).toLuaTable();
                }

                tryExecuteScript("entitydamaged.lua",
                        attackerValue,
                        entity.toLuaTable(),
                        LuaValue.valueOf(event.getDamage()),
                        LuaValue.valueOf(event.getCause().name()));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to execute entitydamaged.lua: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (config.isAutoLoadScripts()) {
            LuaPlayer luaPlayer = new LuaPlayer(event.getPlayer());
            tryExecuteScript("playerchatted.lua",
                    luaPlayer.toLuaTable(),
                    LuaValue.valueOf(event.getMessage()));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!config.isAutoLoadScripts())
            return;

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            LuaPlayer luaPlayer = new LuaPlayer(player);

            tryExecuteScript("playerdied.lua",
                    luaPlayer.toLuaTable());

            tryExecuteScript("entitydeath.lua",
                    LuaValue.userdataOf(luaPlayer));
        } else {
            LuaEntity luaEntity = new LuaEntity(event.getEntity());

            tryExecuteScript("entitydeath.lua",
                    LuaValue.userdataOf(luaEntity));
        }
    }
}