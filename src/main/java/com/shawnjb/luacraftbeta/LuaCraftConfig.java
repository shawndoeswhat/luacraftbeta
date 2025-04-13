package com.shawnjb.luacraftbeta;

import org.bukkit.util.config.Configuration;
import java.io.File;
import java.util.logging.Level;

public class LuaCraftConfig {
    private final int configVersion = 1;
    private final LuaCraftBetaPlugin plugin;
    private Configuration config;
    private final File configFile;

    public LuaCraftConfig(LuaCraftBetaPlugin plugin, File configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
        loadConfig();
    }

    private void loadConfig() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
            plugin.logger(Level.INFO, "Created config directory: " + configFile.getParentFile().getAbsolutePath());
        }

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.logger(Level.INFO, "Default config.yml saved to: " + configFile.getAbsolutePath());
        }

        try {
            this.config = new Configuration(configFile);
            plugin.logger(Level.INFO, "Configuration object initialized.");
            config.load();
            plugin.logger(Level.INFO, "Loaded config.yml successfully from: " + configFile.getAbsolutePath());
            writeDefaults();
        } catch (Exception e) {
            plugin.logger(Level.SEVERE, "Error loading the configuration file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeDefaults() {
        if (config == null) {
            plugin.logger(Level.SEVERE, "Configuration is null, cannot write defaults.");
            return;
        }

        plugin.logger(Level.INFO, "Attempting to write defaults...");

        if (config.getKeys("config-version") == null
                || !config.getKeys("config-version").contains(String.valueOf(configVersion))) {
            config.setProperty("config-version", configVersion);
            plugin.logger(Level.INFO, "Set config-version to: " + configVersion);
        }

        if (config.getKeys("scripts.directory") == null
                || !config.getKeys("scripts.directory").contains("scripts.directory")) {
            config.setProperty("scripts.directory", "plugins/LuaCraftBeta/scripts");
            plugin.logger(Level.INFO, "Set default script directory: plugins/LuaCraftBeta/scripts");
        }

        if (config.getKeys("scripts.autoload") == null
                || !config.getKeys("scripts.autoload").contains("scripts.autoload")) {
            config.setProperty("scripts.autoload", true);
            plugin.logger(Level.INFO, "Set default autoload: true");
        }

        saveConfig();
    }

    private void saveConfig() {
        if (config != null) {
            config.save();
            plugin.logger(Level.INFO, "Configuration saved successfully.");
        } else {
            plugin.logger(Level.SEVERE, "Cannot save configuration, config is null.");
        }
    }

    public String getScriptsDirectory() {
        return config != null ? config.getString("scripts.directory", "plugins/LuaCraftBeta/scripts")
                : "plugins/LuaCraftBeta/scripts";
    }

    public boolean isAutoLoadScripts() {
        return config != null && config.getBoolean("scripts.autoload", true);
    }

    public int getConfigVersion() {
        return configVersion;
    }
}
