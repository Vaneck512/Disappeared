package ru.vaneck.disappeared;

import ru.vaneck.disappeared.files.SoundInstanceRecord;
import ru.vaneck.disappeared.files.SoundResourcesCache;
import ru.vaneck.disappeared.files.Utils;
import ru.vaneck.disappeared.platform.Services;
import ru.vaneck.sap.lib.SAP;
import ru.vaneck.sap.lib.sound.AudioType;

import java.util.concurrent.ThreadLocalRandom;

public class DisappearedCommon {

    public static void init() {
        if (Services.PLATFORM.isModLoaded("disappeared")) {
            DisappearedConstants.LOG.info("Disappeared is loaded.");
        }
        SoundResourcesCache.init();

        Utils.runOnResourceManagerLoaded(() -> {
            SoundInstanceRecord s = SoundResourcesCache.getActiveSounds().get(ThreadLocalRandom.current().nextInt(SoundResourcesCache.getActiveSounds().size()));
            DisappearedConstants.LOG.info("Playing " + s.fileName);
            SAP.play(s.path, DisappearedConstants.IDENTIFIER_PATTERN.matcher(s.path).find() ? AudioType.LOCATION : AudioType.FILE, 0.25f);
            DisappearedConstants.LOG.info(SoundResourcesCache.ALL_SOUNDS.toString());
            DisappearedConstants.LOG.info(SoundResourcesCache.ALL_SOUNDS.stream().toList().get(0).toString());
        });
    }

}