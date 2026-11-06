package ru.vaneck.disappeared.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.vaneck.disappeared.screen.widgets.SoundListWidget;

public class SoundsGui extends Screen {
    private final SoundListWidget soundListWidget;
    public SoundsGui() {
        super(Component.empty());
        this.soundListWidget = addRenderableWidget(new SoundListWidget(Minecraft.getInstance(), 720, 480, 1, 160));
    }

    @Override
    protected void init() {
        super.init();
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0, 0, this));
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0,0, this));
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0,0, this));
    }
}
