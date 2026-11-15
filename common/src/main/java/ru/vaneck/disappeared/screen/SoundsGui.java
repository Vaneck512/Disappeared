package ru.vaneck.disappeared.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.files.SoundResourcesCache;
import ru.vaneck.disappeared.screen.widgets.SoundListWidget;

public class SoundsGui extends Screen {
    private SoundListWidget soundListWidget;

    private final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/sounds_gui.png");

    private static final Component TITLE = Component.translatable("disappeared.gui_components.sounds_text");
    private static final int FONT_COLOR = 0xFF424242;

    private final int BACKGROUND_TOP_HEIGHT = 17;
    private final int BACKGROUND_BOTTOM_HEIGHT = 10;
    private final int ENTRY_HEIGHT = 45;

    private int maxEntries;
    private int ySize;
    private int TOP = 62;

    public SoundsGui() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();

        TOP = height/100 * 15;

        int x = width/2 - 235/2;
        int y = TOP;

        int minimumEntries = Mth.ceil((float) (BACKGROUND_BOTTOM_HEIGHT + BACKGROUND_TOP_HEIGHT + y + ENTRY_HEIGHT) / ENTRY_HEIGHT);
        int maxEntriesOnScreen = Math.max(minimumEntries, (height - BACKGROUND_TOP_HEIGHT - BACKGROUND_BOTTOM_HEIGHT - y * 2 + 16) / ENTRY_HEIGHT);
        maxEntries = Math.clamp(SoundResourcesCache.ALL_SOUNDS.size()+1, 1, maxEntriesOnScreen);

        ySize = ENTRY_HEIGHT * maxEntries - BACKGROUND_BOTTOM_HEIGHT;

        this.soundListWidget = addRenderableWidget(new SoundListWidget(Minecraft.getInstance(), x, 248, ySize+5, TOP+15, ENTRY_HEIGHT));

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = width/2 - 235/2;
        int y = TOP+16;
        graphics.enableScissor(x, y, x+256, y+ySize+5);
        this.soundListWidget.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.disableScissor();

        int width = this.soundListWidget.isScrollable() ? 256 : 238;
        graphics.text(font, TITLE, x + width/2 - font.width(TITLE) / 2, y - 10, FONT_COLOR, false);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = width/2 - 235/2 - 1;
        int y = TOP;

        boolean isScrollbarThere = this.soundListWidget.isScrollable();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y, 0, 0, isScrollbarThere ? 256 : 235, BACKGROUND_TOP_HEIGHT, 256, 256);
        if (!isScrollbarThere) graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x+235, y, 253, 0, 3, BACKGROUND_TOP_HEIGHT, 256, 256);
        for (int i = 0; i < maxEntries; i++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y + BACKGROUND_TOP_HEIGHT + i * ENTRY_HEIGHT, 0, BACKGROUND_TOP_HEIGHT + 1, isScrollbarThere ? 256 : 235, ENTRY_HEIGHT, 256, 256);
            if (!isScrollbarThere) graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x+235, y + BACKGROUND_TOP_HEIGHT + i * ENTRY_HEIGHT, 253, BACKGROUND_TOP_HEIGHT + 1, 3, ENTRY_HEIGHT, 256, 256);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y + ENTRY_HEIGHT * maxEntries + BACKGROUND_BOTTOM_HEIGHT, 0, 90 - BACKGROUND_BOTTOM_HEIGHT, isScrollbarThere ? 256 : 235, BACKGROUND_BOTTOM_HEIGHT, 256, 256);
        if (!isScrollbarThere) graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x+235, y + ENTRY_HEIGHT * maxEntries + BACKGROUND_BOTTOM_HEIGHT, 253, 90 - BACKGROUND_BOTTOM_HEIGHT, 3, BACKGROUND_BOTTOM_HEIGHT, 256, 256);

    }
}
