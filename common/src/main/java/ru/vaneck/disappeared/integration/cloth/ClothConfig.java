package ru.vaneck.disappeared.integration.cloth;

import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.platform.Services;

public class ClothConfig {
    public static boolean isClothLoaded() {
        if (
                Services.PLATFORM.isModLoaded("cloth_config") ||
                        Services.PLATFORM.isModLoaded("cloth-config") ||
                        Services.PLATFORM.isModLoaded("cloth-config2")
        ) {
            try {
                Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
                return true;
            } catch (Exception e) {
                DisappearedConstants.LOG.warn("Failed to open Cloth config screen.");
                return false;
            }
        }
        return false;
    }
}