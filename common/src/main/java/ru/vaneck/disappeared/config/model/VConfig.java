package ru.vaneck.disappeared.config.model;

import ru.vaneck.disappeared.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class VConfig {
    private final List<VConfigEntry<?>> ENTRIES = new ArrayList<>();
    private String header = null;

    private Path filePath = Constants.MAIN_WORK_DIRECTORY.resolve("configuration.properties");

    private <T extends Comparable<T>> VConfigEntry<T> createEntry(String key, T value) {
        VConfigEntry<T> configEntry = new VConfigEntry<>(key, value);
        ENTRIES.add(configEntry);
        configEntry.setConfig(this);
        return  configEntry;
    }
    private <T extends Comparable<T>> VConfigEntry<T> createBoundedEntry(String key, T value, T minValue, T maxValue) {
        VConfigEntry<T> configEntry = new VConfigEntry<>(key, value, minValue, maxValue);
        ENTRIES.add(configEntry);
        configEntry.setConfig(this);
        return  configEntry;
    }
    
    protected VConfigEntry<Boolean> addEntry(String key, boolean defaultValue) {
        return createEntry(key, defaultValue);
    }
    protected VConfigEntry<Integer> addEntry(String key, int defaultValue, int minValue, int maxValue) {
        return createBoundedEntry(key, defaultValue, minValue, maxValue);
    }
    protected VConfigEntry<String> addEntry(String key, String defaultValue) {
        return createEntry(key, defaultValue);
    }
    protected VConfigEntry<Long> addEntry(String key, long defaultValue, long minValue, long maxValue) {
        return createBoundedEntry(key, defaultValue, minValue, maxValue);
    }
    protected VConfigEntry<Double> addEntry(String key, double defaultValue, double minValue, double maxValue) {
        return createBoundedEntry(key, defaultValue, minValue, maxValue);
    }

    protected void setHeader(String header) {
        this.header = header;
    }

    protected void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void save() {
        if (!Files.exists(filePath))
            try {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            } catch (IOException e) {
                Constants.LOG.error("Failed to create Disappeared config: {}", e.getLocalizedMessage(), e);
                return;
            }
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8
        )) {
            if (header != null) {
                String[] headerLines = header.split("\n");
                for (String headerLine : headerLines) {
                    writer.write("# " + headerLine);
                    if (!headerLines[headerLines.length-1].equals(headerLine))
                        writer.newLine();
                }
                writer.newLine();
                writer.newLine();
            }
            for (var entry : ENTRIES) {
                for (String line : entry.getDescription()) {
                    writer.write("# "+line);
                    writer.newLine();
                }
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to write Disappeared config: {}", e.getLocalizedMessage(), e);
        }
    }

    public void load() {
        if (!Files.exists(filePath)) save();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.strip().replace(" ", "");

                if (!trimmedLine.contains("=") || trimmedLine.startsWith("#")) {
                    continue;
                }

                String[] parts = trimmedLine.split("=", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();

                for (VConfigEntry<?> entry : ENTRIES) {
                    if (entry.getKey().equals(key)) {
                        entry.setValueFromString(value);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to read Disappeared config: {}", e.getLocalizedMessage(), e);
        }
    }
}
