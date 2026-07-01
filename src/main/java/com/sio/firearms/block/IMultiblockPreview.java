package com.sio.firearms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * Implemented by multiblock controller BlockEntities so the client-side ghost
 * preview renderer can show players where structure blocks are expected.
 * {@code origin} is always the controller's own position (placed or hypothetical);
 * each implementation maps that onto one canonical valid layout of its structure,
 * mirroring the coordinates used by its checkStructure() validation.
 */
public interface IMultiblockPreview {

    /** Required position → expected block, relative to {@code origin} (the controller's position). */
    Map<BlockPos, Block> getPreviewPositions(BlockPos origin);

    boolean isPreviewActive();

    void setPreviewActive(boolean active);
}
