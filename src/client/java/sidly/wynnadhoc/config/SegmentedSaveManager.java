package sidly.wynnadhoc.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SegmentedSaveManager {
    private static final Gson gson = new Gson();

    private final File folder;
    private final int maxEntriesPerFile;
    private final int maxFileSizeBytes; // optional
    private final File latestFile;

    public SegmentedSaveManager(File folder, int maxEntriesPerFile, int maxFileSizeBytes) {
        this.folder = folder;
        this.maxEntriesPerFile = maxEntriesPerFile;
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.latestFile = new File(folder, "latest.json");

        if (!folder.exists()) folder.mkdirs();
    }

    /** Loads the latest file into memory */
    public JsonArray loadLatest() {
        if (!latestFile.exists()) return new JsonArray();
        try (FileReader reader = new FileReader(latestFile)) {
            return JsonParser.parseReader(reader).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonArray();
        }
    }

    /** Saves entries, rolling over if needed */
    public boolean save(JsonArray data) {
        boolean rolledOver = false;
        try {
            // Check rollover conditions
            boolean shouldRollover = data.size() >= maxEntriesPerFile || latestFile.length() >= maxFileSizeBytes;

            if (shouldRollover) {
                archiveLatest();
                rolledOver = true;
                data = new JsonArray(); // start new segment
            }

            try (FileWriter writer = new FileWriter(latestFile)) {
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rolledOver;
    }

    /** Renames the latest file to a timestamped archive file */
    private void archiveLatest() {
        if (!latestFile.exists()) return;

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File archive = new File(folder, "data_" + timestamp + ".json");

        boolean renamed = latestFile.renameTo(archive);
        if (!renamed) {
            System.err.println("Failed to rename latest.json -> " + archive.getName());
        }
    }

    /** Iterates over all archived files (oldest to newest) */
    public List<File> listArchivedFiles() {
        File[] files = folder.listFiles((dir, name) ->
                name.startsWith("data_") && name.endsWith(".json"));

        if (files == null) return Collections.emptyList();

        Arrays.sort(files, Comparator.comparing(File::getName));
        return Arrays.asList(files);
    }

}
