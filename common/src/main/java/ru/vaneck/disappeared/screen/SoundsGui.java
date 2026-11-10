package ru.vaneck.disappeared.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.vaneck.disappeared.screen.widgets.SoundListWidget;

public class SoundsGui extends Screen {
    private SoundListWidget soundListWidget;
    public SoundsGui() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        this.soundListWidget = addRenderableWidget(new SoundListWidget(Minecraft.getInstance(), 195, 120, 50, 25));

        soundListWidget.add();
        soundListWidget.add();
//        soundListWidget.add();
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0, 0, this));
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0,0, this));
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0,0, this));
    }
}
