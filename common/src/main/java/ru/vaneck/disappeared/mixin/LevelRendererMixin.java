package ru.vaneck.disappeared.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.vaneck.disappeared.animation.GhostEffect;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(
            method = "submitEntities",
            at = @At("TAIL")
    )
    private void disappeared$onGlobalCollectSubmits(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector output, CallbackInfo ci) {
        GhostEffect.render(poseStack, output, levelRenderState.cameraRenderState);
    }
}
