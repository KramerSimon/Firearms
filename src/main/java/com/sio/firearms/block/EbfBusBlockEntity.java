package com.sio.firearms.block;

import com.sio.firearms.menu.EbfBusMenu;
import com.sio.firearms.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Shared base for the EBF item hatches (import / output bus). Holds a small buffer
 * exposed to pipes and hoppers, locates the controller of the multiblock it is part
 * of via a bounded flood-fill over EBF structure blocks, and moves items to/from it
 * each tick while the furnace is formed.
 */
public abstract class EbfBusBlockEntity extends BlockEntity implements MenuProvider {

    private static final int MAX_BFS_DEPTH   = 12;
    private static final int RESCAN_INTERVAL = 40;

    protected final ItemStackHandler buffer = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    private int tickCount = 0;
    private BlockPos cachedControllerPos = null;

    protected EbfBusBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStackHandler getBuffer() { return buffer; }

    // ── Read-only viewer GUI ────────────────────────────────────────────────────
    // Shows the buffer contents; items only move through pipes, never by hand.
    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EbfBusMenu(containerId, playerInventory, buffer);
    }

    public void serverTick() {
        if (level == null) return;
        tickCount++;

        if (cachedControllerPos == null || tickCount % RESCAN_INTERVAL == 0) {
            cachedControllerPos = findController();
        }
        if (cachedControllerPos == null) return;

        if (level.getBlockEntity(cachedControllerPos) instanceof EBFControllerBlockEntity ctrl
                && ctrl.isStructureValid()) {
            transfer(ctrl);
        } else {
            cachedControllerPos = null;
        }
    }

    /** Move items between this bus's buffer and the controller. */
    protected abstract void transfer(EBFControllerBlockEntity controller);

    private BlockPos findController() {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(worldPosition);
        queue.add(worldPosition);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos nb = current.relative(dir);
                if (visited.contains(nb)) continue;
                if (nb.distManhattan(worldPosition) > MAX_BFS_DEPTH) continue;
                visited.add(nb);

                if (level.getBlockEntity(nb) instanceof EBFControllerBlockEntity) return nb;
                if (isEbfStructureBlock(level.getBlockState(nb).getBlock())) queue.add(nb);
            }
        }
        return null;
    }

    private static boolean isEbfStructureBlock(Block block) {
        return block == ModBlocks.BLAST_FURNACE_CASING.get()
                || block == ModBlocks.MUFFLER_HATCH.get()
                || block == ModBlocks.KANTHAL_COIL.get()
                || block == ModBlocks.NICHROME_COIL.get()
                || block == ModBlocks.TUNGSTEN_COIL.get()
                || block == ModBlocks.EBF_CONTROLLER.get()
                || block == ModBlocks.EBF_IMPORT_BUS.get()
                || block == ModBlocks.EBF_OUTPUT_BUS.get()
                || block == ModBlocks.ENERGY_PORT.get()
                || block == ModBlocks.FLUID_PORT.get();
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < buffer.getSlots(); i++) {
            if (!buffer.getStackInSlot(i).isEmpty()) {
                Block.popResource(level, pos, buffer.getStackInSlot(i));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Buffer", buffer.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Buffer")) buffer.deserializeNBT(registries, tag.getCompound("Buffer"));
    }
}
