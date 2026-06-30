package com.sio.firearms.menu;

import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Unified item-pipe config menu — shows all 6 faces from one screen.
 * Holds 9 filter slots for the currently-active face; switching faces is done
 * by SwitchItemPipeFacePayload which calls switchFace() on the server copy.
 *
 * ContainerData layout (7 ints):
 *   [0..5] = SideMode ordinals for Direction.values()[i]
 *   [6]    = activeFaceOrdinal (synced to client so face buttons highlight correctly)
 */
public class ItemPipeUnifiedMenu extends AbstractContainerMenu {

    public static final int FILTER_SLOTS = 9;

    // Slot layout constants (screen-relative, shared with ItemPipeUnifiedScreen)
    public static final int FILTER_X = 90;
    public static final int FILTER_Y = 62;
    public static final int INV_X    = 47;
    public static final int INV_Y    = 122;
    public static final int HOTBAR_Y = 180;

    public final BlockPos pos;
    private final SimpleContainer filterContainer;
    private final ContainerData data;

    // Face modes: read from pipe on server, from ContainerData sync on client
    private final int[] cachedModes = new int[6];
    // Which face's filter slots are currently shown
    private int activeFaceOrdinal = Direction.NORTH.ordinal();

    // Non-null only on the server-side instance; null on client
    private @Nullable ItemPipeBlockEntity pipeRef;

    /** Client-side constructor: slots start empty and are synced from server. */
    public ItemPipeUnifiedMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, pos, new SimpleContainer(FILTER_SLOTS));
    }

    /**
     * Server-side factory: pre-loads the active (NORTH) face's filter slots and
     * wires a live ContainerData so face modes + activeFaceOrdinal sync to client.
     */
    public static ItemPipeUnifiedMenu openFor(int id, Inventory playerInv, BlockPos pos,
                                              ItemPipeBlockEntity pipe) {
        SimpleContainer filter = new SimpleContainer(FILTER_SLOTS);
        Direction startFace = Direction.NORTH;
        for (int i = 0; i < FILTER_SLOTS; i++) {
            filter.setItem(i, pipe.getFilterHandler(startFace).getStackInSlot(i).copy());
        }
        ItemPipeUnifiedMenu menu = new ItemPipeUnifiedMenu(id, playerInv, pos, filter);
        menu.pipeRef = pipe;
        // Propagate filter slot edits back to the BE immediately
        filter.addListener(c -> {
            if (menu.pipeRef == null) return;
            Direction face = Direction.values()[menu.activeFaceOrdinal];
            for (int i = 0; i < FILTER_SLOTS; i++) {
                menu.pipeRef.getFilterHandler(face).setStackInSlot(i, c.getItem(i).copy());
            }
            menu.pipeRef.setChanged();
        });
        return menu;
    }

    private ItemPipeUnifiedMenu(int id, Inventory playerInv, BlockPos pos, SimpleContainer fc) {
        super(ModMenuTypes.ITEM_PIPE_UNIFIED_MENU.get(), id);
        this.pos = pos;
        this.filterContainer = fc;

        // Server: reads live from pipeRef; client: reads from cachedModes updated by server sync
        this.data = new ContainerData() {
            @Override public int get(int index) {
                if (index < 6) {
                    return pipeRef != null
                            ? pipeRef.getSideMode(Direction.values()[index]).ordinal()
                            : cachedModes[index];
                }
                return activeFaceOrdinal;
            }
            @Override public void set(int index, int value) {
                if (index < 6) cachedModes[index] = value;
                else activeFaceOrdinal = value;
            }
            @Override public int getCount() { return 7; }
        };
        addDataSlots(data);

        // 3×3 filter slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(filterContainer, row * 3 + col,
                        FILTER_X + col * 18, FILTER_Y + row * 18));
            }
        }
        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9,
                        INV_X + col * 18, INV_Y + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, INV_X + col * 18, HOTBAR_Y));
        }
    }

    /**
     * Called by SwitchItemPipeFacePayload handler on the server.
     * Saves current filter slots to old face, loads new face, and immediately
     * broadcasts the slot changes to the client.
     */
    public void switchFace(int newFaceOrdinal, ItemPipeBlockEntity pipe) {
        Direction oldFace = Direction.values()[activeFaceOrdinal];
        for (int i = 0; i < FILTER_SLOTS; i++) {
            pipe.getFilterHandler(oldFace).setStackInSlot(i, filterContainer.getItem(i).copy());
        }
        Direction newFace = Direction.values()[newFaceOrdinal];
        for (int i = 0; i < FILTER_SLOTS; i++) {
            filterContainer.setItem(i, pipe.getFilterHandler(newFace).getStackInSlot(i).copy());
        }
        activeFaceOrdinal = newFaceOrdinal;
        pipe.setChanged();
        broadcastChanges(); // push updated slots + ContainerData[6] to client
    }

    /** Current active face ordinal (client-side value is synced via ContainerData[6]). */
    public int getActiveFaceOrdinal() { return activeFaceOrdinal; }

    /** Mode for the given face, reading live on server or from synced cache on client. */
    public ItemPipeBlockEntity.SideMode getModeForFace(Direction dir) {
        int ord = data.get(dir.ordinal());
        ItemPipeBlockEntity.SideMode[] values = ItemPipeBlockEntity.SideMode.values();
        return (ord >= 0 && ord < values.length) ? values[ord] : ItemPipeBlockEntity.SideMode.NONE;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide() && pipeRef != null) {
            Direction face = Direction.values()[activeFaceOrdinal];
            for (int i = 0; i < FILTER_SLOTS; i++) {
                pipeRef.getFilterHandler(face).setStackInSlot(i, filterContainer.getItem(i).copy());
            }
            pipeRef.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < FILTER_SLOTS) {
            if (!moveItemStackTo(stack, FILTER_SLOTS, FILTER_SLOTS + 36, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, FILTER_SLOTS, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }
}
