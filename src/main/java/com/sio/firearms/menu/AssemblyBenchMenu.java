package com.sio.firearms.menu;

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

public class AssemblyBenchMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public AssemblyBenchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(7), new SimpleContainerData(4));
    }

    public AssemblyBenchMenu(int containerId, Inventory playerInventory,
                             ItemStackHandler handler, ContainerData data) {
        super(ModMenuTypes.ASSEMBLY_BENCH_MENU.get(), containerId);
        this.data = data;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                addSlot(new SlotItemHandler(handler, row * 2 + col, 30 + col * 18, 17 + row * 18));
            }
        }

        addSlot(new SlotItemHandler(handler, 6, 124, 35) {
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

    public int getEnergyStored() {
        return data.get(0);
    }

    public int getMaxEnergy() {
        return data.get(1);
    }

    public int getProgress() {
        return data.get(2);
    }

    public int getMaxProgress() {
        return data.get(3);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
