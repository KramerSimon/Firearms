package com.sio.firearms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sio.firearms.block.WeaponRackBlock;
import com.sio.firearms.block.WeaponRackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WeaponRackRenderer implements BlockEntityRenderer<WeaponRackBlockEntity> {

    public WeaponRackRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(WeaponRackBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = be.getStoredItem();
        // Explicit null + empty guard — ensures stale render data never shows a ghost gun
        if (stack == null || stack.isEmpty()) return;

        Direction facing = be.getBlockState().getValue(WeaponRackBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        // Rotate item to face outward from the wall
        float yRot = switch (facing) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case EAST  -> 270f;
            case WEST  -> 90f;
            default    -> 0f;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        // Tilt gun to lie horizontally across the rack pegs
        poseStack.mulPose(Axis.ZP.rotationDegrees(90f));

        poseStack.scale(0.55f, 0.55f, 0.55f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.FIXED,
                packedLight, packedOverlay, poseStack, bufferSource,
                be.getLevel(), 0
        );

        poseStack.popPose();
    }
}
