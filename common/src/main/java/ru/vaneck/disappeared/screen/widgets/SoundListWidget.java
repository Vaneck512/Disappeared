package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
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
//    private int x = 0;
//    private int y = 0;

    public SoundListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        setX(90);
//        this.setX(x);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
    }

    public void add() {
        SoundListEntry s = new SoundListEntry(getX(), SoundResourcesCache.ALL_SOUNDS.stream().toList().getFirst());
        addEntry(s);
//        System.out.println(this.children().size()*defaultEntryHeight);
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {
        super.extractListBackground(graphics);
//        graphics.fill(getX(), getY(), getWidth(), getHeight(), 0xFFFFFFFF);
    }

//    @Override
//    public int getX() {
//        return this.x;
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

        private SoundInstanceRecord soundInstanceRecord;
        private final IconButtonWidget playButton;
        private final Checkbox activeCheckbox;

        private int left;
        private int top;

        public SoundListEntry(int left, SoundInstanceRecord record) {
            super();
            this.left = left;
            this.soundInstanceRecord = record;
            this.activeCheckbox = Checkbox.builder(Component.empty(), Minecraft.getInstance().font)
                    .pos(getRectangle().right()-20, getRectangle().bottom()-20)
                    .selected(soundInstanceRecord.active)
                    .onValueChange((checkbox, b) -> {
                        soundInstanceRecord.active = b;
                    })
                    .build();
            this.playButton = new IconButtonWidget(getRectangle().right()-45, getRectangle().bottom()-20, 16,
                    16, Identifier.parse("disappeared:textures/gui/icon/play_icon.png"),
                    b -> {
                        SAP.play(
                                soundInstanceRecord.path,
                                DisappearedConstants.IDENTIFIER_PATTERN.matcher(soundInstanceRecord.path).find() ? AudioType.LOCATION : AudioType.FILE,
                                soundInstanceRecord.volume
                        );
                    }
            );
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean b, float partialTick) {
//            guiGraphicsExtractor.text(Minecraft.getInstance().font, "СТРОКА ЬАЬАЬАЬЬ", left+5, getY()+5, 0xFFFFFFFF);
            activeCheckbox.setPosition(getRectangle().right()-30, getRectangle().bottom()-20);
            activeCheckbox.extractRenderState(guiGraphicsExtractor, mouseX ,mouseY, partialTick);
            playButton.setPosition(getRectangle().right()-45, getRectangle().bottom()-20);
            playButton.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
            guiGraphicsExtractor.text(Minecraft.getInstance().font, soundInstanceRecord.fileName, left + 5, getY() + 5, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (this.playButton.mouseClicked(event, doubleClick))
                return true;
            if (this.activeCheckbox.mouseClicked(event, doubleClick))
                return true;
            return super.mouseClicked(event, doubleClick);
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
