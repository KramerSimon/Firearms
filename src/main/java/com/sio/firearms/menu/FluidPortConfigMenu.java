package com.sio.firearms.menu;

import com.sio.firearms.block.FluidPortBlockEntity;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class FluidPortConfigMenu extends AbstractContainerMenu {

    public final BlockPos pos;
    public String targetFluid;
    public FluidPortBlockEntity.Mode mode;

    public FluidPortConfigMenu(int id, Inventory inv, BlockPos pos, String targetFluid, FluidPortBlockEntity.Mode mode) {
        super(ModMenuTypes.FLUID_PORT_CONFIG_MENU.get(), id);
        this.pos = pos;
        this.targetFluid = targetFluid;
        this.mode = mode;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }

    @Override
    public boolean stillValid(Player player) { return true; }
}
