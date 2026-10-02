package com.github.smallinger.copperagebackport.config;

import com.github.smallinger.copperagebackport.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared configuration for Copper Age Backport.
 * Works on all loaders - persisted as JSON in the config directory.
 */
public final class CommonConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // Config keys
    private static final String KEY_END_FLASH_ENABLED = "endFlashEnabled";
    
    // Default values
    private static final boolean DEFAULT_END_FLASH_ENABLED = true;
    
    // Runtime values
    private static boolean endFlashEnabled = DEFAULT_END_FLASH_ENABLED;
    
    // Config file path (set by platform)
    private static Path configPath;

    private CommonConfig() {
    }

    /**
     * Initialize the config system with the platform-specific config directory.
     * Call this once during mod initialization.
     */
    public static void init(Path configDir) {
        configPath = configDir.resolve("copperagebackport.json");
        load();
    }

    /**
     * Enable or disable End dimension sky flashing effect.
     * Default: true
     */
    public static boolean endFlashEnabled() {
        return endFlashEnabled;
    }

    public static void setEndFlashEnabled(boolean value) {
        endFlashEnabled = value;
    }

    /**
     * Load config from disk. Creates default config if not present.
     */
    public static void load() {
        if (configPath == null) {
            Constants.LOG.warn("Config path not set, using defaults");
            return;
        }

        if (!Files.exists(configPath)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json == null) {
                return;
            }

            if (json.has(KEY_END_FLASH_ENABLED)) {
                endFlashEnabled = json.get(KEY_END_FLASH_ENABLED).getAsBoolean();
            }

            Constants.LOG.info("Loaded config: endFlashEnabled={}", endFlashEnabled);
        } catch (IOException | IllegalStateException e) {
            Constants.LOG.error("Failed to load config, using defaults", e);
        }
    }

    /**
     * Save current config to disk.
     */
    public static void save() {
        if (configPath == null) {
            Constants.LOG.warn("Config path not set, cannot save");
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty(KEY_END_FLASH_ENABLED, endFlashEnabled);

        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }

}
