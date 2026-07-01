package com.sio.firearms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sio.firearms.Firearms;
import com.sio.firearms.block.ChemicalMixerControllerBlockEntity;
import com.sio.firearms.block.CoilBlock;
import com.sio.firearms.block.CokeOvenControllerBlockEntity;
import com.sio.firearms.block.CoolingTowerControllerBlockEntity;
import com.sio.firearms.block.CrystalGrowthControllerBlockEntity;
import com.sio.firearms.block.EBFControllerBlockEntity;
import com.sio.firearms.block.EuvLithographyControllerBlockEntity;
import com.sio.firearms.block.HangarControllerBlockEntity;
import com.sio.firearms.block.IMultiblockPreview;
import com.sio.firearms.block.ReactorControllerBlockEntity;
import com.sio.firearms.block.RefineryControllerBlockEntity;
import com.sio.firearms.block.SpentFuelStorageBlockEntity;
import com.sio.firearms.block.VehicleGarageControllerBlockEntity;
import com.sio.firearms.registry.ModBlocks;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Renders a translucent ghost outline of a multiblock structure: either around the
 * position the player is aiming at (while holding a controller item), or continuously
 * around a placed controller whose {@link IMultiblockPreview#isPreviewActive()} is on
 * (toggled with a wrench). Positions are colour-coded green (correct block already
 * placed) or red (wrong block / air); anything outside the structure gets no highlight.
 */
@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MultiblockPreviewRenderer {

    private static final int MAX_DISTANCE = 20;
    private static final int SCAN_RADIUS_CHUNKS = 3;

    // Controller block → dummy BlockEntity factory, used to call getPreviewPositions()
    // without needing a real placed BlockEntity (e.g. while just holding the item).
    // Built lazily on first use — a static initializer here would call ModBlocks.*.get()
    // as soon as this @EventBusSubscriber class is loaded, which happens before the block
    // RegisterEvent populates the DeferredRegister and throws an unbound-value NPE.
    private static Map<Block, BiFunction<BlockPos, BlockState, BlockEntity>> controllerFactories;

    private static Map<Block, BiFunction<BlockPos, BlockState, BlockEntity>> controllerFactories() {
        if (controllerFactories == null) {
            Map<Block, BiFunction<BlockPos, BlockState, BlockEntity>> map = new HashMap<>();
            map.put(ModBlocks.EBF_CONTROLLER.get(), EBFControllerBlockEntity::new);
            map.put(ModBlocks.CHEMICAL_MIXER_CONTROLLER.get(), ChemicalMixerControllerBlockEntity::new);
            map.put(ModBlocks.REFINERY_CONTROLLER.get(), RefineryControllerBlockEntity::new);
            map.put(ModBlocks.REACTOR_CONTROLLER.get(), ReactorControllerBlockEntity::new);
            map.put(ModBlocks.COOLING_TOWER_CONTROLLER.get(), CoolingTowerControllerBlockEntity::new);
            map.put(ModBlocks.GARAGE_CONTROLLER.get(), VehicleGarageControllerBlockEntity::new);
            map.put(ModBlocks.HANGAR_CONTROLLER.get(), HangarControllerBlockEntity::new);
            map.put(ModBlocks.SPENT_FUEL_STORAGE_CONTROLLER.get(), SpentFuelStorageBlockEntity::new);
            map.put(ModBlocks.CRYSTAL_GROWTH_CONTROLLER.get(), CrystalGrowthControllerBlockEntity::new);
            map.put(ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get(), EuvLithographyControllerBlockEntity::new);
            map.put(ModBlocks.COKE_OVEN_CONTROLLER.get(), CokeOvenControllerBlockEntity::new);
            controllerFactories = map;
        }
        return controllerFactories;
    }

    // Placed controllers with an active persistent preview toggle; refreshed periodically
    // by scanning loaded chunks near the player (block entities aren't otherwise enumerable).
    private static final List<BlockEntity> activePreviewControllers = new ArrayList<>();
    private static int scanCooldown = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null) {
            activePreviewControllers.clear();
            return;
        }

        if (--scanCooldown > 0) return;
        scanCooldown = 10;

        activePreviewControllers.clear();
        int cx = player.chunkPosition().x;
        int cz = player.chunkPosition().z;
        for (int dx = -SCAN_RADIUS_CHUNKS; dx <= SCAN_RADIUS_CHUNKS; dx++) {
            for (int dz = -SCAN_RADIUS_CHUNKS; dz <= SCAN_RADIUS_CHUNKS; dz++) {
                if (!level.hasChunk(cx + dx, cz + dz)) continue;
                LevelChunk chunk = level.getChunk(cx + dx, cz + dz);
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof IMultiblockPreview preview && preview.isPreviewActive()) {
                        activePreviewControllers.add(be);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cam = event.getCamera().getPosition();
        Camera camera = event.getCamera();

        renderHeldItemPreview(level, player, poseStack, bufferSource, cam, camera);

        for (BlockEntity be : activePreviewControllers) {
            if (!(be instanceof IMultiblockPreview preview) || be.isRemoved() || be.getLevel() == null) continue;
            BlockPos origin = be.getBlockPos();
            double distSq = player.distanceToSqr(origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5);
            if (distSq > (MAX_DISTANCE + 16) * (double) (MAX_DISTANCE + 16)) continue;
            renderPreview(level, player, poseStack, bufferSource, cam, camera, origin, preview.getPreviewPositions(origin));
        }

        bufferSource.endBatch();
    }

    private static void renderHeldItemPreview(ClientLevel level, LocalPlayer player, PoseStack poseStack,
                                               MultiBufferSource.BufferSource bufferSource, Vec3 cam, Camera camera) {
        Block controllerBlock = heldControllerBlock(player);
        if (controllerBlock == null) return;

        HitResult hit = Minecraft.getInstance().hitResult;
        if (!(hit instanceof BlockHitResult bhr) || bhr.getType() == HitResult.Type.MISS) return;

        BlockPos hitPos = bhr.getBlockPos();
        BlockPos target = level.getBlockState(hitPos).canBeReplaced() ? hitPos : hitPos.relative(bhr.getDirection());

        double distSq = player.distanceToSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        if (distSq > (double) MAX_DISTANCE * MAX_DISTANCE) return;

        BlockEntity dummy = controllerFactories().get(controllerBlock).apply(target, controllerBlock.defaultBlockState());
        if (!(dummy instanceof IMultiblockPreview preview)) return;

        renderPreview(level, player, poseStack, bufferSource, cam, camera, target, preview.getPreviewPositions(target));
    }

    private static Block heldControllerBlock(LocalPlayer player) {
        Block main = blockFromStack(player.getMainHandItem());
        if (main != null) return main;
        return blockFromStack(player.getOffhandItem());
    }

    private static Block blockFromStack(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem bi && controllerFactories().containsKey(bi.getBlock())) {
            return bi.getBlock();
        }
        return null;
    }

    private static void renderPreview(ClientLevel level, LocalPlayer player, PoseStack poseStack,
                                       MultiBufferSource.BufferSource bufferSource, Vec3 cam, Camera camera,
                                       BlockPos origin, Map<BlockPos, Block> positions) {
        VertexConsumer lineBuffer = bufferSource.getBuffer(RenderType.lines());
        boolean allValid = true;
        Block firstMissing = null;

        for (Map.Entry<BlockPos, Block> entry : positions.entrySet()) {
            BlockPos pos = entry.getKey();
            double distSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distSq > (double) MAX_DISTANCE * MAX_DISTANCE) continue;

            Block expected = entry.getValue();
            boolean correct = matchesExpected(level.getBlockState(pos).getBlock(), expected);
            if (correct) {
                renderBox(poseStack, lineBuffer, cam, pos, 0f, 1f, 0f, 0.6f);
            } else {
                renderBox(poseStack, lineBuffer, cam, pos, 1f, 0f, 0f, 0.6f);
                allValid = false;
                if (firstMissing == null) firstMissing = expected;
            }
        }

        String labelText = allValid
                ? "Structure: Valid"
                : "Structure: Invalid — missing " + firstMissing.getName().getString();
        int color = allValid ? 0x55FF55 : 0xFF5555;
        renderLabel(poseStack, bufferSource, cam, camera, origin, labelText, color);
    }

    // Any coil block satisfies a COIL preview slot, matching the EBF's own validation.
    private static boolean matchesExpected(Block found, Block expected) {
        if (found == expected) return true;
        return expected instanceof CoilBlock && found instanceof CoilBlock;
    }

    private static void renderBox(PoseStack poseStack, VertexConsumer buffer, Vec3 cam, BlockPos pos,
                                   float r, float g, float b, float a) {
        poseStack.pushPose();
        poseStack.translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        AABB box = new AABB(0.002, 0.002, 0.002, 0.998, 0.998, 0.998);
        LevelRenderer.renderLineBox(poseStack, buffer, box, r, g, b, a);
        poseStack.popPose();
    }

    private static void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cam, Camera camera,
                                     BlockPos origin, String text, int color) {
        double wx = origin.getX() + 0.5;
        double wy = origin.getY() + 2.2;
        double wz = origin.getZ() + 0.5;

        poseStack.pushPose();
        poseStack.translate(wx - cam.x, wy - cam.y, wz - cam.z);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);

        Font font = Minecraft.getInstance().font;
        Matrix4f matrix = poseStack.last().pose();
        float halfWidth = font.width(text) / 2f;
        font.drawInBatch(text, -halfWidth, 0, color, false, matrix, bufferSource,
                Font.DisplayMode.NORMAL, 0, 0xF000F0);

        poseStack.popPose();
    }
}
