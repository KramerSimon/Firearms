package com.sio.firearms.item;

import com.sio.firearms.block.LandMineBlock;
import com.sio.firearms.entity.SeaMineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class MetalDetectorItem extends Item {

    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int SCAN_RADIUS_XZ = 5;
    private static final int SCAN_RADIUS_Y = 3;
    private static final double MAX_DETECT_RANGE = 5.0;

    // Client-only detection state, shared with the HUD overlay in OverlayRenderer.
    @Nullable private static BlockPos nearestMinePos = null;
    private static double nearestDistance = Double.MAX_VALUE;
    private static long nextBeepTick = 0;

    public MetalDetectorItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) return;
        if (!(entity instanceof Player player)) return;
        boolean held = stack == player.getMainHandItem() || stack == player.getOffhandItem();
        if (!held) return;

        long gameTime = level.getGameTime();

        if (gameTime % SCAN_INTERVAL_TICKS == 0) {
            scanForMines(level, player);
        }

        if (nearestMinePos == null) return;

        if (gameTime >= nextBeepTick) {
            float pitch = 1.0f + (float) ((MAX_DETECT_RANGE - nearestDistance) / MAX_DETECT_RANGE);
            level.playSound(player, player.blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(),
                    SoundSource.PLAYERS, 0.5f, pitch);
            level.addParticle(ParticleTypes.END_ROD,
                    nearestMinePos.getX() + 0.5, nearestMinePos.getY() + 0.3, nearestMinePos.getZ() + 0.5,
                    0.0, 0.01, 0.0);

            double t = Math.max(0.0, Math.min(1.0, nearestDistance / MAX_DETECT_RANGE));
            int interval = (int) Math.round(2 + t * 38); // 2 ticks (near-continuous) at 1 block, 40 ticks (2s) at 5 blocks
            nextBeepTick = gameTime + interval;
        }
    }

    private static void scanForMines(Level level, Player player) {
        BlockPos center = player.blockPosition();
        BlockPos minPos = center.offset(-SCAN_RADIUS_XZ, -SCAN_RADIUS_Y, -SCAN_RADIUS_XZ);
        BlockPos maxPos = center.offset(SCAN_RADIUS_XZ, SCAN_RADIUS_Y, SCAN_RADIUS_XZ);

        BlockPos closest = null;
        double closestDistSq = Double.MAX_VALUE;

        for (BlockPos bp : BlockPos.betweenClosed(minPos, maxPos)) {
            if (!(level.getBlockState(bp).getBlock() instanceof LandMineBlock)) continue;
            if (!level.getBlockState(bp).getValue(LandMineBlock.HIDDEN)) continue;
            double distSq = player.distanceToSqr(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = bp.immutable();
            }
        }

        AABB scanBox = new AABB(minPos.getX(), minPos.getY(), minPos.getZ(),
                maxPos.getX() + 1, maxPos.getY() + 1, maxPos.getZ() + 1);
        for (SeaMineEntity mine : level.getEntitiesOfClass(SeaMineEntity.class, scanBox)) {
            if (!mine.isHidden()) continue;
            double distSq = player.distanceToSqr(mine);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = mine.blockPosition();
            }
        }

        double dist = Math.sqrt(closestDistSq);
        if (closest != null && dist <= MAX_DETECT_RANGE) {
            nearestMinePos = closest;
            nearestDistance = dist;
        } else {
            nearestMinePos = null;
            nearestDistance = Double.MAX_VALUE;
        }
    }

    @Nullable
    public static BlockPos getNearestMinePos() {
        return nearestMinePos;
    }

    public static double getNearestDistance() {
        return nearestDistance;
    }
}
