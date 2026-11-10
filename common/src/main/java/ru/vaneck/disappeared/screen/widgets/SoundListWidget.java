package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
    private int x = 0;
//    private int y = 0;

    public SoundListWidget(Minecraft minecraft, int x, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        setX(90);
        this.x = x;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
    }

    public void add() {
        SoundListEntry s = new SoundListEntry(getX(), getWidth(), SoundResourcesCache.ALL_SOUNDS.stream().toList().get(1));
        SoundListEntry s1 = new SoundListEntry(getX(), getWidth(), SoundResourcesCache.ALL_SOUNDS.stream().toList().get(2));
        addEntry(s);
        addEntry(s1);
//        System.out.println(this.children().size()*defaultEntryHeight);
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {
        super.extractListBackground(graphics);
//        graphics.fill(getX(), getY(), getWidth(), getHeight(), 0xFFFFFFFF);
    }

    @Override
    public int getX() {
        return this.x;
    }

//    @Override
//    public int getRowLeft() {
//        return this.x;
//    }
//
//    @Override
//    public int getRowRight() {
//        return this.x + this.width;
//    }

    //
//    @Override
//    public int getY() {
//        return this.y;
//    }

//    @Override
//    public void setX(int x) {
//        this.x = x;
//    }
//
//    @Override
//    public void setY(int y) {
//        this.y = y;
//    }

    public static class SoundListEntry extends ContainerObjectSelectionList.Entry<SoundListEntry> {

        private static int count = 0;
        private int id = 0;

        private SoundInstanceRecord soundInstanceRecord;
        private final SliderCFGWidget volumeSlider;
        private final IconButtonWidget playButton;
        private final Checkbox activeCheckbox;

        private int left;
        private int top;

        private int width;

        public SoundListEntry(int left, int width, SoundInstanceRecord record) {
            count++;
            super();
            id = count;
            this.left = left;
            this.soundInstanceRecord = record;
            this.activeCheckbox = Checkbox.builder(Component.empty(), Minecraft.getInstance().font)
                    .pos(getRectangle().right()-20, getRectangle().bottom()-20)
                    .selected(soundInstanceRecord.active)
                    .onValueChange((checkbox, b) -> {
                        soundInstanceRecord.active = b;
                    })
                    .build();
            this.playButton = new IconButtonWidget(getRectangle().right()-45, getRectangle().bottom()-18, 17,
                    17, Identifier.parse("disappeared:textures/gui/icon/play_icon.png"),
                    b -> {
                        SAP.play(
                                soundInstanceRecord.path,
                                DisappearedConstants.IDENTIFIER_PATTERN.matcher(soundInstanceRecord.path).find() ? AudioType.LOCATION : AudioType.FILE,
                                soundInstanceRecord.volume
                        );
                    }
            ) {
                @Override
                protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
                    this.extractDefaultSprite(guiGraphicsExtractor);
                    this.extractDefaultLabel(guiGraphicsExtractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
                    int x = getX()+width/2 - 6;
                    int y = getY()+height/2 - 6;
                    guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, ICON, x, y, 0, 0, 13, 13, 13, 13);
                }
            };
            this.volumeSlider = new SliderCFGWidget(getX(), getY(), 100, 20,
                    Component.literal("Volume"), soundInstanceRecord.volume, soundInstanceRecord);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean b, float partialTick) {
//            guiGraphicsExtractor.text(Minecraft.getInstance().font, "СТРОКА ЬАЬАЬАЬЬ", left+5, getY()+5, 0xFFFFFFFF);
            guiGraphicsExtractor.fill(getX(), getY(), getRectangle().left()+getWidth(), getY()+getHeight(), id % 2 == 0 ? 0xFFFFFFFF : 0xFFababab);
            activeCheckbox.setPosition(getRectangle().right()-30, getRectangle().bottom()-25);
            activeCheckbox.extractRenderState(guiGraphicsExtractor, mouseX ,mouseY, partialTick);
            playButton.setPosition(getRectangle().right()-50, getRectangle().bottom()-25);
            playButton.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
            volumeSlider.setPosition(getX()+5, getRectangle().bottom() - 25);
            this.volumeSlider.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
            guiGraphicsExtractor.text(Minecraft.getInstance().font, soundInstanceRecord.fileName, getX() + 5, getY() + 5, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            this.volumeSlider.setFocused(false);
            if (this.playButton.mouseClicked(event, doubleClick))
                return true;
            if (this.activeCheckbox.mouseClicked(event, doubleClick))
                return true;
            if (this.volumeSlider.mouseClicked(event, doubleClick)) {
                this.volumeSlider.setFocused(true);
                return true;
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
            if (this.volumeSlider.mouseDragged(event, dx, dy)) {
                this.volumeSlider.setFocused(true);
                return true;
            }
            return super.mouseDragged(event, dx, dy);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            if (this.volumeSlider.mouseReleased(event))
                return true;
            return super.mouseReleased(event);
        }

        @Override
        public boolean isMouseOver(double mx, double my) {
            if (this.playButton.isMouseOver(mx, my))
                return true;
            if (this.activeCheckbox.isMouseOver(mx, my))
                return true;
            if (this.volumeSlider.isMouseOver(mx, my))
                return true;
            return super.isMouseOver(mx, my);
        }

        @Override
        public boolean keyPressed(@NonNull KeyEvent event) {
            if (this.volumeSlider.isFocused() && this.volumeSlider.keyPressed(event))
                return true;
            return false;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return getY();
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
