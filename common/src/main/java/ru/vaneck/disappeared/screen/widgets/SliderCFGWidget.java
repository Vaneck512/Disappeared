package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.vaneck.disappeared.config.model.VConfigEntry;
import ru.vaneck.disappeared.files.SoundInstanceRecord;

public class SliderCFGWidget extends AbstractSliderButton {
    private final SoundInstanceRecord disappearSound;
    private final VConfigEntry<Integer> vConfigEntry;
    private final String message;
    public SliderCFGWidget(int x, int y, int width, int height, Component message, double initialValue, VConfigEntry<Integer> configEntry) {
        super(x, y, width, height, message, initialValue);
        value = (double) configEntry.getValue() /100;
        this.vConfigEntry = configEntry;
        this.message = message.getString();
        this.disappearSound = null;
        update();
    }
    public SliderCFGWidget(int x, int y, int width, int height, Component message, double initialValue, SoundInstanceRecord sound) {
        super(x, y, width, height, message, initialValue);
        value = sound.volume;
        this.vConfigEntry = null;
        this.disappearSound = sound;
        this.message = message.getString();
        update();
    }

    @Override
    protected void updateMessage() {

    }

    private void update() {
        setMessage(Component.literal(message + ": " +  Math.round(value*100)));
    }

    @Override
    protected void applyValue() {
        if (vConfigEntry != null) {
            double roundedValue = Math.round(value * 100.0);
            vConfigEntry.setValue((int) roundedValue);

            this.value = roundedValue / 100.0;

            update();
        } else if (disappearSound != null) {
            float roundedVolume = (float) (Math.round(value * 100.0) / 100.0);
            disappearSound.volume = roundedVolume;
            this.value = roundedVolume;

            update();
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (!this.active || !this.visible || !this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (!this.active || !this.visible || !this.isFocused()) {
            return false;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (!this.active || !this.visible || !this.isFocused()) {
            return false;
        }
        return super.mouseReleased(event);
    }

    @Nullable
    public SoundInstanceRecord getSound() {
        return this.disappearSound;
    }
}
