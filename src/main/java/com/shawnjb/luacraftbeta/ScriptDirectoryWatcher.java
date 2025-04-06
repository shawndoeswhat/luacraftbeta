package com.shawnjb.luacraftbeta;

import java.io.File;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptDirectoryWatcher implements Runnable {

    private final LuaCraftBetaPlugin plugin;
    private final File scriptDirectory;
    private final Logger logger;
    private WatchService watchService;

    public ScriptDirectoryWatcher(LuaCraftBetaPlugin plugin) {
        this.plugin = plugin;
        this.scriptDirectory = new File(plugin.getDataFolder(), "scripts");
        this.logger = plugin.getLogger();
    }

    /**
     * Starts the directory watcher.
     */
    public void start() {
        try {
            if (!scriptDirectory.exists()) {
                scriptDirectory.mkdirs();
            }

            watchService = FileSystems.getDefault().newWatchService();
            Path path = scriptDirectory.toPath();

            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            new Thread(this).start();
            logger.info("Started watching scripts directory: " + scriptDirectory.getAbsolutePath());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting script directory watcher", e);
        }
    }

    /**
     * Stops the directory watcher.
     */
    public void stop() {
        try {
            if (watchService != null) {
                watchService.close();
            }
            logger.info("Stopped watching scripts directory.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error stopping script directory watcher", e);
        }
    }

    /**
     * Handles the file change events.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path filePath = (Path) event.context();
                    File affectedFile = filePath.toFile();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        logger.info("File created: " + affectedFile.getAbsolutePath());
                        reloadScript(affectedFile);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        logger.info("File modified: " + affectedFile.getAbsolutePath());
                        reloadScript(affectedFile);
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        logger.info("File deleted: " + affectedFile.getAbsolutePath());
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Reload or re-execute the Lua script when it is created or modified.
     *
     * @param scriptFile The script file that was created or modified.
     */
    private void reloadScript(File scriptFile) {
        try {
            if (scriptFile.exists()) {
                plugin.getLuaManager().loadScript(scriptFile);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reloading script: " + scriptFile.getName(), e);
        }
    }
}
