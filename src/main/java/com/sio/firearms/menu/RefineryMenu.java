package com.sio.firearms.menu;

import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RefineryMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public RefineryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(5), new SimpleContainerData(7));
    }

    public RefineryMenu(int containerId, Inventory playerInventory,
                        ItemStackHandler handler, ContainerData data) {
        super(ModMenuTypes.REFINERY_MENU.get(), containerId);
        this.data = data;

        addSlot(new SlotItemHandler(handler, 0, 26, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.OIL_BUCKET.get());
            }
        });

        addSlot(new SlotItemHandler(handler, 1, 26, 50) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addSlot(new SlotItemHandler(handler, 2, 130, 14) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addSlot(new SlotItemHandler(handler, 3, 130, 50) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addSlot(new SlotItemHandler(handler, 4, 130, 32) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    public int getEnergyStored() { return data.get(0); }
    public int getMaxEnergy() { return data.get(1); }
    public int getOilAmount() { return data.get(2); }
    public int getFuelAmount() { return data.get(3); }
    public boolean isStructureValid() { return data.get(4) == 1; }
    public int getProgress() { return data.get(5); }
    public int getMaxProgress() { return data.get(6); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
