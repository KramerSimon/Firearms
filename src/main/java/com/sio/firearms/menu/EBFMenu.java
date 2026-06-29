package com.sio.firearms.menu;

import com.sio.firearms.block.EBFControllerBlockEntity;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * Info-only menu for the Electric Blast Furnace controller. It holds no item slots —
 * material flows through the import / output buses — and only syncs status data
 * (energy, temperature, progress, structure state) for the screen to display.
 */
public class EBFMenu extends AbstractContainerMenu {

    // Button id sent from the screen to toggle smelting on/off.
    public static final int BUTTON_TOGGLE = 0;

    private final ContainerData data;
    private final EBFControllerBlockEntity blockEntity; // server-side only; null on the client

    public EBFMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainerData(9), null);
    }

    public EBFMenu(int containerId, Inventory playerInventory, ContainerData data,
                   EBFControllerBlockEntity blockEntity) {
        super(ModMenuTypes.EBF_MENU.get(), containerId);
        this.data = data;
        this.blockEntity = blockEntity;
        addDataSlots(data);
    }

    public int getEnergyStored()      { return data.get(0); }
    public int getMaxEnergy()         { return data.get(1); }
    public int getProgress()          { return data.get(2); }
    public int getMaxProgress()       { return data.get(3); }
    public boolean isStructureValid() { return data.get(4) == 1; }
    public int getCoilTemperature()   { return data.get(5); }
    public int getRequiredTemp()      { return data.get(6); }
    public boolean isActive()         { return data.get(7) == 1; }
    public boolean isEnabled()        { return data.get(8) == 1; }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_TOGGLE && blockEntity != null) {
            blockEntity.toggleEnabled();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) { return true; }
}
