package com.shawndoeswhat.luacraftbeta.commands;

import com.shawndoeswhat.luacraftbeta.console.GuiConsoleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LuaConsoleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
            Command command,
            String label,
            String[] args) {
        GuiConsoleManager.showConsole();
        sender.sendMessage("Lua console opened.");
        return true;
    }
}
