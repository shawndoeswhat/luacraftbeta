package com.shawnjb.luacraftbeta;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import com.legacyminecraft.poseidon.event.PlayerDeathEvent;

import org.luaj.vm2.LuaValue;
import com.shawnjb.luacraftbeta.lua.api.LuaEntity;
import com.shawnjb.luacraftbeta.lua.api.LuaPlayer;

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

    public void handlePoseidonPlayerDeath(PlayerDeathEvent event) {
        if (config.isAutoLoadScripts()) {
            String deathMsg = event.getDeathMessage();
            if (deathMsg == null) deathMsg = "Player died.";

            LuaPlayer luaPlayer = new LuaPlayer((Player) event.getEntity());

            tryExecuteScript("playerdied.lua",
                luaPlayer.toLuaTable(),
                LuaValue.valueOf(deathMsg)
            );
        }
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
        if (config.isAutoLoadScripts()) {
            try {
                LuaValue worldName = LuaValue.valueOf(event.getWorld().getName());
                LuaValue toState = LuaValue.valueOf(event.toWeatherState()); // true = rain starting

                tryExecuteScript("weatherchanged.lua", worldName, toState);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to execute weatherchanged.lua: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (config.isAutoLoadScripts()) {
            LuaPlayer luaPlayer = new LuaPlayer(event.getPlayer());

            tryExecuteScript("playerinteract.lua",
                    luaPlayer.toLuaTable(),
                    LuaValue.valueOf(event.getAction().name()),
                    LuaValue.valueOf(
                            event.getClickedBlock() != null
                                    ? event.getClickedBlock().getType().name()
                                    : "AIR"));
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
                tryExecuteScript("entitydamaged.lua",
                        LuaValue.userdataOf(entity),
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
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
    
        Player player = (Player) event.getEntity();
        LuaPlayer luaPlayer = new LuaPlayer(player);
    
        String deathMsg = "rest in peace, " + player.getName();
        tryExecuteScript("playerdied.lua",
            luaPlayer.toLuaTable(),
            LuaValue.valueOf(deathMsg)
        );
    }
}