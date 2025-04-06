package com.shawnjb.luacraftbeta;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LuaCraftListener implements Listener {
    private final LuaCraftBetaPlugin plugin;
    private final LuaCraftConfig config;

    public LuaCraftListener(LuaCraftBetaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config.isAutoLoadScripts()) {
            String welcomeScript = "welcome.lua";
            String scriptsDir = config.getScriptsDirectory();
            String scriptPath = scriptsDir + "/" + welcomeScript;

            plugin.getLogger().info("Attempting to load script: " + scriptPath);

            plugin.getLuaManager().executeScript(scriptPath, event.getPlayer().getName(), true);
        }
    }
}
