package ru.vaneck.disappeared.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

public class NoteBlockGeometry implements SubmitNodeCollector.CustomGeometryRenderer {

    private final Identifier noteBlockLocation = Identifier.withDefaultNamespace("block/note_block");
    private TextureAtlasSprite sprite = null;

    @Override
    public void render(PoseStack.@NonNull Pose pose, @NonNull VertexConsumer vertexConsumer) {
        float min = -0.25f;
        float max = 0.25f;

        if (sprite == null)
            sprite = Minecraft.getInstance()
                    .getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS)
                    .getSprite(noteBlockLocation);

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        int light = 15728880;
        int overlay = OverlayTexture.NO_OVERLAY;

        vertex(pose, vertexConsumer, min, min, max, u0, v1, 0, 0, 1, light, overlay);
        vertex(pose, vertexConsumer, max, min, max, u1, v1, 0, 0, 1, light, overlay);
        vertex(pose, vertexConsumer, max, max, max, u1, v0, 0, 0, 1, light, overlay);
        vertex(pose, vertexConsumer, min, max, max, u0, v0, 0, 0, 1, light, overlay);

        vertex(pose, vertexConsumer, max, min, min, u0, v1, 0, 0, -1, light, overlay);
        vertex(pose, vertexConsumer, min, min, min, u1, v1, 0, 0, -1, light, overlay);
        vertex(pose, vertexConsumer, min, max, min, u1, v0, 0, 0, -1, light, overlay);
        vertex(pose, vertexConsumer, max, max, min, u0, v0, 0, 0, -1, light, overlay);

        vertex(pose, vertexConsumer, min, max, max, u0, v1, 0, 1, 0, light, overlay);
        vertex(pose, vertexConsumer, max, max, max, u1, v1, 0, 1, 0, light, overlay);
        vertex(pose, vertexConsumer, max, max, min, u1, v0, 0, 1, 0, light, overlay);
        vertex(pose, vertexConsumer, min, max, min, u0, v0, 0, 1, 0, light, overlay);

        vertex(pose, vertexConsumer, min, min, min, u0, v1, 0, -1, 0, light, overlay);
        vertex(pose, vertexConsumer, max, min, min, u1, v1, 0, -1, 0, light, overlay);
        vertex(pose, vertexConsumer, max, min, max, u1, v0, 0, -1, 0, light, overlay);
        vertex(pose, vertexConsumer, min, min, max, u0, v0, 0, -1, 0, light, overlay);

        vertex(pose, vertexConsumer, max, min, max, u0, v1, 1, 0, 0, light, overlay);
        vertex(pose, vertexConsumer, max, min, min, u1, v1, 1, 0, 0, light, overlay);
        vertex(pose, vertexConsumer, max, max, min, u1, v0, 1, 0, 0, light, overlay);
        vertex(pose, vertexConsumer, max, max, max, u0, v0, 1, 0, 0, light, overlay);

        vertex(pose, vertexConsumer, min, min, min, u0, v1, -1, 0, 0, light, overlay);
        vertex(pose, vertexConsumer, min, min, max, u1, v1, -1, 0, 0, light, overlay);
        vertex(pose, vertexConsumer, min, max, max, u1, v0, -1, 0, 0, light, overlay);
        vertex(pose, vertexConsumer, min, max, min, u0, v0, -1, 0, 0, light, overlay);
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer,
                        float x, float y, float z, float u, float v,
                        float nx, float ny, float nz, int light, int overlay) {

        Vector4f pos = new Vector4f(x, y, z, 1.0f);
        pos.mul(pose.pose());

        Vector3f normal = new Vector3f(nx, ny, nz);
        normal.mul(pose.normal());

        consumer.addVertex(pos.x(), pos.y(), pos.z())
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(normal.x(), normal.y(), normal.z());
    }
}
