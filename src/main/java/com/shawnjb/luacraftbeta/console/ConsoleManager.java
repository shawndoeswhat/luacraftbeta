package com.shawnjb.luacraftbeta.console;

public class ConsoleManager {
    public static void handleCommand(String input) {
        if (input == null || input.trim().isEmpty())
            return;

        String[] parts = input.trim().split(" ", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "setplayer":
                if (args.isEmpty()) {
                    GuiConsoleManager.printToConsole("Usage: setplayer [name]");
                } else {
                    LuaConsoleBridge.setActivePlayer(args);
                }
                break;
            case "clearplayer":
            case "unsetplayer":
                LuaConsoleBridge.setActivePlayer(null);
                GuiConsoleManager.printToConsole("Cleared active player.");
                break;
            case "loadscript":
                LuaConsoleBridge.loadScript(args);
                break;
            case "remoteload":
                LuaConsoleBridge.remoteLoad(args);
                break;
            case "runscript":
                LuaConsoleBridge.runInline(args);
                break;
            case "listscripts":
                LuaConsoleBridge.listScripts();
                break;
            case "exit":
                GuiConsoleManager.printToConsole("Goodbye.");
                break;
            default:
                GuiConsoleManager.printToConsole("Unknown command: " + command);
                break;
        }
    }
}
