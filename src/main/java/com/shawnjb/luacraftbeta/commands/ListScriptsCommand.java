package com.shawnjb.luacraftbeta.commands;

import com.shawnjb.luacraftbeta.LuaCraftBetaPlugin;
import com.shawnjb.luacraftbeta.auth.AuthMeHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.File;

public class ListScriptsCommand implements CommandExecutor {

    private final LuaCraftBetaPlugin plugin;

    public ListScriptsCommand(LuaCraftBetaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !sender.hasPermission("luacraftbeta.listscripts")) {
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
    
        File scriptsDir = new File(plugin.getDataFolder(), "scripts");
    
        if (!scriptsDir.exists() || !scriptsDir.isDirectory()) {
            sender.sendMessage("The scripts directory does not exist or is not a directory.");
            return true;
        }
    
        File[] scriptFiles = scriptsDir.listFiles((dir, name) -> name.endsWith(".lua"));
    
        if (scriptFiles == null || scriptFiles.length == 0) {
            sender.sendMessage("No Lua scripts found in the scripts directory.");
            return true;
        }
    
        sender.sendMessage("Available Lua Scripts:");
        for (File scriptFile : scriptFiles) {
            sender.sendMessage("- " + scriptFile.getName());
        }
    
        return true;
    }    
}
