package com.shawnjb.luacraftbeta;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import com.shawnjb.luacraftbeta.commands.LoadScriptCommand;
import com.shawnjb.luacraftbeta.commands.ListScriptsCommand;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LuaCraftBetaPlugin extends JavaPlugin {
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    private LuaCraftConfig config;
    private LuaManager luaManager;

    @Override
    public void onEnable() {
        log = this.getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File scriptsDir = new File(getDataFolder(), "scripts");
        if (!scriptsDir.exists() && scriptsDir.mkdirs()) {
            log.info("[" + pluginName + "] Created scripts directory.");
        }

        extractLuaScriptsFromJar();

        config = new LuaCraftConfig(this, new File(getDataFolder(), "config.yml"));
        luaManager = new LuaManager(this);

        getCommand("loadscript").setExecutor(new LoadScriptCommand(this, luaManager));
        getCommand("listscripts").setExecutor(new ListScriptsCommand(this));

        getServer().getPluginManager().registerEvents(new LuaCraftListener(this), this);

        ScriptDirectoryWatcher scriptWatcher = new ScriptDirectoryWatcher(this);
        scriptWatcher.start();

        LuaCraftListener listener = new LuaCraftListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        log.info("[" + pluginName + "] Is Loaded, Version: " + pdf.getVersion());
    }

    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] Is Unloading, Version: " + pdf.getVersion());
        log.info("[" + pluginName + "] Is Unloaded, Version: " + pdf.getVersion());
    }

    private void extractLuaScriptsFromJar() {
        try {
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.startsWith("scripts/") && name.endsWith(".lua") && !entry.isDirectory()) {
                        File outFile = new File(getDataFolder(), name);
                        if (!outFile.exists()) {
                            saveResource(name, false);
                            getLogger().info("Extracted script: " + name);
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            getLogger().severe("Failed to extract Lua scripts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        File outFile = new File(getDataFolder(), resourcePath);

        if (!outFile.exists() || replace) {
            try (InputStream in = getResourceAsStream(resourcePath);
                    OutputStream out = new FileOutputStream(outFile)) {

                if (in == null) {
                    throw new IllegalArgumentException("Resource not found: " + resourcePath);
                }

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

            } catch (IOException e) {
                getLogger().warning("Could not save resource " + resourcePath + ": " + e.getMessage());
            }
        }
    }

    public InputStream getResourceAsStream(String resourcePath) {
        return getClass().getClassLoader().getResourceAsStream(resourcePath);
    }

    public Logger getLogger() {
        return Bukkit.getLogger();
    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }

    public LuaCraftConfig getConfig() {
        if (config == null) {
            return new LuaCraftConfig(this, new File(getDataFolder(), "config.yml"));
        }
        return config;
    }

    public LuaManager getLuaManager() {
        return luaManager;
    }
}
