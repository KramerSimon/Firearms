package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A single "recipe" describing how to assemble the Electric Blast Furnace
 * 5x5x5 multiblock. All visual layout lives in {@link EBFStructureCategory};
 * this just carries the block list / counts and the selectable coil variants.
 */
public class EBFStructureJeiRecipe {

    // Block counts in the assembled structure (see EBFControllerBlockEntity).
    public static final int CASING_COUNT  = 96;  // any may be an Energy/Fluid Port
    public static final int COIL_COUNT    = 16;  // 8 per coil ring x 2 layers
    public static final int MUFFLER_COUNT = 1;
    public static final int CONTROLLER_COUNT = 1;

    private final List<ItemStack> coils;

    public EBFStructureJeiRecipe(List<ItemStack> coils) {
        this.coils = coils;
    }

    public List<ItemStack> getCoils() { return coils; }

    public static List<EBFStructureJeiRecipe> getAllRecipes() {
        return List.of(new EBFStructureJeiRecipe(List.of(
                new ItemStack(ModItems.KANTHAL_COIL.get()),
                new ItemStack(ModItems.NICHROME_COIL.get()),
                new ItemStack(ModItems.TUNGSTEN_COIL.get())
        )));
    }
}
