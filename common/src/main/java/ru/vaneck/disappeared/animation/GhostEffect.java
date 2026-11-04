package ru.vaneck.disappeared.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import ru.vaneck.disappeared.animation.ghost.Ghost;
import ru.vaneck.disappeared.animation.ghost.IGhostRenderState;
import ru.vaneck.disappeared.config.DisappearedConfiguration;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GhostEffect {

    private static final DisappearedConfiguration configuration = DisappearedConfiguration.CONFIGURATION;

    public static final Set<Ghost<?,?>> GHOSTS_TO_RENDER = ConcurrentHashMap.newKeySet();
    public static final Set<Ghost<?,?>> REVERSED_GHOSTS_TO_RENDER = ConcurrentHashMap.newKeySet();

    public static void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        GHOSTS_TO_RENDER.removeIf(GhostEffect::removeIf);
        REVERSED_GHOSTS_TO_RENDER.removeIf(GhostEffect::removeIf);
        REVERSED_GHOSTS_TO_RENDER.removeIf(reversed ->
                GHOSTS_TO_RENDER.stream().anyMatch(ghost -> ghost.uuid.equals(reversed.uuid)));
        for (Ghost<?,?> ghostUnit : GHOSTS_TO_RENDER) {
            if (!ghostUnit.entity.level().equals(Minecraft.getInstance().player.level())) return;
            renderGhost(ghostUnit, poseStack, submitNodeCollector, camera);
        }
        for (Ghost<?,?> ghostUnit : REVERSED_GHOSTS_TO_RENDER) {
            if (!ghostUnit.entity.level().equals(Minecraft.getInstance().player.level())) return;
            renderGhost(ghostUnit, poseStack, submitNodeCollector, camera);
        }
    }

    private static boolean removeIf(Ghost<?,?> ghost) {
        double distance = ghost.pos.distanceTo(Minecraft.getInstance().player.position());
        return distance >= 300 || Util.getMillis() - ghost.timeCreatedMs > ghost.durationMs;
    }

    private static <E extends Entity, S extends EntityRenderState>
    void renderGhost(Ghost<E, S> ghostUnit, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!(ghostUnit.entity instanceof LivingEntity)) return;
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        assert Minecraft.getInstance().player != null;

        EntityRenderState ghostRenderState =
                ghostUnit.reverse
                ? ghostUnit.entityRenderer.createRenderState(ghostUnit.entity, partialTicks)
                : ghostUnit.renderState;


        if (ghostRenderState instanceof LivingEntityRenderState livingState && livingState instanceof IGhostRenderState ghostState) {
            ghostState.disappeared$setGhost(ghostUnit);
        }
        if (poseStack instanceof IGhostRenderState ighostRenderState)
            ighostRenderState.disappeared$setGhost(ghostUnit);

        if (ghostUnit.reverse)
            ghostRenderState.y = ghostUnit.entity.getPosition(partialTicks).y;

        double renderX = ghostRenderState.x - camera.pos.x;
        double renderY = ghostRenderState.y - camera.pos.y;
        double renderZ = ghostRenderState.z - camera.pos.z;

        dispatcher.submit(
                ghostRenderState,
                camera,
                renderX,
                renderY,
                renderZ,
                poseStack,
                submitNodeCollector
        );

        if (ghostUnit.isMusicMob)
        {
            poseStack.pushPose();
            poseStack.translate(renderX, renderY+ghostRenderState.boundingBoxHeight+0.5, renderZ);
            float angle = (Util.getMillis() + partialTicks) * 0.1F % 360.0F;
            poseStack.last().pose().rotate(Axis.YP.rotationDegrees(angle));
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.solidMovingBlock(), new NoteBlockGeometry());
            poseStack.popPose();
        }

    }

    public static void clearAll() {
        GHOSTS_TO_RENDER.clear();
        REVERSED_GHOSTS_TO_RENDER.clear();
    }

    public static void submitReversedGhost(Entity entity, long lifetimeMs, boolean isMusicMob) {
        int maxGhostCount = configuration.MAX_GHOSTS.getValue();
        if ((REVERSED_GHOSTS_TO_RENDER.size() < maxGhostCount || maxGhostCount < 0) && lifetimeMs > 0)
            REVERSED_GHOSTS_TO_RENDER.add(new Ghost<>(entity, lifetimeMs, true, isMusicMob));
    }

    public static void submitGhost(Entity entity, long lifetimeMs) {
        int maxGhostCount = configuration.MAX_GHOSTS.getValue();
        if ((GHOSTS_TO_RENDER.size() < maxGhostCount || maxGhostCount < 0) && lifetimeMs > 0)
            GHOSTS_TO_RENDER.add(new Ghost<>(entity, lifetimeMs, false, false));
    }



    public static RenderType castToTranslucent(RenderType prevType, boolean cull) {
        Pattern LOCATION_PATTERN = Pattern.compile("location=([^,\\s\\]]+)");
        Matcher matcher = LOCATION_PATTERN.matcher(prevType.toString());
        Identifier texture;
        if (matcher.find()) {
            texture = Identifier.parse(matcher.group(1));
        } else {
            return prevType;
        }
        return cull ? RenderTypes.entityTranslucentCull(texture) : RenderTypes.entityTranslucent(texture);
    }
}
