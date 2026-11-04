package ru.vaneck.disappeared.mixin;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.vaneck.disappeared.animation.ghost.Ghost;
import ru.vaneck.disappeared.animation.ghost.IGhostRenderState;

import java.util.UUID;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IGhostRenderState {

    @Unique
    Ghost<?,?> disappeared$ghost = null;

    @Unique
    public UUID disappeared$uuid;

    @Override
    public float disappeared$getGhostAlpha() {
        if (disappeared$ghost == null) return 0;
        long currentTimeMs = Util.getMillis();
        float elapsed = (float) (currentTimeMs - disappeared$ghost.timeCreatedMs);
        elapsed = Math.max(0.0F, elapsed);
        float fadeProgress = Math.min(1.0F, elapsed / (disappeared$ghost.durationMs));
        int alpha = Math.round((disappeared$ghost.reverse ? fadeProgress : (1.0F - fadeProgress)) * 255);
        return alpha / 255.0F;
    }

    @Override
    public boolean disappeared$isGhost() {
        return disappeared$ghost != null &&
                (Util.getMillis() - disappeared$ghost.timeCreatedMs) <= disappeared$ghost.durationMs;
    }

    @Override
    public void disappeared$setGhost(Ghost<?, ?> ghost) {
        disappeared$ghost = ghost;
    }

    @Override
    public Ghost<?, ?> disappeared$getGhost() {
        return disappeared$ghost;
    }

}
