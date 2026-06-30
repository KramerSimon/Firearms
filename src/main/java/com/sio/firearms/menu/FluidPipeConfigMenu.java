package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FluidPipeConfigMenu extends AbstractContainerMenu {

    public final BlockPos pos;
    public @Nullable ResourceLocation filterFluid;

    public FluidPipeConfigMenu(int id, Inventory inv, BlockPos pos, @Nullable ResourceLocation filterFluid) {
        super(ModMenuTypes.FLUID_PIPE_CONFIG_MENU.get(), id);
        this.pos = pos;
        this.filterFluid = filterFluid;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }

    @Override
    public boolean stillValid(Player player) { return true; }
}
