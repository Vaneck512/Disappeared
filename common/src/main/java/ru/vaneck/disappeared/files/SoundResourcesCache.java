package ru.vaneck.disappeared.files;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import ru.vaneck.disappeared.DisappearedConstants;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class SoundResourcesCache {
    public static final Path SOUND_CACHE_FILE = DisappearedConstants.MAIN_WORK_DIRECTORY.get().resolve("cache").resolve("sound_cache.json");
    private static final Path EXTERNAL_SOUNDS_DIR = DisappearedConstants.MAIN_WORK_DIRECTORY.get().resolve("sounds");
    private static final List<String> EXTENSIONS = List.of(".ogg", ".mp3", ".wav");
    private static final Float DEFAULT_VOLUME = 0.5F;

    public static Set<SoundInstanceRecord> ALL_SOUNDS = ConcurrentHashMap.newKeySet();

    public static List<SoundInstanceRecord> getActiveSounds() {
        return ALL_SOUNDS.stream().filter(soundInstanceRecord -> soundInstanceRecord.active).toList();
    }

    public static void load() throws IOException {
        List<SoundInstanceRecord> collectedSounds = collectALlSounds();

        Type setType = new TypeToken<Set<SoundInstanceRecord>>(){}.getType();
        Gson gson = new Gson();

        try(BufferedReader reader = Files.newBufferedReader(SOUND_CACHE_FILE)) {
            Set<SoundInstanceRecord> readedSet = gson.fromJson(reader, setType);

            ALL_SOUNDS = ConcurrentHashMap.newKeySet();
            if (readedSet != null)
            {
                readedSet.removeIf(record -> {
                    if (DisappearedConstants.IDENTIFIER_PATTERN.matcher(record.path).find()) {
                        return !Minecraft.getInstance().getResourceManager().getResource(Identifier.parse(record.path)).isPresent();
                    } else return !Files.exists(Path.of(record.path));
                });
                ALL_SOUNDS.addAll(readedSet);
            }
        }

        ALL_SOUNDS.addAll(collectedSounds);
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(SOUND_CACHE_FILE.toFile())) {
            new Gson().toJson(ALL_SOUNDS, writer);
        } catch (IOException e) {
            DisappearedConstants.LOG.error(e.getLocalizedMessage(), e);
        }
    }

    public static void init() {
        Utils.runOnResourceManagerLoaded(() -> {
            try {
                if (!Files.exists(SOUND_CACHE_FILE)) {
                    Files.createDirectories(SOUND_CACHE_FILE.getParent());
                    Files.createFile(SOUND_CACHE_FILE);
                }
                if (!Files.exists(EXTERNAL_SOUNDS_DIR))
                    Files.createDirectories(EXTERNAL_SOUNDS_DIR);
                load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            save();
            watchDir();
        });
    }

    private static List<SoundInstanceRecord> collectALlSounds() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        List<SoundInstanceRecord> export = new ArrayList<>();

        // From resourcepacks
        Map<Identifier, Resource> disappearedResources = resourceManager.listResources(
                "sounds",
                res -> res.getNamespace().equals(DisappearedConstants.MOD_ID)
                        && EXTENSIONS.stream().anyMatch(ext -> res.getPath().endsWith(ext))
        );

        for (Identifier soundLocation: disappearedResources.keySet()) {
            String soundPath = soundLocation.toString();
            if (EXTENSIONS.stream().anyMatch(soundPath::endsWith)) {
                export.add(
                        new SoundInstanceRecord(
                                soundPath,
                                soundPath.substring(soundPath.lastIndexOf('/')+1),
                                DEFAULT_VOLUME,
                                true
                        )
                );
            }
        }

        // From directory
        if (Files.exists(EXTERNAL_SOUNDS_DIR) && Files.isDirectory(EXTERNAL_SOUNDS_DIR)) {
            try (Stream<Path> walk = Files.walk(EXTERNAL_SOUNDS_DIR)) {
                walk.filter(Files::isRegularFile).forEach(filePath -> {
                    if (EXTENSIONS.stream().anyMatch(filePath.getFileName().toString().toLowerCase()::endsWith)) {
                        export.add(
                                new SoundInstanceRecord(
                                        filePath.toString(),
                                        filePath.getFileName().toString(),
                                        DEFAULT_VOLUME,
                                        true
                                )
                        );
                    }
                });
            } catch (IOException e) {
                DisappearedConstants.LOG.error("Error scanning sounds from external directory: {}", e.getLocalizedMessage(), e);
            }
        }
        return export;
    }

    private static AtomicBoolean IS_WATCHING = new AtomicBoolean(false);
    public static void watchDir() {
        if (!IS_WATCHING.compareAndSet(false, true)) return;

        List<WatchEvent.Kind<?>> kinds = List.of(
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );

        Thread thread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                EXTERNAL_SOUNDS_DIR.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE
                );

                while (true) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        return;
                    }
                    boolean needToLoad = false;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (kinds.contains(event.kind())) {
                            needToLoad = true;
                        }
                    }

                    if (needToLoad) {
                        load();
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IS_WATCHING.set(false);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
