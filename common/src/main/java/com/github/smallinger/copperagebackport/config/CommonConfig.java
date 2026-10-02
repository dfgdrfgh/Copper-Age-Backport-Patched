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
    private static final String KEY_GOLEM_BUILD_SPAWNING = "golemBuildSpawning";
    private static final String KEY_GOLEM_TRANSPORT_STACK_SIZE = "golemTransportStackSize";
    private static final String KEY_WEATHERING_TICK_FROM = "weatheringTickFrom";
    private static final String KEY_WEATHERING_TICK_TO = "weatheringTickTo";
    private static final String KEY_END_FLASH_ENABLED = "endFlashEnabled";
    
    // Default values
    private static final boolean DEFAULT_GOLEM_BUILD_SPAWNING = true;
    private static final int DEFAULT_GOLEM_TRANSPORT_STACK_SIZE = 16;
    private static final int DEFAULT_WEATHERING_TICK_FROM = 504000; // 7 real-time hours / 21 in-game days
    private static final int DEFAULT_WEATHERING_TICK_TO = 552000;   // 7h40m real time / 23 in-game days
    private static final boolean DEFAULT_END_FLASH_ENABLED = true;
    
    // Runtime values
    private static boolean golemBuildSpawning = DEFAULT_GOLEM_BUILD_SPAWNING;
    private static int golemTransportStackSize = DEFAULT_GOLEM_TRANSPORT_STACK_SIZE;
    private static int weatheringTickFrom = DEFAULT_WEATHERING_TICK_FROM;
    private static int weatheringTickTo = DEFAULT_WEATHERING_TICK_TO;
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
     * Can Copper Golems be spawned by building with copper block + carved pumpkin?
     * Default: true
     */
    public static boolean golemBuildSpawning() {
        return golemBuildSpawning;
    }

    public static void setGolemBuildSpawning(boolean value) {
        golemBuildSpawning = value;
    }

    /**
     * Number of items a Copper Golem can transport at once (1-64).
     * Default: 16
     */
    public static int golemTransportStackSize() {
        return golemTransportStackSize;
    }

    public static void setGolemTransportStackSize(int value) {
        golemTransportStackSize = clamp(value, 1, 64);
    }

    /**
     * Minimum time in ticks until the Copper Golem starts weathering to the next oxidation level.
     * Default: 504000 (7 real-time hours / 21 in-game days).
     *
     * Lowering the configured range also shortens an existing unwaxed golem's
     * outstanding timer on its next server tick.
     */
    public static int weatheringTickFrom() {
        return weatheringTickFrom;
    }

    public static void setWeatheringTickFrom(int value) {
        weatheringTickFrom = Math.max(0, value);
    }

    /**
     * Maximum time in ticks until the Copper Golem weathers to the next oxidation level.
     * Default: 552000 (7h40m real time / 23 in-game days).
     *
     * Lowering the configured range also shortens an existing unwaxed golem's
     * outstanding timer on its next server tick.
     */
    public static int weatheringTickTo() {
        return weatheringTickTo;
    }

    public static void setWeatheringTickTo(int value) {
        weatheringTickTo = Math.max(weatheringTickFrom, value);
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

            if (json.has(KEY_GOLEM_BUILD_SPAWNING)) {
                golemBuildSpawning = json.get(KEY_GOLEM_BUILD_SPAWNING).getAsBoolean();
            }
            if (json.has(KEY_GOLEM_TRANSPORT_STACK_SIZE)) {
                golemTransportStackSize = clamp(json.get(KEY_GOLEM_TRANSPORT_STACK_SIZE).getAsInt(), 1, 64);
            }
            if (json.has(KEY_WEATHERING_TICK_FROM)) {
                weatheringTickFrom = Math.max(0, json.get(KEY_WEATHERING_TICK_FROM).getAsInt());
            }
            if (json.has(KEY_WEATHERING_TICK_TO)) {
                weatheringTickTo = Math.max(weatheringTickFrom, json.get(KEY_WEATHERING_TICK_TO).getAsInt());
            }
            if (json.has(KEY_END_FLASH_ENABLED)) {
                endFlashEnabled = json.get(KEY_END_FLASH_ENABLED).getAsBoolean();
            }

            Constants.LOG.info("Loaded config: golemBuildSpawning={}, golemTransportStackSize={}, weatheringTickFrom={}, weatheringTickTo={}, endFlashEnabled={}",
                golemBuildSpawning, golemTransportStackSize, weatheringTickFrom, weatheringTickTo, endFlashEnabled);
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
        json.addProperty(KEY_GOLEM_BUILD_SPAWNING, golemBuildSpawning);
        json.addProperty(KEY_GOLEM_TRANSPORT_STACK_SIZE, golemTransportStackSize);
        json.addProperty(KEY_WEATHERING_TICK_FROM, weatheringTickFrom);
        json.addProperty(KEY_WEATHERING_TICK_TO, weatheringTickTo);
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

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
