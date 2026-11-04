package ru.vaneck.disappeared.config;

import ru.vaneck.disappeared.Constants;
import ru.vaneck.disappeared.config.model.VConfig;
import ru.vaneck.disappeared.config.model.VConfigEntry;

public class DisappearedConfiguration extends VConfig {

    public static final DisappearedConfiguration CONFIGURATION = new DisappearedConfiguration();

    static {
        CONFIGURATION.load();
    }

    {
        setHeader("Client configuration for Disappeared mod \\nF3+T to reload in-game");
        setFilePath(Constants.MAIN_WORK_DIRECTORY.resolve("configuration.properties"));
    }

    public final VConfigEntry<Boolean> TEST = addEntry("test", true).addDescLine("Test configuration entry");
}
