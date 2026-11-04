package ru.vaneck.disappeared;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(DisappearedConstants.MOD_ID)
public class DisappearedNeoforge {

    public DisappearedNeoforge(IEventBus eventBus) {

        DisappearedConstants.LOG.info("Hello NeoForge world!");
        DisappearedCommon.init();

    }
}