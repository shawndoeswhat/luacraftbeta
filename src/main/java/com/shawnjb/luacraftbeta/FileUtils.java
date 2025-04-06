package com.shawnjb.luacraftbeta;

import java.io.*;
import java.nio.file.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class FileUtils {
    private final Logger logger;

    public FileUtils(LuaCraftBetaPlugin plugin) {
        this.logger = plugin.getLogger();
    }

    /**
     * Reads the content of a file into a String.
     *
     * @param file The file to read from.
     * @return The content of the file as a String.
     */
    public String readFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading file: " + file.getAbsolutePath(), e);
        }
        return content.toString();
    }

    /**
     * Writes content to a file.
     *
     * @param file    The file to write to.
     * @param content The content to write.
     */
    public void writeFile(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Reads a list of strings from a file (one per line).
     *
     * @param file The file to read from.
     * @return A list of strings, each representing a line from the file.
     */
    public List<String> readFileAsList(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading file as list: " + file.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * Creates a directory if it doesn't exist.
     *
     * @param dir The directory to create.
     */
    public void createDirectory(File dir) {
        if (!dir.exists()) {
            try {
                Files.createDirectories(dir.toPath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error creating directory: " + dir.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Deletes a file or directory (recursively for directories).
     *
     * @param file The file or directory to delete.
     */
    public void deleteFile(File file) {
        if (file.exists()) {
            try {
                if (file.isDirectory()) {
                    Files.walk(file.toPath())
                            .sorted((path1, path2) -> path2.compareTo(path1)) // FILES BEGONE first
                            .forEach(this::deletePath);
                } else {
                    Files.delete(file.toPath());
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error deleting file: " + file.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Deletes a single path.
     *
     * @param path The path to delete.
     */
    private void deletePath(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deleting path: " + path, e);
        }
    }

    /**
     * Copies a file to a new location.
     *
     * @param source The source file to copy.
     * @param dest   The destination file.
     */
    public void copyFile(File source, File dest) {
        try {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error copying file: " + source.getAbsolutePath(), e);
        }
    }

    /**
     * Renames a file or directory.
     *
     * @param oldFile The current file or directory.
     * @param newFile The new file or directory name.
     */
    public void renameFile(File oldFile, File newFile) {
        try {
            Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error renaming file: " + oldFile.getAbsolutePath(), e);
        }
    }
}
