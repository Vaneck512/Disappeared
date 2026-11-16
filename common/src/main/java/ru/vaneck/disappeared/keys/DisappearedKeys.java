package ru.vaneck.disappeared.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.animation.GhostEffect;
import ru.vaneck.disappeared.config.DisappearedConfiguration;
import ru.vaneck.disappeared.platform.Services;
import ru.vaneck.disappeared.screen.MainGui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DisappearedKeys {

    private static final DisappearedConfiguration configuration = DisappearedConfiguration.CONFIGURATION;

    public static List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();

    public static final KeyMapping.Category DISAPPEARED_CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "disappeared_category"));

    public static final KeyMapping DISAPPEARED = addMapping(new KeyMapping(
            "disappeared.keys.disappeared",
            InputConstants.KEY_I,
            DISAPPEARED_CATEGORY
    ));

    public static final KeyMapping TOGGLE_GHOST = addMapping(new KeyMapping(
            "disappeared.keys.toggle_animation",
            InputConstants.UNKNOWN.getValue(),
            DISAPPEARED_CATEGORY
    ));

    public static final KeyMapping TOGGLE_SOUND = addMapping(new KeyMapping(
            "disappeared.keys.toggle_sound",
            InputConstants.UNKNOWN.getValue(),
            DISAPPEARED_CATEGORY
    ));

    public static final KeyMapping TOGGLE_ALL = addMapping(new KeyMapping(
            "disappeared.keys.toggle_mod",
            InputConstants.KEY_C,
            DISAPPEARED_CATEGORY
    ));

    private static KeyMapping addMapping(KeyMapping keyMapping) {
        KEY_MAPPINGS.add(keyMapping);
        return keyMapping;
    }

    private static KeyMapping DEBUG_SPAWN_SELF_GHOST = null;


    public static void initDebugKeys() {
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            DEBUG_SPAWN_SELF_GHOST = addMapping(
                    new KeyMapping(
                            "spawn_ghost",
                           InputConstants.KEY_F7,
                            DISAPPEARED_CATEGORY
                    )
            );
        }
    }

    public static void handleKeys() {
        Minecraft client = Minecraft.getInstance();
        Player clientPlayer = Minecraft.getInstance().player;

        if (clientPlayer == null) return;

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            if (DEBUG_SPAWN_SELF_GHOST != null && DEBUG_SPAWN_SELF_GHOST.consumeClick()) {
                GhostEffect.submitGhost(clientPlayer, 1512993);
            }
        }

        if (DISAPPEARED.consumeClick()) {
            client.gui.setScreen(new MainGui());
        }
        if (TOGGLE_SOUND.consumeClick()) {
           configuration.ENABLE_SOUNDS.setValue(!configuration.ENABLE_SOUNDS.getValue());
            clientPlayer.sendOverlayMessage(
                    Component.translatable("disappeared.gui_components.sound_text").append(": ")
                            .append(
                                    configuration.ENABLE_SOUNDS.getValue() ?
                                            Component.translatable("disappeared.gui_components.state_on").withColor(Color.GREEN.getRGB())
                                            : Component.translatable("disappeared.gui_components.state_off").withColor(Color.RED.getRGB())
                            )
            );
        }
        if (TOGGLE_GHOST.consumeClick()) {
            configuration.SHOW_GHOSTS.setValue(!configuration.SHOW_GHOSTS.getValue());
            clientPlayer.sendOverlayMessage(
                    Component.translatable("disappeared.gui_components.animation_text").append(": ")
                            .append(
                                    configuration.SHOW_GHOSTS.getValue() ?
                                            Component.translatable("disappeared.gui_components.state_on").withColor(Color.GREEN.getRGB())
                                            : Component.translatable("disappeared.gui_components.state_off").withColor(Color.RED.getRGB())
                            )
            );
        }
        if (TOGGLE_ALL.consumeClick() && client.hasAltDown()) { // Alt + C
            configuration.ENABLE_MOD.setValue(!configuration.ENABLE_MOD.getValue());
            clientPlayer.sendOverlayMessage(
                    Component.literal("Disappeared").append(": ")
                            .append(
                                    configuration.ENABLE_MOD.getValue() ?
                                            Component.translatable("disappeared.gui_components.state_on").withColor(Color.GREEN.getRGB())
                                            : Component.translatable("disappeared.gui_components.state_off").withColor(Color.RED.getRGB())
                            )
            );
            if (!configuration.ENABLE_MOD.getValue()) {
                GhostEffect.GHOSTS_TO_RENDER.clear();
                GhostEffect.REVERSED_GHOSTS_TO_RENDER.clear();
            }
        }
    }
}
