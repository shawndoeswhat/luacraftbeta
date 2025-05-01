package com.shawndoeswhat.luacraftbeta;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.LuaValue;

import com.shawndoeswhat.luacraftbeta.lua.api.LuaBlock;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaEntity;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaPlayer;
import com.shawndoeswhat.luacraftbeta.lua.api.LuaWorld;

public class StaticEventListener implements Listener {
    private final Plugin plugin;
    private final LuaCraftConfig config;

    public StaticEventListener(Plugin plugin) {
        this.plugin = plugin;
        this.config = ((LuaCraftBetaPlugin) plugin).getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "playerjoin.lua";
                LuaValue luaPlayer = new LuaPlayer(event.getPlayer()).toLuaTable();
                tryExecuteScript(scriptName, luaPlayer);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for PlayerJoinEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "playerquit.lua";
                LuaValue luaPlayer = new LuaPlayer(event.getPlayer()).toLuaTable();
                tryExecuteScript(scriptName, luaPlayer);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for PlayerQuitEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "playerchat.lua";
                LuaValue luaPlayer = new LuaPlayer(event.getPlayer()).toLuaTable();
                LuaValue message = LuaValue.valueOf(event.getMessage());
                tryExecuteScript(scriptName, luaPlayer, message);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for PlayerChatEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "playerinteract.lua";
                LuaValue luaPlayer = new LuaPlayer(event.getPlayer()).toLuaTable();
                String actionName = event.getAction().name();
                LuaBlock luaBlock = event.getClickedBlock() != null ? new LuaBlock(event.getClickedBlock()) : null;
                LuaValue luaBlockValue = (luaBlock != null) ? luaBlock.toLuaTable() : LuaValue.NIL;
                tryExecuteScript(scriptName, luaPlayer, LuaValue.valueOf(actionName), luaBlockValue);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for PlayerInteractEvent: " + e.getMessage());
            }
        }
    }    

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "entitydamage.lua";
                LuaValue luaEntity = new LuaEntity(event.getEntity()).toLuaTable();
                LuaValue luaAttacker = null;
                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                    Entity attacker = damageEvent.getDamager();
                    if (attacker instanceof Player) {
                        luaAttacker = new LuaPlayer((Player) attacker).toLuaTable();
                    } else {
                        luaAttacker = new LuaEntity(attacker).toLuaTable();
                    }
                }
                String damageCause = event.getCause().name();
                tryExecuteScript(scriptName, luaAttacker, luaEntity, LuaValue.valueOf(event.getDamage()),
                        LuaValue.valueOf(damageCause));
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for EntityDamageEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                if (event.getEntity() instanceof Player) {
                    String scriptName = "playerdeath.lua";
                    LuaValue luaPlayer = new LuaPlayer((Player) event.getEntity()).toLuaTable();
                    tryExecuteScript(scriptName, luaPlayer);
                } else {
                    String scriptName = "entitydeath.lua";
                    LuaValue luaEntity = new LuaEntity(event.getEntity()).toLuaTable();
                    tryExecuteScript(scriptName, luaEntity);
                }
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for EntityDeathEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "blockbreak.lua";
                LuaValue luaPlayer = new LuaPlayer(event.getPlayer()).toLuaTable();
                LuaValue luaBlock = new LuaBlock(event.getBlock()).toLuaTable();
                tryExecuteScript(scriptName, luaPlayer, luaBlock);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for BlockBreakEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "blockplace.lua";
                LuaValue luaPlayer = new LuaPlayer(event.getPlayer()).toLuaTable();
                LuaValue luaBlock = new LuaBlock(event.getBlock()).toLuaTable();
                tryExecuteScript(scriptName, luaPlayer, luaBlock);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for BlockPlaceEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "weatherchange.lua";
                LuaValue luaWorld = new LuaWorld(event.getWorld()).toLuaTable();
                LuaValue toState = LuaValue.valueOf(event.toWeatherState());
                tryExecuteScript(scriptName, luaWorld, toState);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for WeatherChangeEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "worldload.lua";
                LuaValue luaWorld = new LuaWorld(event.getWorld()).toLuaTable();
                tryExecuteScript(scriptName, luaWorld);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for WorldLoadEvent: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        if (config.isAutoLoadScripts()) {
            try {
                String scriptName = "worldunload.lua";
                LuaValue luaWorld = new LuaWorld(event.getWorld()).toLuaTable();
                tryExecuteScript(scriptName, luaWorld);
            } catch (Exception e) {
                plugin.getServer().getLogger()
                        .warning("Failed to execute Lua script for WorldUnloadEvent: " + e.getMessage());
            }
        }
    }

    private void tryExecuteScript(String fileName, Object... args) {
        LuaCraftBetaPlugin luaPlugin = (LuaCraftBetaPlugin) plugin;

        LuaValue[] luaArgs = new LuaValue[args.length];
        for (int i = 0; i < args.length; i++) {
            luaArgs[i] = (LuaValue) args[i];
        }

        luaPlugin.getLuaManager().executeScriptWithArgsInsensitive(fileName, true, luaArgs);
    }
}
