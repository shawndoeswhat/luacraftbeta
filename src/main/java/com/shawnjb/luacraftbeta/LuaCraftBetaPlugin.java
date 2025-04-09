package com.shawnjb.luacraftbeta;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import com.shawnjb.luacraftbeta.commands.LoadScriptCommand;
import com.shawnjb.luacraftbeta.commands.LuaInfoCommand;
import com.shawnjb.luacraftbeta.commands.RunScriptCommand;
import com.shawnjb.luacraftbeta.console.GuiConsoleManager;
import com.shawnjb.luacraftbeta.console.LuaConsoleBridge;
import com.shawnjb.luacraftbeta.auth.AuthMeHandler;
import com.shawnjb.luacraftbeta.commands.LcbConsoleCommand;
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
        AuthMeHandler.init();

        config = new LuaCraftConfig(this, new File(getDataFolder(), "config.yml"));
        luaManager = new LuaManager(this);

        getCommand("loadscript").setExecutor(new LoadScriptCommand(this, luaManager));
        getCommand("listscripts").setExecutor(new ListScriptsCommand(this));
        getCommand("luainfo").setExecutor(new LuaInfoCommand(this, luaManager));
        getCommand("runscript").setExecutor(new RunScriptCommand(this, luaManager));
        getCommand("lcbconsole").setExecutor(new LcbConsoleCommand());

        getServer().getPluginManager().registerEvents(new LuaCraftListener(this), this);

        ScriptDirectoryWatcher scriptWatcher = new ScriptDirectoryWatcher(this);
        scriptWatcher.start();

        LuaConsoleBridge.init(this, this.luaManager);
        log.info("[LuaCraftBeta] Launching in-process console thread...");
        new Thread(() -> GuiConsoleManager.launch(), "LuaCraftBetaGuiConsoleThread").start();

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

                    boolean isLuaScript = name.startsWith("scripts/") && name.endsWith(".lua");
                    boolean isDocsFile = name.startsWith("docs/") && name.endsWith(".lua");

                    if ((isLuaScript || isDocsFile) && !entry.isDirectory()) {
                        File outFile = new File(getDataFolder(), name);

                        if (outFile.exists()) {
                            getLogger().info("Skipped extraction (already exists): " + name);
                            continue;
                        }

                        outFile.getParentFile().mkdirs();

                        try (InputStream in = jar.getInputStream(entry);
                                OutputStream out = new FileOutputStream(outFile)) {

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = in.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }

                            getLogger().info("Extracted: " + name);

                        } catch (IOException e) {
                            getLogger().warning("Failed to extract: " + name + " -> " + e.getMessage());
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            getLogger().severe("Failed to extract Lua resources: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public InputStream getResourceAsStream(String resourcePath) {
        return getClass().getClassLoader().getResourceAsStream(resourcePath);
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        InputStream in = getResourceAsStream(resourcePath);
        if (in == null) {
            getLogger().warning("The embedded resource '" + resourcePath + "' cannot be found in the JAR.");
            return;
        }

        File outFile = new File(getDataFolder(), resourcePath);
        File outDir = outFile.getParentFile();

        if (!outDir.exists() && !outDir.mkdirs()) {
            getLogger().warning("Failed to create directories for: " + outDir.getPath());
            return;
        }

        if (!outFile.exists() || replace) {
            try (OutputStream out = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                getLogger().info("Extracted resource: " + resourcePath);
            } catch (IOException e) {
                getLogger().warning("Failed to save resource '" + resourcePath + "': " + e.getMessage());
            }
        } else {
            getLogger().info("Resource already exists and will not be replaced: " + resourcePath);
        }
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
