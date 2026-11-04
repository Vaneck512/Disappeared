package ru.vaneck.disappeared.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.vaneck.disappeared.animation.GhostEffect;

/**
 * Hides original entity's model when appearance animation begin
 *
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void disappeared$onSubmit(T entity, S state, float partialTicks, CallbackInfo ci) {
        if (
                GhostEffect.REVERSED_GHOSTS_TO_RENDER.stream()
                        .anyMatch(
                                ghost -> ghost.uuid.equals(entity.getUUID())
                                        && (Util.getMillis() - ghost.timeCreatedMs) < (ghost.durationMs - 20)
                        )
        ) {
            state.y = 993429512;
        }
    }

}
