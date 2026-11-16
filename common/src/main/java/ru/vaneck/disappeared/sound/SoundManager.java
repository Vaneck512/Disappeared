package ru.vaneck.disappeared.sound;

import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.sap.lib.SAP;
import ru.vaneck.sap.lib.sound.AudioType;
import ru.vaneck.sap.lib.sound.model.AudioInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class SoundManager {
    private static final List<AudioInstance> ACTIVE_SOUND_SOURCES = new CopyOnWriteArrayList<>();

     public static void playSound(String path, Consumer<AudioInstance> settings, boolean positional) {
         AudioType type = DisappearedConstants.IDENTIFIER_PATTERN.matcher(path).find() ? AudioType.LOCATION : AudioType.FILE;
         SAP.create(path, type, positional, 0.045F).thenAccept(soundInstance -> {
             settings.accept(soundInstance);
             ACTIVE_SOUND_SOURCES.add(soundInstance);
             soundInstance.play();
         });
     }

     public static void playSound(String path, Consumer<AudioInstance> settings) {
         playSound(path, settings, false);
     }

     public static void update() {
         ACTIVE_SOUND_SOURCES.removeIf(soundInstance -> {
             if (!soundInstance.isStarted()) return false;
             if (!soundInstance.isPlaying() && !soundInstance.isPaused()) {
                 if (!soundInstance.isClosed()) {
                     try {
                         soundInstance.close();
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                     return true;
                 }
             }
             return false;
         });
     }

     public static void stopAll() {
         ACTIVE_SOUND_SOURCES.removeIf(instance -> {
             try {
                 instance.close();
                 return true;
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         });
     }
}
