package com.sio.firearms.block;

import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Item input hatch: pushes whatever lands in its buffer into the furnace controller's
 * material/additive slots, routing coke and carbon steel to the additive slot.
 */
public class EbfImportBusBlockEntity extends EbfBusBlockEntity {

    public EbfImportBusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EBF_IMPORT_BUS.get(), pos, state);
    }

    @Override
    protected void transfer(EBFControllerBlockEntity controller) {
        for (int i = 0; i < buffer.getSlots(); i++) {
            ItemStack stack = buffer.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ItemStack remainder = controller.importItem(stack);
            if (remainder.getCount() != stack.getCount()) {
                buffer.setStackInSlot(i, remainder);
            }
        }
    }
}
