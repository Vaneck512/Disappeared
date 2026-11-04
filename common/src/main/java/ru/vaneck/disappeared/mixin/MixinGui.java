package ru.vaneck.disappeared.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
    A workaround?
    For Flashback,
    to see whether "Disappeared" is enabled or not.
 */

@Mixin(Hud.class)
public class MixinGui {

    @Shadow @Final private Minecraft minecraft;
    @Shadow private Component overlayMessageString;
    @Shadow private int overlayMessageTime;
    @Shadow private boolean animateOverlayMessageColor;
    @Shadow public Font getFont() { return null; }

    @Shadow
    private boolean isHidden;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void disappeared$forceRenderCustomOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        boolean isHudHidden = this.isHidden;
        boolean isSpectator = this.minecraft.player != null && this.minecraft.player.isSpectator();

        if (isHudHidden && !isSpectator) {
            return;
        }

        if (this.overlayMessageString != null && this.overlayMessageTime > 0) {

            String rawText = this.overlayMessageString.getString();
            if (!rawText.toLowerCase().contains("Disappeared".toLowerCase())) {
                return;
            }

            Profiler.get().push("forcedCustomOverlayMessage");
            Font font = this.getFont();

            float t = (float)this.overlayMessageTime - deltaTracker.getGameTimeDeltaPartialTick(false);
            int alpha = (int)(t * 255.0F / 20.0F);
            if (alpha > 255) alpha = 255;

            if (alpha > 0) {
                graphics.nextStratum();
                graphics.pose().pushMatrix();

                int yOffset = graphics.guiHeight() - 68;
                graphics.pose().translate((float)(graphics.guiWidth() / 2), (float)yOffset);

                int maskColor;
                if (this.animateOverlayMessageColor) {
                    maskColor = Mth.hsvToArgb(t / 50.0F, 0.7F, 0.6F, alpha);
                } else {
                    maskColor = 0x00FFFFFF | (alpha << 24);
                }

                int width = font.width(this.overlayMessageString);

                graphics.textWithBackdrop(font, this.overlayMessageString, -width / 2, -4, width, maskColor);

                graphics.pose().popMatrix();
            }

            Profiler.get().pop();
        }
    }
}
