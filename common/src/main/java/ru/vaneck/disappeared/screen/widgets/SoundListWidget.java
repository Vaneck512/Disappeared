package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SoundListWidget extends ContainerObjectSelectionList<SoundListWidget.SoundListEntry> {
    public SoundListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
    }

    public static class SoundListEntry extends ContainerObjectSelectionList.Entry<SoundListEntry> {

        private final SoundListWidget parent;

        private int x;
        private int y;
        private int width;
        private int height;

        public SoundListEntry(int x, int y, SoundListWidget parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return parent.children().size()*this.height;
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, boolean b, float v) {
            guiGraphicsExtractor.text(Minecraft.getInstance().font, "СТРОКА ЬАЬАЬАЬЬ", 1 ,1, 0xFFFFFFFF);
        }

        @Override
        public @NonNull List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return List.of();
        }
    }
}
