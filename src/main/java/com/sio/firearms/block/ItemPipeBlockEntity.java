package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.EnumMap;

public class ItemPipeBlockEntity extends BlockEntity {

    private static final Logger LOGGER = LogUtils.getLogger();

    public enum SideMode {
        NONE("None"),
        EXTRACT("Extract"),
        INSERT("Insert");

        private final String displayName;

        SideMode(String displayName) { this.displayName = displayName; }

        public String displayName() { return displayName; }

        public SideMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private final ItemStackHandler buffer = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final EnumMap<Direction, SideMode> sideModes = new EnumMap<>(Direction.class);
    private int tickCount = 0;

    public ItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_PIPE.get(), pos, state);
        for (Direction dir : Direction.values()) {
            sideModes.put(dir, SideMode.NONE);
        }
    }

    public ItemStackHandler getBuffer() {
        return buffer;
    }

    public SideMode getSideMode(Direction dir) {
        return sideModes.getOrDefault(dir, SideMode.NONE);
    }

    public SideMode cycleSideMode(Direction dir) {
        SideMode next = sideModes.get(dir).next();
        sideModes.put(dir, next);
        setChanged();
        return next;
    }

    public void dropBuffer(Level level, BlockPos pos) {
        ItemStack item = buffer.getStackInSlot(0);
        if (!item.isEmpty()) {
            Block.popResource(level, pos, item.copy());
            buffer.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    public void serverTick() {
        if (level == null) return;
        if (++tickCount % 4 != 0) return;

        boolean changed = false;

        boolean doLog = (tickCount % 20 == 0);

        // ── DEBUG: log state every 20 ticks ──────────────────────────────────
        if (doLog) {
            LOGGER.info("[ItemPipe] @ {} | buffer={}", worldPosition, buffer.getStackInSlot(0));
            for (Direction dir : Direction.values()) {
                LOGGER.info("[ItemPipe]   side {} = {}", dir.getSerializedName(), sideModes.get(dir).name());
            }
        }

        // EXTRACT: pull 1 item FROM adjacent inventory INTO pipe buffer
        if (buffer.getStackInSlot(0).isEmpty()) {
            for (Direction dir : Direction.values()) {
                SideMode mode = sideModes.get(dir);

                if (doLog) {
                    LOGGER.info("[ItemPipe] EXTRACT check {} | mode={}", dir.getSerializedName(), mode.name());
                }

                if (mode != SideMode.EXTRACT) continue;

                BlockPos neighborPos = worldPosition.relative(dir);
                BlockEntity be = level.getBlockEntity(neighborPos);

                if (doLog) {
                    LOGGER.info("[ItemPipe]   neighbor @ {} = {}", neighborPos,
                            be == null ? "null" : be.getClass().getSimpleName());
                }

                if (be == null || be instanceof ItemPipeBlockEntity) continue;

                // Try NeoForge capability first; fall back to InvWrapper for vanilla containers
                IItemHandler inv = level.getCapability(
                        Capabilities.ItemHandler.BLOCK, neighborPos, dir.getOpposite());
                if (inv == null && be instanceof Container container) {
                    inv = new InvWrapper(container);
                }

                if (doLog) {
                    if (inv == null) {
                        LOGGER.info("[ItemPipe]   cap=null (no IItemHandler or Container on {} face)", dir.getOpposite().getSerializedName());
                    } else {
                        LOGGER.info("[ItemPipe]   cap=found via {} ({} slots)",
                                inv instanceof InvWrapper ? "InvWrapper" : "capability",
                                inv.getSlots());
                        for (int s = 0; s < inv.getSlots(); s++) {
                            LOGGER.info("[ItemPipe]     slot[{}] = {}", s, inv.getStackInSlot(s));
                        }
                    }
                }

                if (inv == null) continue;

                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    // Simulate first; only execute if the slot is non-empty
                    ItemStack sim = inv.extractItem(slot, 1, true);
                    if (!sim.isEmpty()) {
                        ItemStack extracted = inv.extractItem(slot, 1, false);
                        if (!extracted.isEmpty()) {
                            buffer.setStackInSlot(0, extracted);
                            changed = true;
                        }
                        break;
                    }
                }
                if (!buffer.getStackInSlot(0).isEmpty()) break;
            }
        }

        // INSERT: push items FROM pipe buffer INTO adjacent inventory
        if (!buffer.getStackInSlot(0).isEmpty()) {
            for (Direction dir : Direction.values()) {
                if (sideModes.get(dir) != SideMode.INSERT) continue;

                BlockPos neighborPos = worldPosition.relative(dir);
                BlockEntity be = level.getBlockEntity(neighborPos);
                if (be == null || be instanceof ItemPipeBlockEntity) continue;

                IItemHandler inv = level.getCapability(
                        Capabilities.ItemHandler.BLOCK, neighborPos, dir.getOpposite());
                if (inv == null && be instanceof Container container) {
                    inv = new InvWrapper(container);
                }

                if (doLog) {
                    LOGGER.info("[ItemPipe] INSERT {} -> neighbor={} cap={} item={}",
                            dir.getSerializedName(),
                            be.getClass().getSimpleName(),
                            inv != null ? (inv instanceof InvWrapper ? "InvWrapper" : "found") : "null",
                            buffer.getStackInSlot(0));
                }

                if (inv == null) continue;

                ItemStack toInsert = buffer.getStackInSlot(0).copy();
                // ItemHandlerHelper.insertItem tries all slots and returns whatever it couldn't insert
                ItemStack remainder = ItemHandlerHelper.insertItem(inv, toInsert, false);
                if (remainder.isEmpty()) {
                    // All items were accepted — clear the pipe buffer
                    buffer.setStackInSlot(0, ItemStack.EMPTY);
                    changed = true;
                    break;
                }
            }
        }

        // Pipe-to-pipe propagation: pass buffer to any adjacent pipe that has space
        if (!buffer.getStackInSlot(0).isEmpty()) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = worldPosition.relative(dir);
                if (!(level.getBlockEntity(neighborPos) instanceof ItemPipeBlockEntity neighbor)) continue;
                if (!neighbor.buffer.getStackInSlot(0).isEmpty()) continue;
                neighbor.buffer.setStackInSlot(0, buffer.getStackInSlot(0).copy());
                neighbor.setChanged();
                buffer.setStackInSlot(0, ItemStack.EMPTY);
                changed = true;
                break;
            }
        }

        if (changed) setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Buffer", buffer.serializeNBT(registries));
        CompoundTag modesTag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            modesTag.putString(dir.getSerializedName(), sideModes.get(dir).name());
        }
        tag.put("SideModes", modesTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Buffer")) buffer.deserializeNBT(registries, tag.getCompound("Buffer"));
        if (tag.contains("SideModes")) {
            CompoundTag modesTag = tag.getCompound("SideModes");
            for (Direction dir : Direction.values()) {
                String val = modesTag.getString(dir.getSerializedName());
                try {
                    sideModes.put(dir, SideMode.valueOf(val));
                } catch (IllegalArgumentException e) {
                    sideModes.put(dir, SideMode.NONE);
                }
            }
        }
    }
}
