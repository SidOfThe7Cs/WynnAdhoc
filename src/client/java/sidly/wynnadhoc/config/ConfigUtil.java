// yoinked from https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/src/main/java/io/github/moulberry/notenoughupdates/core/config/ConfigUtil.java

package sidly.wynnadhoc.config;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.utils.Debug;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static sidly.wynnadhoc.config.ConfigManager.GSON;

public class ConfigUtil {
    private static final Set<Class<?>> brokenClasses = new HashSet<>();

    public static <T> @Nullable T loadConfig(Class<T> configClass, File file, Gson gson) {
        return loadConfig(configClass, file, gson, false);
    }

    public static <T> @Nullable T loadConfig(Class<T> configClass, File file, Gson gson, boolean useGzip) {
        return loadConfig(configClass, file, gson, useGzip, true);
    }

    public static <T> @Nullable T loadConfig(
            Class<T> configClass,
            File file,
            Gson gson,
            boolean useGzip,
            boolean handleError
    ) {
        if (!file.exists()) return null;
        try (
                BufferedReader reader = useGzip ?
                        new BufferedReader(new InputStreamReader(
                                new GZIPInputStream(Files.newInputStream(file.toPath())),
                                StandardCharsets.UTF_8
                        )) :
                        new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))
        ) {
            return gson.fromJson(reader, configClass);
        } catch (JsonSyntaxException e) {
            brokenClasses.add(configClass);
            // JSON structure error (wrong types, missing fields, etc.)
            WynnAdhocClient.LOGGER.error("JSON Syntax Error in " + file.getName() + ": " + e.getMessage());

            // Get more details about the error location
            Throwable cause = e.getCause();
            if (cause != null) {
                WynnAdhocClient.LOGGER.error("Cause: " + cause.getMessage());
            }

            // Try to find line/position from the error message
            String errorMsg = e.getMessage();
            // Example: "Expected BEGIN_OBJECT but was STRING at line 5 column 12"
            if (errorMsg.contains("line") && errorMsg.contains("column")) {
                WynnAdhocClient.LOGGER.error("Error location: " + errorMsg);
            }

            if (!handleError) return null;
            e.printStackTrace();
            makeBackup(file, ".corrupted");

        } catch (JsonParseException e) {
            brokenClasses.add(configClass);
            // Generic JSON parsing error
            WynnAdhocClient.LOGGER.error("JSON Parse Error in " + file.getName() + ": " + e.getMessage());
            if (!handleError) return null;
            e.printStackTrace();
            makeBackup(file, ".corrupted");

        } catch (IOException e) {
            brokenClasses.add(configClass);
            // File reading error
            WynnAdhocClient.LOGGER.error("IO Error reading " + file.getName() + ": " + e.getMessage());
            if (!handleError) return null;
            e.printStackTrace();

        } catch (Exception e) {
            brokenClasses.add(configClass);
            // Any other error
            WynnAdhocClient.LOGGER.error("Unexpected error loading " + file.getName() + ": " + e.getMessage());
            if (!handleError) return null;
            e.printStackTrace();
            makeBackup(file, ".corrupted");
        }
        return null;
    }

    private static final List<String> unimportantConfigs = List.of();

    public static void saveConfig(Object config, File file) {
        saveConfig(config, file, GSON, false);
    }

    public static void saveConfig(Object config, File file, Gson gson) {
        saveConfig(config, file, gson, false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveConfig(Object config, File file, Gson gson, boolean useGzip) {
        if (brokenClasses.contains(config.getClass())) return;
        File tempFile = new File(file.getParent(), file.getName() + "-" + System.currentTimeMillis() + ".temp");
        try {
            tempFile.createNewFile();
            try (
                    BufferedWriter writer = useGzip ?
                            new BufferedWriter(new OutputStreamWriter(
                                    new GZIPOutputStream(Files.newOutputStream(tempFile.toPath())),
                                    StandardCharsets.UTF_8
                            )) :
                            new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile.toPath()), StandardCharsets.UTF_8))
            ) {
                String saving = gson.toJson(config);
                writer.write(saving);
            }

            if (loadConfig(config.getClass(), tempFile, gson, useGzip, false) == null) {
                WynnAdhocClient.LOGGER.error("Config verification failed for " + tempFile + ", could not save config properly.");
                if (!unimportantConfigs.contains(tempFile.getName())) makeBackup(tempFile, ".backup");
                return;
            }

            try {
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                // If atomic move fails it could be because it isn't supported or because the implementation of it
                // doesn't overwrite the old file, in this case we will try a normal move.
                WynnAdhocClient.LOGGER.warn("Atomic move failed attempting normal");
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            if (!unimportantConfigs.contains(tempFile.getName())) makeBackup(tempFile, ".backup");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void makeBackup(File file, String suffix) {
        File backupFile = new File(file.getParent(), file.getName() + "-" + System.currentTimeMillis() + suffix);
        WynnAdhocClient.LOGGER.info(Debug.Type.CONFIG, "trying to make backup: " + backupFile.getName());

        try {
            Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception __) {
                WynnAdhocClient.LOGGER.error("WynnAdhoc file gone " + file.getName());
            }
        } finally {
            file.delete();
        }
    }
}