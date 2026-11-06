package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class IconButtonWidget extends Button {
    public final Identifier ICON;

    public IconButtonWidget(int x, int y, int width, int height, Identifier icon, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, _->Component.empty());
        this.ICON = icon;
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
        this.extractDefaultSprite(guiGraphicsExtractor);
        this.extractDefaultLabel(guiGraphicsExtractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        int x = getX()+width/2 - 8;
        int y = getY()+height/2 - 8;
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, ICON, x, y, 0, 0, 16, 16, 16, 16);
    }

    public IconButtonWidget withTooltip(Tooltip tooltip) {
        this.setTooltip(tooltip);
        return this;
    }
}
