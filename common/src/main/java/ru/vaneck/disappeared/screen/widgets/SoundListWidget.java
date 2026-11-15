package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.files.SoundInstanceRecord;
import ru.vaneck.disappeared.files.SoundResourcesCache;
import ru.vaneck.sap.lib.SAP;
import ru.vaneck.sap.lib.sound.AudioType;

import java.util.List;

public class SoundListWidget extends ContainerObjectSelectionList<SoundListWidget.SoundListEntry> {

    public SoundListWidget(Minecraft minecraft, int x, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.setX(x);

        SoundResourcesCache.ALL_SOUNDS.forEach(record -> addEntry(new SoundListEntry(record)));
    }

    @Override
    protected void extractListBackground(@NonNull GuiGraphicsExtractor graphics) {
    }

    @Override
    protected void extractListSeparators(@NonNull GuiGraphicsExtractor graphics) {
    }

    @Override
    public int scrollbarWidth() {
        return super.scrollbarWidth() + 6;
    }

    @Override
    protected int scrollBarX() {
        return super.scrollBarX() - 19;
    }

    @Override
    protected void extractScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.enableScissor(scrollBarX(), scrollBarY()+2, scrollBarX()+scrollbarWidth(), scrollBarY()+getHeight());
        super.extractScrollbar(graphics, mouseX, mouseY);
        graphics.disableScissor();
    }

    @Override
    public int getRight() {
        return super.getRight();
    }

    @Override
    public int getRowWidth() {
        return this.width - this.scrollbarWidth() - 4;
    }

    public boolean isScrollable() {
        return this.scrollable();
    }


    public static class SoundListEntry extends ContainerObjectSelectionList.Entry<SoundListEntry> {
        private static int count = 0;
        private final int id;
        private final SoundInstanceRecord soundInstanceRecord;
        private final SliderCFGWidget volumeSlider;
        private final IconButtonWidget playButton;
        private final Checkbox activeCheckbox;

        public SoundListEntry(SoundInstanceRecord record) {
            super();
            count++;
            this.id = count;
            this.soundInstanceRecord = record;

            this.activeCheckbox = Checkbox.builder(Component.empty(), Minecraft.getInstance().font)
                    .pos(0, 0)
                    .selected(soundInstanceRecord.active)
                    .onValueChange((_, b) -> soundInstanceRecord.active = b)
                    .build();

            this.playButton = new IconButtonWidget(0, 0, 17, 17,
                    Identifier.parse("disappeared:textures/gui/icon/play_icon.png"), _ -> SAP.play(
                            soundInstanceRecord.path,
                            DisappearedConstants.IDENTIFIER_PATTERN.matcher(soundInstanceRecord.path).find() ? AudioType.LOCATION : AudioType.FILE,
                            soundInstanceRecord.volume
                    )) {
                @Override
                protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
                    this.extractDefaultSprite(guiGraphicsExtractor);
                    this.extractDefaultLabel(guiGraphicsExtractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
                    int iconX = getX() + width / 2 - 6;
                    int iconY = getY() + height / 2 - 6;
                    guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, ICON, iconX, iconY, 0, 0, 13, 13, 13, 13);
                }
            };

            this.volumeSlider = new SliderCFGWidget(0, 0, 100, 20, Component.literal("Volume"), soundInstanceRecord.volume, soundInstanceRecord);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean b, float partialTick) {
            int left = getX();
            int top = getY();
            int right = left + getWidth();
            int bottom = top + getHeight();

            int color1 = 0xFF858282;
            int color2 = 0xFF999999;
            int color3 = 0xFFFFFFFF;

            int color = isMouseOver(mouseX, mouseY) ? color3 :  (id % 2 == 0 ? color1 : color2);
            guiGraphicsExtractor.fill(left, top, right, bottom, color);

            activeCheckbox.setPosition(right - 25, bottom - 25);
            activeCheckbox.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);

            playButton.setPosition(right - 45, bottom - 25);
            playButton.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);

            volumeSlider.setPosition(left + 5, bottom - 25);
            volumeSlider.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);

            guiGraphicsExtractor.text(Minecraft.getInstance().font, soundInstanceRecord.fileName, left + 5, top + 5, isMouseOver(mouseX, mouseY) ? 0xFF424242 : 0xFFFFFFFF, false);
        }

        @Override
        public int getContentRight() {
            return super.getContentRight() - 13;
        }

        @Override
        public int getWidth() {
            return super.getWidth() - 13;
        }

        @Override
        public @NonNull List<? extends NarratableEntry> narratables() {
            return List.of(this.activeCheckbox, this.volumeSlider, this.playButton);
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return List.of(this.activeCheckbox, this.volumeSlider, this.playButton);
        }
    }
}

