package com.sio.firearms.block;

import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Item output hatch: pulls finished products out of the furnace controller's output
 * slot into its buffer, where pipes and hoppers can collect them.
 */
public class EbfOutputBusBlockEntity extends EbfBusBlockEntity {

    public EbfOutputBusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EBF_OUTPUT_BUS.get(), pos, state);
    }

    @Override
    protected void transfer(EBFControllerBlockEntity controller) {
        // Pull a stack out of the controller, then stash it in the first buffer slot
        // that will take it.
        ItemStack preview = controller.extractOutput(64, true);
        if (preview.isEmpty()) return;

        for (int i = 0; i < buffer.getSlots(); i++) {
            ItemStack remainder = buffer.insertItem(i, preview, true);
            int accepted = preview.getCount() - remainder.getCount();
            if (accepted > 0) {
                ItemStack taken = controller.extractOutput(accepted, false);
                buffer.insertItem(i, taken, false);
                return;
            }
        }
    }
}
