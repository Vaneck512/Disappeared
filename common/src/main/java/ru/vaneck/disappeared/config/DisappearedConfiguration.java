package ru.vaneck.disappeared.config;

import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.config.model.VConfig;
import ru.vaneck.disappeared.config.model.VConfigEntry;

public class DisappearedConfiguration extends VConfig {

    public static final DisappearedConfiguration CONFIGURATION = new DisappearedConfiguration();

    static {
        CONFIGURATION.load();
    }

    {
        setHeader("Client configuration for Disappeared mod \\nF3+T to reload in-game");
        setFilePath(DisappearedConstants.MAIN_WORK_DIRECTORY.get().resolve("configuration.properties"));
    }

    public final VConfigEntry<Boolean> TEST = addEntry("test", true).addDescLine("Test configuration entry");

    public final VConfigEntry<Long> WORK_RADIUS =
            this.addEntry("radius", -1L, -1L, 1000000000L)
                    .addDescLine("Enable all mod functions");

    public final VConfigEntry<Boolean> ENABLE_MOD =
            this.addEntry("enable_mod", true)
                    .addDescLine("Enable all mod functions");

    public final VConfigEntry<Boolean> ENABLE_SOUNDS =
            this.addEntry("enable_sounds", true)
                    .addDescLine("Play sounds when the entity disappears.");

    public final VConfigEntry<Integer> MASTER_VOLUME =
            this.addEntry("master_volume", 25, 0, 100)
                    .addDescLine("Each sound can be set on a individual volume level.")
                    .addDescLine("So, you can change master volume for them");

    public final VConfigEntry<Boolean> SHOW_GHOSTS =
            this.addEntry("show_ghosts", true)
                    .addDescLine("Toggle disappearance animation.");

    public final VConfigEntry<Long> DISAPPEAR_ANIMATION_DURATION =
            this.addEntry("disappear_animation_duration", 450L, 1, 1_000_000)
                    .addDescLine("Time in millis for the entity to disappear.");

    public final VConfigEntry<Long> APPEAR_ANIMATION_DURATION =
            this.addEntry("appear_animation_duration", 500L, 1, 1_000_000)
                    .addDescLine("Time in millis for the entity to appear.");

    public final VConfigEntry<Boolean> ONLY_PLAYERS =
            this.addEntry("only_players", false)
                    .addDescLine("If disabled - mod works with mobs");

    public final VConfigEntry<Boolean> PLAY_ANIMATION_AFTER_DEATH =
            this.addEntry("after_death", true)
                    .addDescLine("Disappearing animation after entity death (without sound)");

    public final VConfigEntry<Integer> MAX_GHOSTS =
            this.addEntry("max_ghosts", -1, -1, 100000)
                    .addDescLine("Count of ghosts that can be added at the same time")
                    .addDescLine("Set '-1' to Unlimited");
}
