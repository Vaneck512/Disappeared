package ru.vaneck.disappeared.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import ru.vaneck.disappeared.DisappearedConstants;
import ru.vaneck.disappeared.config.DisappearedConfiguration;
import ru.vaneck.disappeared.screen.widgets.ButtonCFGWidget;
import ru.vaneck.disappeared.screen.widgets.EditBoxCFGWidget;
import ru.vaneck.disappeared.screen.widgets.SliderCFGWidget;

public class SettingsGui extends Screen {

    public static final DisappearedConfiguration CONFIGURATION = DisappearedConfiguration.CONFIGURATION;

    public SettingsGui() {
        super(Component.empty());
        this.parent = null;
    }

    public SettingsGui(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    public SettingsGui(Screen parent, boolean showDoneButton) {
        this(parent);
        this.showDoneButton = showDoneButton;
    }

    private boolean showDoneButton = false;

    private final Screen parent;

    private final int FONT_COLOR = 0xFF404040;

    private final int texWidth = 195;
    private final int texHeight = 128; //94

    private final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DisappearedConstants.MOD_ID, "textures/gui/settings_screen.png");

    private final Component DISAPPEARANCE_DURATION = Component.translatable("disappeared.gui_components.disappearance_animation_duration_text");
    private final Component APPEARANCE_DURATION = Component.translatable("disappeared.gui_components.appearance_animation_duration_text");
    private final Component MASTER_VOLUME = Component.translatable("soundCategory.master");
    private final Component MODE = Component.translatable("disappeared.gui_components.work_mode");
    private final Component PLAYERS = Component.translatable("disappeared.gui_components.state_players");
    private final Component MOBS = Component.translatable("disappeared.gui_components.state_mobs");

    @Override
    protected void init() {
        super.init();
        int x = width/2-(texWidth/2);
        int y = height/2-(texHeight/2);

        addRenderableWidget(new SliderCFGWidget(x+15, y+10, 164, 15, MASTER_VOLUME, 0.75, CONFIGURATION.MASTER_VOLUME));

        addRenderableWidget(new EditBoxCFGWidget(Minecraft.getInstance().font, x+115, y+35, 64, 20, CONFIGURATION.DISAPPEAR_ANIMATION_DURATION));

        addRenderableWidget(new EditBoxCFGWidget(Minecraft.getInstance().font, x+115, y+65, 64, 20, CONFIGURATION.APPEAR_ANIMATION_DURATION));

        addRenderableWidget(new ButtonCFGWidget(x+115, y+94, 64, 20, PLAYERS, MOBS, CONFIGURATION.ONLY_PLAYERS));

//        addRenderableWidget(Button.builder(Component.literal(""), button -> minecraft.setScreenAndShow(new MainGui())).build());

        if (showDoneButton) {
            addRenderableWidget(Button.builder(Component.translatable("gui.done"), _ -> onClose())
                    .bounds(x, y + texHeight + 15, texWidth, 20)
                    .build());
        }
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = width/2-(texWidth/2);
        int y = height/2-(texHeight/2);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, texWidth, texHeight, 256, 256);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int x = width/2-(texWidth/2);
        int y = height/2-(texHeight/2);
        graphics.textWithWordWrap(font, DISAPPEARANCE_DURATION, x + 15, y + 40, 107, FONT_COLOR, false);
        graphics.textWithWordWrap(font, APPEARANCE_DURATION, x + 15, y + 70, 107, FONT_COLOR, false);
        graphics.textWithWordWrap(font, MODE, x + 15, y + 108-font.lineHeight, 107, FONT_COLOR, false);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        clearFocus();
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void extractBlurredBackground(@NonNull GuiGraphicsExtractor graphics) {
        super.extractBlurredBackground(graphics);
    }

    @Override
    public void onClose() {
        super.onClose();
        CONFIGURATION.save();
        if (parent != null)
            Minecraft.getInstance().gui.setScreen(parent);
    }
}