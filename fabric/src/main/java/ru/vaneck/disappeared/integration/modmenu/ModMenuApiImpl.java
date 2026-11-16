package ru.vaneck.disappeared.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import ru.vaneck.disappeared.integration.cloth.ClothConfig;
import ru.vaneck.disappeared.integration.cloth.ClothConfigHelper;
import ru.vaneck.disappeared.screen.SettingsGui;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (ClothConfig.isClothLoaded())
                return ClothConfigHelper.createScreen(parent);
            return new SettingsGui(parent, true);
        };
    }
}