package com.shawnjb.luacraftbeta.commands;

import com.shawnjb.luacraftbeta.LuaManager;
import com.shawnjb.luacraftbeta.console.GuiConsoleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetLuaEnvironmentCommand implements CommandExecutor {
    private final LuaManager luaManager;

    public ResetLuaEnvironmentCommand(LuaManager luaManager) {
        this.luaManager = luaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        luaManager.reset();
        GuiConsoleManager.printToConsole("Lua environment has been reset.");
        sender.sendMessage("Lua environment reset successfully.");
        return true;
    }
}
