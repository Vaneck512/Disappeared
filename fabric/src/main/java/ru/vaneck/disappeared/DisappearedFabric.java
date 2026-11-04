package ru.vaneck.disappeared;

import net.fabricmc.api.ModInitializer;

public class DisappearedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DisappearedCommon.init();
    }
}
