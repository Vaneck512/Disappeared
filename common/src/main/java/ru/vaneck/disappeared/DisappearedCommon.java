package ru.vaneck.disappeared;

import ru.vaneck.disappeared.platform.Services;

public class DisappearedCommon {

    public static void init() {
        if (Services.PLATFORM.isModLoaded("disappeared")) {
            DisappearedConstants.LOG.info("Disappeared is loaded.");
        }
    }

}