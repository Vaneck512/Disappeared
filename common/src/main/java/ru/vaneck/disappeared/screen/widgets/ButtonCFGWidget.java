package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import ru.vaneck.disappeared.config.model.VConfigEntry;

public class ButtonCFGWidget extends Button {
    private final VConfigEntry<Boolean> vConfigEntry;
    private final Component onMessage;
    private final Component offMessage;
    public ButtonCFGWidget(int x, int y, int width, int height, Component onMessage, Component offMessage, VConfigEntry<Boolean> configEntry) {
        super(x, y, width, height, onMessage, (_)->{}, (_)->Component.literal(onMessage.toString()));
        vConfigEntry = configEntry;
        this.onMessage = onMessage;
        this.offMessage = offMessage;
        update();
    }

    @Override
    public void onPress(InputWithModifiers input) {
        super.onPress(input);
        vConfigEntry.setValue(!vConfigEntry.getValue());
        update();
    }

    private void update() {
        setMessage(vConfigEntry.getValue() ? onMessage : offMessage);
    }

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
        this.extractDefaultSprite(guiGraphicsExtractor);
        this.extractDefaultLabel(guiGraphicsExtractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }
}
