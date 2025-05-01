package com.shawndoeswhat.luacraftbeta.commands;

import com.shawndoeswhat.luacraftbeta.LuaCraftBetaPlugin;
import com.shawndoeswhat.luacraftbeta.LuaManager;
import com.shawndoeswhat.luacraftbeta.auth.AuthMeHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunScriptCommand implements CommandExecutor {

    private final LuaCraftBetaPlugin plugin;
    private final LuaManager luaManager;

    public RunScriptCommand(LuaCraftBetaPlugin plugin, LuaManager luaManager) {
        this.plugin = plugin;
        this.luaManager = luaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !sender.hasPermission("luacraftbeta.runscript")) {
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

        if (args.length == 0) {
            sender.sendMessage("Usage: /runscript [lua expression]");
            return true;
        }

        String luaCode = String.join(" ", args);
        String playerName = sender instanceof Player ? ((Player) sender).getName() : "console";

        try {
            luaManager.executeScriptFromString(luaCode, playerName);
            sender.sendMessage("Lua script executed.");
        } catch (Exception e) {
            sender.sendMessage("Error executing Lua script. Check console for details.");
            plugin.getLogger().severe("Error executing inline Lua script:");
            e.printStackTrace();
        }

        return true;
    }
}
