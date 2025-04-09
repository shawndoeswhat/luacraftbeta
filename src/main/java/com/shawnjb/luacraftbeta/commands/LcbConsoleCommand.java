package com.shawnjb.luacraftbeta.commands;

import com.shawnjb.luacraftbeta.console.GuiConsoleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LcbConsoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GuiConsoleManager.showConsole();
        sender.sendMessage("LuaCraftBeta console opened.");
        return true;
    }
}
