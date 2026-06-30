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
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
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
    private final EnumMap<Direction, ItemStackHandler> sideFilters = new EnumMap<>(Direction.class);
    private int tickCount = 0;

    public ItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_PIPE.get(), pos, state);
        for (Direction dir : Direction.values()) {
            sideModes.put(dir, SideMode.NONE);
            sideFilters.put(dir, new ItemStackHandler(9) {
                @Override
                protected void onContentsChanged(int slot) {
                    setChanged();
                }
            });
        }
    }

    public ItemStackHandler getBuffer() { return buffer; }

    public SideMode getSideMode(Direction dir) {
        return sideModes.getOrDefault(dir, SideMode.NONE);
    }

    public void setSideMode(Direction dir, SideMode mode) {
        sideModes.put(dir, mode);
        setChanged();
    }

    public SideMode cycleSideMode(Direction dir) {
        SideMode next = sideModes.get(dir).next();
        sideModes.put(dir, next);
        setChanged();
        return next;
    }

    public ItemStackHandler getFilterHandler(Direction dir) {
        return sideFilters.get(dir);
    }

    /** Empty filter = pass all. Non-empty filter = only items matching at least one slot pass. */
    public boolean matchesFilter(Direction dir, ItemStack stack) {
        ItemStackHandler filter = sideFilters.get(dir);
        boolean hasAnyFilter = false;
        for (int i = 0; i < 9; i++) {
            ItemStack f = filter.getStackInSlot(i);
            if (!f.isEmpty()) {
                hasAnyFilter = true;
                if (ItemStack.isSameItem(f, stack)) return true;
            }
        }
        return !hasAnyFilter;
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
                    String blockClass = level.getBlockState(neighborPos).getBlock().getClass().getSimpleName();
                    String beClass    = be == null ? "null" : be.getClass().getSimpleName();
                    LOGGER.info("[ItemPipe] EXTRACT neighbor @ {} | block={} be={}", neighborPos, blockClass, beClass);
                }

                if (be == null || be instanceof ItemPipeBlockEntity) continue;

                IItemHandler capHandler = level.getCapability(
                        Capabilities.ItemHandler.BLOCK, neighborPos, dir.getOpposite());
                boolean isContainer = be instanceof Container;
                IItemHandler inv = capHandler;
                if (inv == null && isContainer) {
                    inv = new InvWrapper((Container) be);
                }

                if (doLog) {
                    LOGGER.info("[ItemPipe]   cap={} isContainer={} resolvedHandler={}",
                            capHandler != null ? "non-null" : "null",
                            isContainer,
                            inv != null ? "found via " + (capHandler != null ? "capability" : "InvWrapper") + " (" + inv.getSlots() + " slots)" : "null");
                    if (inv != null) {
                        for (int s = 0; s < inv.getSlots(); s++) {
                            LOGGER.info("[ItemPipe]     slot[{}] = {}", s, inv.getStackInSlot(s));
                        }
                    }
                }

                if (inv == null) continue;

                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    ItemStack sim = inv.extractItem(slot, 1, true);
                    if (!sim.isEmpty() && matchesFilter(dir, sim)) {
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

                if (!matchesFilter(dir, buffer.getStackInSlot(0))) continue;

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
                ItemStack remainder = ItemHandlerHelper.insertItem(inv, toInsert, false);
                if (remainder.isEmpty()) {
                    buffer.setStackInSlot(0, ItemStack.EMPTY);
                    changed = true;
                    break;
                }
            }
        }

        // Pipe-to-pipe propagation
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

        CompoundTag modes = new CompoundTag();
        for (Direction dir : Direction.values()) {
            modes.putString(dir.getSerializedName(), sideModes.get(dir).name());
        }
        tag.put("SideModes", modes);

        CompoundTag filters = new CompoundTag();
        for (Direction dir : Direction.values()) {
            filters.put(dir.getSerializedName(), sideFilters.get(dir).serializeNBT(registries));
        }
        tag.put("SideFilters", filters);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Buffer")) {
            buffer.deserializeNBT(registries, tag.getCompound("Buffer"));
        }
        if (tag.contains("SideModes")) {
            CompoundTag modes = tag.getCompound("SideModes");
            for (Direction dir : Direction.values()) {
                String name = modes.getString(dir.getSerializedName());
                sideModes.put(dir, name.isEmpty() ? SideMode.NONE : SideMode.valueOf(name));
            }
            LOGGER.info("[ItemPipe] loadAdditional @ {} | modes: down={} up={} north={} south={} west={} east={}",
                    worldPosition,
                    sideModes.get(Direction.DOWN).name(),
                    sideModes.get(Direction.UP).name(),
                    sideModes.get(Direction.NORTH).name(),
                    sideModes.get(Direction.SOUTH).name(),
                    sideModes.get(Direction.WEST).name(),
                    sideModes.get(Direction.EAST).name());
        } else {
            LOGGER.info("[ItemPipe] loadAdditional @ {} | no SideModes tag, all defaulting to NONE", worldPosition);
        }
        if (tag.contains("SideFilters")) {
            CompoundTag filters = tag.getCompound("SideFilters");
            for (Direction dir : Direction.values()) {
                if (filters.contains(dir.getSerializedName())) {
                    sideFilters.get(dir).deserializeNBT(registries, filters.getCompound(dir.getSerializedName()));
                }
            }
        }
    }
}
