package com.sio.firearms.menu;

import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemPipeFilterMenu extends AbstractContainerMenu {

    public static final int FILTER_SLOTS = 9;

    public final BlockPos pos;
    public final Direction face;

    private final SimpleContainer filterContainer;
    // 0 = mode ordinal (NONE=0, EXTRACT=1, INSERT=2)
    private final ContainerData data;

    /** Client-side constructor: called with the data written by the server into FriendlyByteBuf. */
    public ItemPipeFilterMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv,
                buf.readBlockPos(),
                Direction.values()[buf.readByte()],
                new SimpleContainer(FILTER_SLOTS),
                new SimpleContainerData(1));
    }

    /** Server-side constructor: opens for a specific face and pre-populates filter from the BE. */
    public static ItemPipeFilterMenu openFor(int id, Inventory playerInv, BlockPos pos, Direction face,
                                              ItemPipeBlockEntity pipe) {
        SimpleContainer filter = new SimpleContainer(FILTER_SLOTS);
        for (int i = 0; i < FILTER_SLOTS; i++) {
            filter.setItem(i, pipe.getFilterHandler(face).getStackInSlot(i).copy());
        }
        ContainerData data = new ContainerData() {
            @Override public int get(int index) {
                return pipe.getSideMode(face).ordinal();
            }
            @Override public void set(int index, int value) { /* controlled via packet */ }
            @Override public int getCount() { return 1; }
        };
        ItemPipeFilterMenu menu = new ItemPipeFilterMenu(id, playerInv, pos, face, filter, data);
        // Register a listener so changes to filter slots flow back to the BE immediately
        filter.addListener(c -> {
            for (int i = 0; i < FILTER_SLOTS; i++) {
                pipe.getFilterHandler(face).setStackInSlot(i, c.getItem(i).copy());
            }
            pipe.setChanged();
        });
        return menu;
    }

    private ItemPipeFilterMenu(int id, Inventory playerInv, BlockPos pos, Direction face,
                                SimpleContainer filterContainer, ContainerData data) {
        super(ModMenuTypes.ITEM_PIPE_FILTER_MENU.get(), id);
        this.pos = pos;
        this.face = face;
        this.filterContainer = filterContainer;
        this.data = data;
        addDataSlots(data);

        // 3×3 filter slots — centered in the GUI
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(filterContainer, row * 3 + col, 44 + col * 18, 18 + row * 18));
            }
        }

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 90 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 148));
        }
    }

    /** Mode ordinal: 0=NONE, 1=EXTRACT, 2=INSERT */
    public int getModeOrdinal() {
        return data.get(0);
    }

    public ItemPipeBlockEntity.SideMode getMode() {
        int ord = getModeOrdinal();
        ItemPipeBlockEntity.SideMode[] values = ItemPipeBlockEntity.SideMode.values();
        return (ord >= 0 && ord < values.length) ? values[ord] : ItemPipeBlockEntity.SideMode.NONE;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Server side: write filter container back to BE (already handled by listener, but belt-and-suspenders)
        if (!player.level().isClientSide()) {
            BlockEntity be = player.level().getBlockEntity(pos);
            if (be instanceof ItemPipeBlockEntity pipe) {
                for (int i = 0; i < FILTER_SLOTS; i++) {
                    pipe.getFilterHandler(face).setStackInSlot(i, filterContainer.getItem(i).copy());
                }
                pipe.setChanged();
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < FILTER_SLOTS) {
            // Filter slot → player inventory
            if (!moveItemStackTo(stack, FILTER_SLOTS, FILTER_SLOTS + 36, false)) return ItemStack.EMPTY;
        } else {
            // Player inventory → filter slots
            if (!moveItemStackTo(stack, 0, FILTER_SLOTS, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }
}
