package com.shawnjb.luacraftbeta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.shawnjb.luacraftbeta.console.LuaConsoleBridge;
import com.shawnjb.luacraftbeta.console.GuiConsoleManager;
import com.shawnjb.luacraftbeta.lua.LuaBindings;

public class LuaManager {
    private final LuaCraftBetaPlugin plugin;
    private final Globals globals;
    private final Set<String> missingScripts = new HashSet<>();
    private int loadedScriptCount = 0;

    public LuaManager(LuaCraftBetaPlugin plugin) {
        this.plugin = plugin;

        LuaBindings bindings = new LuaBindings();
        bindings.registerAll(plugin, this);
        this.globals = bindings.getGlobals();
    }

    public int getLoadedScriptCount() {
        return loadedScriptCount;
    }

    public void loadScript(File scriptFile) {
        if (!scriptFile.exists()) {
            plugin.getLogger().severe("Script not found: " + scriptFile.getName());
            return;
        }

        try (FileReader reader = new FileReader(scriptFile)) {
            LuaValue chunk = globals.load(reader, scriptFile.getName(), globals);
            chunk.call();
            loadedScriptCount++;
        } catch (IOException e) {
            plugin.getLogger().severe("Error reading script file: " + scriptFile.getName());
            e.printStackTrace();
        } catch (LuaError luaError) {
            plugin.getLogger().severe("Lua execution error in script: " + scriptFile.getName());
            luaError.printStackTrace();
        }
    }

    public void executeScriptFromString(String script, String playerName) {
        try {
            LuaValue chunk = globals.load(script, "inlineScript", globals);
            chunk.call();
            globals.set("playerName", LuaValue.valueOf(playerName));
        } catch (LuaError luaError) {
            plugin.getLogger().severe("Lua execution error in inline script.");
            luaError.printStackTrace();
        }
    }

    public void executeScript(String scriptPath, String playerName, boolean debug) {
        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists()) {
            plugin.getLogger().severe("Script not found: " + scriptPath);
            return;
        }

        try (FileReader reader = new FileReader(scriptFile)) {
            LuaValue chunk = globals.load(reader, scriptFile.getName(), globals);
            LuaValue returned = chunk.call();

            if (!returned.isfunction()) {
                plugin.getLogger().warning("Script " + scriptFile.getName() + " did not return a function.");
                return;
            }

            Player player = Bukkit.getPlayer(playerName);
            LuaValue luaPlayer = LuaValue.NIL;

            if (player != null && player.isOnline()) {
                luaPlayer = new com.shawnjb.luacraftbeta.lua.api.LuaPlayer(player).toLuaTable();
            } else {
                plugin.getLogger().warning("Player " + playerName + " not found or offline. Passing nil.");
                GuiConsoleManager.printError("Player '" + playerName + "' not found or offline. Using nil.");
            }

            globals.set("player", luaPlayer);

            if (debug) {
                plugin.getLogger().info("[LuaManager] Script: " + scriptFile.getName());
                plugin.getLogger().info("[LuaManager] Player: " + playerName);
                plugin.getLogger().info("[LuaManager] LuaPlayer Table Keys:");

                for (LuaValue k : luaPlayer.checktable().keys()) {
                    plugin.getLogger().info(" - " + k.tojstring());
                }

                LuaValue testName = luaPlayer.get("getName");
                if (!testName.isnil()) {
                    LuaValue result = testName.call(luaPlayer);
                    plugin.getLogger().info("[LuaManager] getName() returned: " + result.tojstring());
                } else {
                    plugin.getLogger().info("[LuaManager] getName() is nil!");
                }
            }

            returned.call(luaPlayer);
            loadedScriptCount++;
        } catch (IOException e) {
            plugin.getLogger().severe("Error reading script file: " + scriptPath);
            e.printStackTrace();
        } catch (LuaError luaError) {
            plugin.getLogger().severe("Lua execution error in script: " + scriptPath);
            luaError.printStackTrace();
        }
    }

    public void executeScript(String scriptPath, boolean debug) {
        String playerName = LuaConsoleBridge.getActivePlayerName();
        executeScript(scriptPath, playerName, debug);
    }

    public void executeScriptWithArgsInsensitive(String fileName, boolean isAutorun, LuaValue... args) {
        File scriptsDir = new File(plugin.getConfig().getScriptsDirectory());

        if (!scriptsDir.exists() || !scriptsDir.isDirectory()) {
            plugin.getLogger().warning("Scripts directory does not exist or is not a directory: " + scriptsDir.getPath());
            return;
        }

        File[] files = scriptsDir.listFiles();
        if (files == null) {
            plugin.getLogger().warning("Failed to list files in scripts directory.");
            return;
        }

        File matchedFile = null;
        for (File file : files) {
            if (file.getName().equalsIgnoreCase(fileName)) {
                matchedFile = file;
                break;
            }
        }

        if (matchedFile == null) {
            String lower = fileName.toLowerCase();
            if (!missingScripts.contains(lower)) {
                plugin.getLogger().warning("Lua script not found (case-insensitive): " + fileName);
                missingScripts.add(lower);
            }
            return;
        }

        try (FileReader reader = new FileReader(matchedFile)) {
            LuaValue chunk = globals.load(reader, matchedFile.getName(), globals);
            LuaValue returned = chunk.call();

            if (returned.isfunction()) {
                returned.invoke(args);
                loadedScriptCount++;
            } else if (!isAutorun) {
                plugin.getLogger().warning("Script " + matchedFile.getName() + " did not return a function.");
            }

        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error reading script file: " + matchedFile.getPath(), e);
        } catch (LuaError luaError) {
            plugin.getLogger().log(Level.SEVERE, "Lua execution error in script: " + matchedFile.getName(), luaError);
        }
    }

    public Object runInline(String luaCode) {
        try {
            String playerName = LuaConsoleBridge.getActivePlayerName();

            if (playerName != null) {
                plugin.getLogger().info("[LuaManager] Running inline code as: " + playerName);
                Player player = Bukkit.getPlayer(playerName);
                LuaValue luaPlayer = LuaValue.NIL;

                if (player != null && player.isOnline()) {
                    luaPlayer = new com.shawnjb.luacraftbeta.lua.api.LuaPlayer(player).toLuaTable();
                } else {
                    GuiConsoleManager.printError("Player '" + playerName + "' not found or offline. Using nil.");
                }

                globals.set("player", luaPlayer);
            }

            return globals.load(luaCode).call();
        } catch (Exception e) {
            throw new RuntimeException("Lua error: " + e.getMessage(), e);
        }
    }

    public boolean loadScript(String name) {
        File file = new File(plugin.getDataFolder(), "scripts/" + name);
        return loadScriptFromFile(file);
    }

    public boolean loadScriptFromFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        try {
            String script = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            globals.load(script, file.getName()).call();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void notifyScriptCreatedOrModified(String fileName) {
        missingScripts.remove(fileName.toLowerCase());
    }
}