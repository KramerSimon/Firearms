package com.sio.firearms.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Renders a translucent ghost preview of a multiblock structure: either around the
 * position the player is aiming at (while holding a controller item), or continuously
 * around a placed controller whose {@link IMultiblockPreview#isPreviewActive()} is on
 * (toggled with a wrench). Each required position is rendered as a translucent copy of
 * its expected block's actual model, tinted green (correct block already placed), red
 * (wrong block placed), blue (nothing placed yet) or yellow (the controller's own cell).
 */
@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MultiblockPreviewRenderer {

    private static final int MAX_DISTANCE = 20;
    private static final int SCAN_RADIUS_CHUNKS = 3;
    private static final float GHOST_ALPHA = 0.4f;

    // A translucent, non-culled, non-depth-writing variant of the block render type, used
    // to draw ghost copies of block models without them permanently occluding real geometry
    // or z-fighting against each other. Built from vanilla RenderStateShard constants only,
    // so unlike controllerFactories() below this is safe to initialise eagerly at class load.
    private static final RenderType GHOST_BLOCK = RenderType.create(
            "firearms_ghost_block",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
                    .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                    .createCompositeState(false)
    );

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
            renderPreview(level, player, poseStack, bufferSource, cam, camera, origin,
                    be.getBlockState().getBlock(), preview.getPreviewPositions(origin));
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

        renderPreview(level, player, poseStack, bufferSource, cam, camera, target, controllerBlock, preview.getPreviewPositions(target));
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
                                       BlockPos origin, Block controllerBlock, Map<BlockPos, Block> positions) {
        boolean allValid = true;
        Block firstMissing = null;

        for (Map.Entry<BlockPos, Block> entry : positions.entrySet()) {
            BlockPos pos = entry.getKey();
            double distSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distSq > (double) MAX_DISTANCE * MAX_DISTANCE) continue;

            Block expected = entry.getValue();
            BlockState foundState = level.getBlockState(pos);
            boolean correct = matchesExpected(foundState.getBlock(), expected);
            if (correct) {
                renderGhostBlock(level, poseStack, bufferSource, cam, pos, expected.defaultBlockState(), 0.5f, 1.0f, 0.5f, GHOST_ALPHA);
            } else if (foundState.isAir()) {
                renderGhostBlock(level, poseStack, bufferSource, cam, pos, expected.defaultBlockState(), 0.7f, 0.7f, 1.0f, GHOST_ALPHA);
                allValid = false;
                if (firstMissing == null) firstMissing = expected;
            } else {
                renderGhostBlock(level, poseStack, bufferSource, cam, pos, expected.defaultBlockState(), 1.0f, 0.5f, 0.5f, GHOST_ALPHA);
                allValid = false;
                if (firstMissing == null) firstMissing = expected;
            }
        }

        double originDistSq = player.distanceToSqr(origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5);
        if (originDistSq <= (double) MAX_DISTANCE * MAX_DISTANCE) {
            renderGhostBlock(level, poseStack, bufferSource, cam, origin, controllerBlock.defaultBlockState(), 1.0f, 1.0f, 0.3f, GHOST_ALPHA);
        }

        String labelText = allValid
                ? "Structure: Valid"
                : "Structure: Invalid — missing " + firstMissing.getName().getString();
        int color = allValid ? 0x55FF55 : 0xFF5555;
        renderLabel(poseStack, bufferSource, cam, camera, origin, labelText, color, 2.2);

        renderHoverTooltip(player, poseStack, bufferSource, cam, camera, origin, controllerBlock, positions);
    }

    // Any coil block satisfies a COIL preview slot, matching the EBF's own validation.
    private static boolean matchesExpected(Block found, Block expected) {
        if (found == expected) return true;
        return expected instanceof CoilBlock && found instanceof CoilBlock;
    }

    private static void renderGhostBlock(ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                          Vec3 cam, BlockPos pos, BlockState state, float r, float g, float b, float a) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer buffer = bufferSource.getBuffer(GHOST_BLOCK);
        VertexConsumer tinted = new TintedVertexConsumer(buffer, r, g, b, a);

        poseStack.pushPose();
        poseStack.translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        dispatcher.renderBatched(state, pos, level, poseStack, tinted, false, RandomSource.create());
        poseStack.popPose();
    }

    // Finds the ghost position the player's crosshair is currently over (among the
    // structure's required positions plus the controller's own cell) and, if found,
    // shows a small floating label with that block's display name.
    private static void renderHoverTooltip(LocalPlayer player, PoseStack poseStack, MultiBufferSource bufferSource,
                                            Vec3 cam, Camera camera, BlockPos origin, Block controllerBlock,
                                            Map<BlockPos, Block> positions) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0f);
        Vec3 reach = eye.add(look.scale(MAX_DISTANCE));

        BlockPos hovered = null;
        Block hoveredBlock = null;
        double closestDistSq = Double.MAX_VALUE;

        Map<BlockPos, Block> withController = new HashMap<>(positions);
        withController.put(origin, controllerBlock);

        for (Map.Entry<BlockPos, Block> entry : withController.entrySet()) {
            BlockPos pos = entry.getKey();
            AABB box = new AABB(pos);
            Optional<Vec3> hit = box.clip(eye, reach);
            if (hit.isEmpty()) continue;
            double d = hit.get().distanceToSqr(eye);
            if (d < closestDistSq) {
                closestDistSq = d;
                hovered = pos;
                hoveredBlock = entry.getValue();
            }
        }

        if (hovered != null) {
            renderLabel(poseStack, bufferSource, cam, camera, hovered, hoveredBlock.getName().getString(), 0xFFFFFF, 1.35);
        }
    }

    private static void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cam, Camera camera,
                                     BlockPos origin, String text, int color, double yOffset) {
        double wx = origin.getX() + 0.5;
        double wy = origin.getY() + yOffset;
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

    // Wraps a VertexConsumer so every vertex colour written by the block model renderer is
    // multiplied by a fixed tint (r,g,b,a), producing a coloured translucent ghost without
    // needing to touch the model/quad data itself.
    private static final class TintedVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float r, g, b, a;

        TintedVertexConsumer(VertexConsumer delegate, float r, float g, float b, float a) {
            this.delegate = delegate;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            delegate.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            delegate.setColor((int) (red * r), (int) (green * g), (int) (blue * b), (int) (alpha * a));
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            delegate.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            delegate.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            delegate.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
            delegate.setNormal(normalX, normalY, normalZ);
            return this;
        }
    }
}
