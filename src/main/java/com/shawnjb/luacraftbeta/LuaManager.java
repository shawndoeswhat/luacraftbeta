package com.shawnjb.luacraftbeta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.shawnjb.luacraftbeta.lua.LuaBindings;

public class LuaManager {
    private final LuaCraftBetaPlugin plugin;
    private final Globals globals;

    public LuaManager(LuaCraftBetaPlugin plugin) {
        this.plugin = plugin;

        LuaBindings bindings = new LuaBindings();
        bindings.registerAll();
        this.globals = bindings.getGlobals();
    }

    public void loadScript(File scriptFile) {
        if (!scriptFile.exists()) {
            plugin.getLogger().severe("Script not found: " + scriptFile.getName());
            return;
        }

        try (FileReader reader = new FileReader(scriptFile)) {
            LuaValue chunk = globals.load(reader, scriptFile.getName(), globals);
            chunk.call();

            plugin.getLogger().info("Successfully loaded and executed script: " + scriptFile.getName());
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
            plugin.getLogger().info("Successfully executed inline Lua script.");
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

            plugin.getLogger().info("Successfully executed script: " + scriptPath);

        } catch (IOException e) {
            plugin.getLogger().severe("Error reading script file: " + scriptPath);
            e.printStackTrace();
        } catch (LuaError luaError) {
            plugin.getLogger().severe("Lua execution error in script: " + scriptPath);
            luaError.printStackTrace();
        }
    }
}
