package ru.vaneck.disappeared;

import ru.vaneck.disappeared.platform.Services;

public class CommonClass {

    public static void init() {
        if (Services.PLATFORM.isModLoaded("disappeared")) {
            Constants.LOG.info("Disappeared is loaded.");
        }
    }
}