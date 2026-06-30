package com.sio.firearms.menu;

import com.sio.firearms.block.FluidPipeBlockEntity;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Unified fluid-pipe config menu — holds all 6 face filters at once.
 * No item slots; the active-face selection is pure client-side UI state.
 * Fluid filter changes are sent immediately via SetFluidPipeFilterPayload.
 */
public class FluidPipeUnifiedMenu extends AbstractContainerMenu {

    public final BlockPos pos;
    /** Indexed by Direction.ordinal(): DOWN=0, UP=1, NORTH=2, SOUTH=3, WEST=4, EAST=5. */
    public final ResourceLocation[] faceFilters = new ResourceLocation[6];

    /** Client-side constructor — called by ModMenuTypes decoder. */
    public FluidPipeUnifiedMenu(int id, Inventory inv, BlockPos pos, ResourceLocation[] filters) {
        super(ModMenuTypes.FLUID_PIPE_UNIFIED_MENU.get(), id);
        this.pos = pos;
        System.arraycopy(filters, 0, this.faceFilters, 0, 6);
    }

    /** Server-side factory — snapshot all 6 face filters from the BE. */
    public static FluidPipeUnifiedMenu openFor(int id, Inventory inv, BlockPos pos,
                                               FluidPipeBlockEntity pipe) {
        ResourceLocation[] filters = new ResourceLocation[6];
        for (Direction d : Direction.values()) {
            filters[d.ordinal()] = pipe.getFilterFluid(d);
        }
        return new FluidPipeUnifiedMenu(id, inv, pos, filters);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }

    @Override
    public boolean stillValid(Player player) { return true; }
}
