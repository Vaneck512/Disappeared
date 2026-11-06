package ru.vaneck.disappeared.screen;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;
import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.config.DisappearedConfiguration;
import ru.vaneck.disappeared.keys.DisappearedKeys;
import ru.vaneck.disappeared.screen.widgets.IconButtonCFGWidget;
import ru.vaneck.disappeared.screen.widgets.IconButtonWidget;

public class MainGui extends Screen {
    private int guiX;
    private int guiY;

    private final int texWidth = 195;
    private final int texHeight = 87;

    private final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/main_screen.png");
    private final Identifier SOUND_ICON = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/icon/sound_icon.png");
    private final Identifier GHOST_ICON = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/icon/ghost_icon.png");
    private final Identifier FOLDER_ICON = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/icon/folder_icon.png");
    private final Identifier TOOLTIP_ICON = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/icon/warning.png");
    private final Identifier CFG_FILE_ICON = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/icon/cfg_file_icon.png");

    private final Component TITLE = Component.literal("Disappeared");
    private final Component SETTINGS = Component.translatable("disappeared.gui_components.settings_text");
    private final Component SOUNDS = Component.translatable("disappeared.gui_components.sounds_text");

    public static final DisappearedConfiguration CONFIGURATION = DisappearedConfiguration.CONFIGURATION;
    
    private final int FONT_COLOR = 0xFF404040;

    public MainGui() {
        super(Component.literal("Disappeared"));
    }

    @Override
    protected void init() {
        super.init();
        guiX = width/2-(texWidth/2);
        guiY = height/2-(texHeight/2);

        addRenderableWidget(Button.builder(SETTINGS, button -> Minecraft.getInstance().gui.setScreen(new SettingsGui()))
                .width(75)
                .pos(guiX+10, guiY+30)
                .build()
        );

        SpriteIconButton soundsScreenButton = SpriteIconButton.builder(SOUNDS, button -> Minecraft.getInstance().gui.setScreen(new SoundsGui()), false)
                .width(79)
                .sprite(Identifier.withDefaultNamespace("icon/music_notes"), 16, 16)
                .build();
        soundsScreenButton.setPosition(guiX+105, guiY+30);
        addRenderableWidget(soundsScreenButton);

        addRenderableWidget(new IconButtonCFGWidget(guiX+10, guiY+57, 20, 20, SOUND_ICON, CONFIGURATION.ENABLE_SOUNDS)
                .withTooltip(Component.translatable("disappeared.gui_components.sound_text")));

        addRenderableWidget(new IconButtonCFGWidget(guiX+35, guiY+57, 20, 20, GHOST_ICON, CONFIGURATION.SHOW_GHOSTS)
                .withTooltip(Component.translatable("disappeared.gui_components.animation_text")));

        addRenderableWidget(
                new IconButtonWidget(guiX+164, guiY+57, 20, 20, FOLDER_ICON,
                     _ -> {
                        Blaze3D.openPath(DisappearedConstants.MAIN_WORK_DIRECTORY.get());
                     }
                ).withTooltip(Tooltip.create(Component.translatable("disappeared.gui_components.mod_folder_text")))
        );

        addRenderableWidget(
                new IconButtonWidget(guiX+139, guiY+57, 20, 20, CFG_FILE_ICON,
                     _ -> {
                        Blaze3D.openPath(CONFIGURATION.getFilePath());
                     }
                ).withTooltip(Tooltip.create(Component.translatable("disappeared.gui_components.cfg_file_text")))
        );
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.hasAltDown() && DisappearedKeys.TOGGLE_ALL.matches(event)) {
            CONFIGURATION.ENABLE_MOD.setValue(!CONFIGURATION.ENABLE_MOD.getValue());
            children().stream()
                    .filter(
                            guiEventListener -> guiEventListener instanceof IconButtonCFGWidget
                    )
                    .forEach(
                            iconCFGButton ->
                                    ((IconButtonCFGWidget)iconCFGButton)
                                    .withTooltip(
                                            ((IconButtonCFGWidget)iconCFGButton)
                                                    .getDefaultTooltipMessage()
                                    )
                    );
        }

        return super.keyPressed(event);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX, guiY, 0, 0, texWidth, texHeight, 256, 256);
        graphics.text(font, TITLE, guiX + (texWidth/2) - font.width(TITLE)/2, guiY+10, FONT_COLOR, false);

        int tooltipX = guiX+173;
        int tooltipY = guiY+5;
        if (mouseX >= tooltipX && mouseY >= tooltipY && mouseX <= tooltipX+16 && mouseY <= tooltipY+16) {
            Component text = CONFIGURATION.ENABLE_MOD.getValue()
                    ? Component.translatable("disappeared.gui_components.tooltip.disable_mod_hotkey")
                    : Component.translatable("disappeared.gui_components.tooltip.enable_mod_hotkey");

            Component tooltipMessage = Component.nullToEmpty(text
                            .getString().formatted(DisappearedKeys.TOGGLE_ALL.getTranslatedKeyMessage().getString()));

            graphics.setTooltipForNextFrame(tooltipMessage, mouseX, mouseY);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TOOLTIP_ICON, tooltipX, tooltipY, 0, 0, 16, 16, 16, 16);
    }
}
