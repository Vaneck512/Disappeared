package ru.vaneck.disappeared.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.ItemQuads;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.vaneck.disappeared.animation.GhostEffect;
import ru.vaneck.disappeared.animation.ghost.Ghost;
import ru.vaneck.disappeared.animation.ghost.IGhostRenderState;

import java.util.ArrayList;
import java.util.List;

@Mixin(SubmitNodeCollection.class)
public abstract class SubmitNodeCollectionMixin {

    @ModifyVariable(
            method = "submitModel",
            at = @At("HEAD"),
            argsOnly = true
    )
    private <S> RenderType disappeared$modifyRenderTypeArg(RenderType renderType, Model<? super S> model, S state) {
        if (state instanceof IGhostRenderState ghostRenderState && ghostRenderState.disappeared$isGhost()) {
            return GhostEffect.castToTranslucent(renderType);
        }
        return renderType;
    }

    @ModifyVariable(
            method = "submitModel",
            at = @At("HEAD"),
            ordinal = 2,
            argsOnly = true
    )
    private <S> int disappeared$modifyTintedColorArg(int tintedColor, Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords) {
        if (state instanceof IGhostRenderState ghostRenderState && ghostRenderState.disappeared$isGhost()) {
            float alpha = ghostRenderState.disappeared$getGhostAlpha();

            int r = ARGB.red(tintedColor);
            int g = ARGB.green(tintedColor);
            int b = ARGB.blue(tintedColor);
            int vanillaAlpha = ARGB.alpha(tintedColor);

            int newAlpha = (int) (vanillaAlpha * alpha);

            if (ghostRenderState.disappeared$getGhost().isMusicMob)
                newAlpha = Math.max(newAlpha, 176);

            return ARGB.color(newAlpha, r, g, b);
        }
        return tintedColor;
    }

    @ModifyVariable(
            method = "submitItem",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0)
    private ItemQuads disappeared$modifyQuadsForTransparency(
            ItemQuads originalQuads,
            PoseStack poseStack,
            ItemDisplayContext displayContext
    ) {
        if ((displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                && poseStack instanceof IGhostRenderState ghostState && ghostState.disappeared$isGhost()) {

            if (originalQuads == null || originalQuads.isEmpty()) {
                return originalQuads;
            }

            return new ItemQuads(disappeared$applyTranslucent(originalQuads.all()), disappeared$applyTranslucent(originalQuads.solid()), disappeared$applyTranslucent(originalQuads.translucent()));
        }

        return originalQuads;
    }

    private List<BakedQuad> disappeared$applyTranslucent(List<BakedQuad> originalQuads) {
        List<BakedQuad> modifiedQuads = new ArrayList<>(originalQuads.size());

        for (BakedQuad quad : originalQuads) {
            BakedQuad.MaterialInfo oldInfo = quad.materialInfo();

            boolean isBlockAtlas = oldInfo.sprite().atlasLocation().toString().contains("blocks");
            var newRenderType = isBlockAtlas ? Sheets.translucentBlockItemSheet() : Sheets.translucentItemSheet();

            int forcedTintIndex = oldInfo.tintIndex();
            if (forcedTintIndex == -1) {
                forcedTintIndex = 99;
            }

            BakedQuad.MaterialInfo newInfo = new BakedQuad.MaterialInfo(
                    oldInfo.sprite(),
                    ChunkSectionLayer.TRANSLUCENT,
                    newRenderType,
                    oldInfo.itemGlintRenderType(),
                    oldInfo.itemGlintSpecialRenderType(),
                    forcedTintIndex,
                    oldInfo.shadeDirectionOverride(),
                    oldInfo.lightEmission()
            );

            BakedQuad newQuad = new BakedQuad(
                    quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                    quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                    quad.direction(),
                    newInfo
            );

            modifiedQuads.add(newQuad);
        }

        return modifiedQuads;
    }

    @ModifyVariable(
            method = "submitItem",
            at = @At("HEAD"),
            argsOnly = true
    )
    private int[] disappeared$modifyItemTintLayers(int[] tintLayers, PoseStack poseStack) {

        if (poseStack instanceof IGhostRenderState ghostState && ghostState.disappeared$isGhost()) {
            float alpha = ghostState.disappeared$getGhostAlpha();
            int targetAlpha = (int) (255 * alpha);

            int newLength = Math.max(tintLayers.length, 100);
            int[] modifiedLayers = new int[newLength];

            for (int i = 0; i < tintLayers.length; i++) {
                int currentColor = tintLayers[i];
                int r = ARGB.red(currentColor);
                int g = ARGB.green(currentColor);
                int b = ARGB.blue(currentColor);
                int vanillaAlpha = ARGB.alpha(currentColor);

                modifiedLayers[i] = ARGB.color((int) (vanillaAlpha * alpha), r, g, b);
            }

            if (ghostState.disappeared$getGhost().isMusicMob)
                targetAlpha = Math.max(targetAlpha, 176);

            for (int i = tintLayers.length; i < modifiedLayers.length; i++) {
                modifiedLayers[i] = ARGB.color(targetAlpha, 255, 255, 255);
            }

            return modifiedLayers;
        }

        return tintLayers;
    }

    @ModifyVariable(
            method = "submitShadow",
            at = @At("HEAD"),
            argsOnly = true
    )
    private List<EntityRenderState.ShadowPiece> disappeared$modifyShadowPieces(List<EntityRenderState.ShadowPiece> original, PoseStack poseStack) {
        if (poseStack instanceof IGhostRenderState ghostRenderState && ghostRenderState.disappeared$isGhost()) {
            Ghost<?,?> ghostUnit = ghostRenderState.disappeared$getGhost();
            List<EntityRenderState.ShadowPiece> modifiedPieces = new ArrayList<>();
            for (EntityRenderState.ShadowPiece piece : original) {
                long currentTimeMs = Util.getMillis();
                float elapsed = (float) (currentTimeMs - ghostUnit.timeCreatedMs);
                elapsed = Math.max(0.0F, elapsed);
                float fadeProgress = Math.min(piece.alpha(), elapsed / (ghostUnit.durationMs+20));
                int alpha = Math.round((ghostUnit.reverse ? fadeProgress : (piece.alpha() - fadeProgress)) * 255);
                float alphaFloat = alpha / 255.0F;
                modifiedPieces.add(new EntityRenderState.ShadowPiece(
                        piece.relativeX(),
                        piece.relativeY(),
                        piece.relativeZ(),
                        piece.shapeBelow(),
                        Math.min(alphaFloat, piece.alpha())
                ));
            }
            return modifiedPieces;
        }
        return original;
    }
}
