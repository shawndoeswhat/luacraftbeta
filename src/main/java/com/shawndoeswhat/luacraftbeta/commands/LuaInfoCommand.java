package com.shawndoeswhat.luacraftbeta.commands;

import com.shawndoeswhat.luacraftbeta.LuaCraftBetaPlugin;
import com.shawndoeswhat.luacraftbeta.LuaManager;
import com.shawndoeswhat.luacraftbeta.auth.AuthMeHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;

public class LuaInfoCommand implements CommandExecutor {

    private final LuaCraftBetaPlugin plugin;
    private final LuaManager luaManager;

    public LuaInfoCommand(LuaCraftBetaPlugin plugin, LuaManager luaManager) {
        this.plugin = plugin;
        this.luaManager = luaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !sender.hasPermission("luacraftbeta.luainfo")) {
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

        PluginDescriptionFile pdf = plugin.getDescription();
        int loadedCount = luaManager.getLoadedScriptCount();

        File scriptDir = new File(plugin.getDataFolder(), "scripts");
        int availableScripts = scriptDir.exists() ? scriptDir.list((dir, name) -> name.endsWith(".lua")).length : 0;

        sender.sendMessage("§6[LuaCraftBeta]§f Custom Lua API for Bukkit Beta 1.7.3!");
        sender.sendMessage("§eEngine:§f LuaJ | luaj-jse-3.0.2");
        sender.sendMessage("§eLoaded Scripts:§f " + loadedCount);
        sender.sendMessage("§ePlugin Version:§f " + pdf.getVersion());
        sender.sendMessage("§eAvailable Scripts:§f " + availableScripts);
        sender.sendMessage("§7Explore and automate your Minecraft world with Lua power!");

        return true;
    }
}
