package com.shawndoeswhat.luacraftbeta.commands;

import com.shawndoeswhat.luacraftbeta.LuaCraftBetaPlugin;
import com.shawndoeswhat.luacraftbeta.LuaManager;
import com.shawndoeswhat.luacraftbeta.auth.AuthMeHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;

public class LoadScriptCommand implements CommandExecutor {

    private final LuaCraftBetaPlugin plugin;
    private final LuaManager luaManager;

    public LoadScriptCommand(LuaCraftBetaPlugin plugin, LuaManager luaManager) {
        this.plugin = plugin;
        this.luaManager = luaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !sender.hasPermission("luacraftbeta.loadscript")) {
            sender.sendMessage("This command can only be executed by players or requires permission.");
            return true;
        }
    
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!AuthMeHandler.isPlayerLoggedIn(player)) {
                sender.sendMessage("You must be logged in to use this command.");
                return true;
            }
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage("Usage: /loadscript [scriptName] [debug]");
            return true;
        }

        String scriptName = args[0];
        boolean debug = args.length == 2 && args[1].equalsIgnoreCase("debug");

        File scriptFile = new File(plugin.getDataFolder(), "scripts/" + scriptName);
        if (!scriptFile.exists()) {
            sender.sendMessage("The script " + scriptName + " does not exist.");
            return true;
        }

        try {
            String playerName = (sender instanceof Player)
                ? ((Player) sender).getName()
                : "console";

            if (debug) {
                plugin.getLogger().info("[LuaCraftBeta] Executing script with debug enabled: " + scriptName);
            }

            luaManager.executeScript(scriptFile.getAbsolutePath(), playerName, debug);
            sender.sendMessage("Successfully loaded script: " + scriptName);
        } catch (Exception e) {
            sender.sendMessage("Error loading script: " + scriptName);
            plugin.getLogger().log(Level.SEVERE, "Error loading script: " + scriptName, e);
        }

        return true;
    }
}
