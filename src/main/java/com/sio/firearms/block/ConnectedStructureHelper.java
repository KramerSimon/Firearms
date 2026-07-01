package com.sio.firearms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

/**
 * Shared helper multiblock controllers use to toggle {@link ModBlockStateProperties#CONNECTED}
 * across their shell once the structure validates (or stops validating), so wall/floor/roof
 * blocks can swap to a seamless "connected" texture. Controllers keep the previous position
 * set around so a change in shape (e.g. re-orientation) correctly un-connects blocks that fell
 * out of the structure without having to re-scan the whole level.
 */
public final class ConnectedStructureHelper {
    private ConnectedStructureHelper() {}

    /** Moves the shell from {@code previous} to {@code current}, only touching blocks whose
     *  membership actually changed. Returns {@code current} so callers can store it back. */
    public static Set<BlockPos> apply(Level level, Set<BlockPos> previous, Set<BlockPos> current) {
        if (level != null) {
            for (BlockPos pos : previous) {
                if (!current.contains(pos)) setConnected(level, pos, false);
            }
            for (BlockPos pos : current) {
                setConnected(level, pos, true);
            }
        }
        return current;
    }

    /** Un-connects every previously tracked block and returns an empty set to store back. */
    public static Set<BlockPos> clear(Level level, Set<BlockPos> previous) {
        if (level != null) {
            for (BlockPos pos : previous) setConnected(level, pos, false);
        }
        return new HashSet<>();
    }

    private static void setConnected(Level level, BlockPos pos, boolean value) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(ModBlockStateProperties.CONNECTED) && state.getValue(ModBlockStateProperties.CONNECTED) != value) {
            level.setBlock(pos, state.setValue(ModBlockStateProperties.CONNECTED, value), 3);
        }
    }

    public static Set<BlockPos> readPositions(CompoundTag tag, String key) {
        Set<BlockPos> set = new HashSet<>();
        ListTag list = tag.getList(key, Tag.TAG_LONG);
        for (int i = 0; i < list.size(); i++) set.add(BlockPos.of(((LongTag) list.get(i)).getAsLong()));
        return set;
    }

    public static void writePositions(CompoundTag tag, String key, Set<BlockPos> positions) {
        ListTag list = new ListTag();
        for (BlockPos pos : positions) list.add(LongTag.valueOf(pos.asLong()));
        tag.put(key, list);
    }
}
