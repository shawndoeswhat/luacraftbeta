package com.shawnjb.luacraftbeta.console;

import com.shawnjb.luacraftbeta.LuaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class LuaConsoleBridge {
    private static Plugin plugin;
    private static LuaManager luaManager;
    private static String activePlayerName = null;

    public static void setActivePlayer(String name) {
        activePlayerName = name;
        GuiConsoleManager.printToConsole("Active player set to: " + name);
    }

    public static String getActivePlayerName() {
        return activePlayerName;
    }

    public static Player getActiveBukkitPlayer() {
        if (activePlayerName == null)
            return null;
        return Bukkit.getPlayer(activePlayerName);
    }

    public static void init(Plugin pluginInstance, LuaManager manager) {
        plugin = pluginInstance;
        luaManager = manager;
    }

    public static void loadScript(String scriptName) {
        if (scriptName.isEmpty()) {
            GuiConsoleManager.printToConsole("Usage: loadscript [name]");
            return;
        }
    
        String activePlayerName = getActivePlayerName();
        boolean debug = true;
    
        File scriptFile = new File(plugin.getDataFolder(), "scripts/" + scriptName);
        if (!scriptFile.exists()) {
            GuiConsoleManager.printToConsole("Script not found: " + scriptName);
            return;
        }
    
        luaManager.executeScript(scriptFile.getAbsolutePath(), activePlayerName, debug);
    }    

    public static void remoteLoad(String url) {
        if (url.isEmpty()) {
            GuiConsoleManager.printToConsole("Usage: remoteload [url]");
            return;
        }

        try (InputStream in = new URL(url).openStream();
                Scanner s = new Scanner(in).useDelimiter("\\A")) {

            String code = s.hasNext() ? s.next() : "";
            runInline(code);
        } catch (Exception e) {
            GuiConsoleManager.printToConsole("Failed to fetch script: " + e.getMessage());
        }
    }

    public static void runInline(String code) {
        if (code.trim().isEmpty()) {
            GuiConsoleManager.printToConsole("Usage: runscript [code]");
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                Object result = luaManager.runInline(code);
                if (result != null)
                    GuiConsoleManager.printToConsole("=> " + result);
            } catch (Exception e) {
                GuiConsoleManager.printToConsole("Lua error: " + e.getMessage());
            }
        });
    }

    public static void listScripts() {
        if (plugin == null) {
            GuiConsoleManager.printToConsole("LuaConsoleBridge plugin is null. Make sure init() was called.");
            return;
        }

        try {
            File dir = new File(plugin.getDataFolder(), "scripts");
            if (!dir.exists() || !dir.isDirectory()) {
                GuiConsoleManager.printToConsole("Script folder not found.");
                return;
            }

            String[] files = dir.list((d, name) -> name.endsWith(".lua"));
            if (files == null || files.length == 0) {
                GuiConsoleManager.printToConsole("No scripts found.");
            } else {
                GuiConsoleManager.printToConsole("Available scripts:");
                for (String file : files) {
                    GuiConsoleManager.printToConsole(" - " + file);
                }
            }
        } catch (Exception e) {
            GuiConsoleManager.printToConsole("Error listing scripts: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    public static void resetLuaEnvironment() {
        try {
            luaManager.reset();
            GuiConsoleManager.printToConsole("Lua environment has been reset and reinitialized.");
        } catch (Exception e) {
            GuiConsoleManager.printToConsole("Error resetting Lua environment: " + e.getMessage());
        }
    }

    public static void handleInput(String luaCode) {
        if (plugin == null || luaManager == null) {
            GuiConsoleManager.printToConsole("Bridge not initialized.");
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                Object result = luaManager.runInline(luaCode);
                if (result != null) {
                    GuiConsoleManager.printToConsole("=> " + result);
                }
            } catch (Exception e) {
                GuiConsoleManager.printToConsole("Lua error: " + e.getMessage());
            }
        });
    }
}
