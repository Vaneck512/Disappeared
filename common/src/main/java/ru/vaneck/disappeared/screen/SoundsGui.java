package ru.vaneck.disappeared.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.screen.widgets.SoundListWidget;

public class SoundsGui extends Screen {
    private SoundListWidget soundListWidget;

    private Identifier TEXTURE = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/sounds_gui.png");

    private int x, y;

    public SoundsGui() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        this.soundListWidget = addRenderableWidget(new SoundListWidget(Minecraft.getInstance(), width/2 - 235/2 + 9, 235, 120, 47, 45));

        soundListWidget.add();
//        soundListWidget.add();
//        soundListWidget.add();
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0, 0, this));
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0,0, this));
//        soundListWidget.addEntry(new SoundListWidget.SoundListEntry(0,0, this));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        x = width/2 - 235/2;
        y = 50;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y-20, 0, 0, 256, 90, 256, 256);
    }
}
