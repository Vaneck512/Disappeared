package ru.vaneck.disappeared.animation.ghost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class Ghost <E extends Entity, S extends EntityRenderState> implements Comparable<Ghost<?,?>> {
    public final E entity;
    public final S renderState;
    public final EntityRenderer<? super E, S> entityRenderer;
    public final Vec3 pos;
    public final UUID uuid;
    public final long timeCreatedMs;
    public final long durationMs;
    public boolean isMusicMob;
    public boolean reverse;

    @SuppressWarnings("unchecked")
    public Ghost(E entity, long durationMs) {
        assert Minecraft.getInstance().player != null;
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        this.entity = entity;
        this.entityRenderer = (EntityRenderer<? super E, S>) dispatcher.getRenderer(entity);
        this.renderState = this.entityRenderer.createRenderState(entity, 0);
        this.pos = entity.position();
        this.uuid = entity.getUUID();
        this.timeCreatedMs = Util.getMillis();
        this.durationMs = durationMs;
        this.reverse = false;
        this.isMusicMob = false;

        if (entity instanceof LivingEntity living) {
            LivingEntityRenderState livingState = (LivingEntityRenderState) this.renderState;
            livingState.walkAnimationPos = living.walkAnimation.position(1.0F);
            livingState.walkAnimationSpeed = living.walkAnimation.speed(1.0F);

            livingState.ageInTicks = (float)living.tickCount;

            livingState.pose = living.getPose();

        }
    }

    public Ghost(E entity, long durationMs, boolean reverse, boolean isMusicMob) {
        this(entity, durationMs);
        this.reverse = reverse;
        this.isMusicMob = isMusicMob;
    }

    @Override
    public int compareTo(@NonNull Ghost o) {
        return this.uuid.compareTo(o.uuid);
    }
}
