package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.config.DisappearedConfiguration;
import ru.vaneck.disappeared.keys.DisappearedKeys;
import ru.vaneck.disappeared.config.model.VConfigEntry;

public class IconButtonCFGWidget extends Button {
    private final VConfigEntry<Boolean> vConfigEntry;
    private final Identifier icon;
    private final Identifier DISABLED_OVERLAY = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/icon/disabled_overlay.png");
    public static final DisappearedConfiguration CONFIGURATION = new DisappearedConfiguration();
    private Component defaultTooltipMessage;

    public IconButtonCFGWidget(int x, int y, int width, int height, Identifier icon, VConfigEntry<Boolean> configEntry) {
        super(x, y, width, height, Component.empty(), _->{}, _->Component.empty());
        this.vConfigEntry = configEntry;
        this.icon = icon;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        super.onPress(input);
        this.vConfigEntry.setValue(!this.vConfigEntry.getValue());
    }

    @Override
    public boolean isActive() {
        if (!CONFIGURATION.ENABLE_MOD.getValue())
            return false;
        return super.isActive();
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
        this.extractDefaultSprite(guiGraphicsExtractor);
        this.extractDefaultLabel(guiGraphicsExtractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        int x = getX()+width/2 - 8;
        int y = getY()+height/2 - 8;
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0, 0, 16, 16, 16, 16);
        if (!vConfigEntry.getValue() || !isActive())
            guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, DISABLED_OVERLAY, x, y, 0, 0, 16, 16, 16, 16);
    }

    public IconButtonCFGWidget withTooltip(Component tooltipMessage) {
        Component message;
        defaultTooltipMessage = tooltipMessage;
        if (!CONFIGURATION.ENABLE_MOD.getValue()) {
            Component key = DisappearedKeys.TOGGLE_ALL.getTranslatedKeyMessage();
            message = Component.nullToEmpty(
                    Component.translatable("disappeared.gui_components.tooltip.enable_mod_hotkey")
                            .getString().formatted(key.getString())
            );
        } else {
            message = tooltipMessage;
        }
        Tooltip tooltip = Tooltip.create(message);
        this.setTooltip(tooltip);
        return this;
    }

    public Component getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }
}
