package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ChipPackagingMachineJeiRecipe {

    private final ItemStack input0;
    private final ItemStack input1;
    private final ItemStack input2;
    private final ItemStack output;

    public ChipPackagingMachineJeiRecipe(ItemStack input0, ItemStack input1, ItemStack input2, ItemStack output) {
        this.input0 = input0;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
    }

    public ItemStack getInput0() { return input0; }
    public ItemStack getInput1() { return input1; }
    public ItemStack getInput2() { return input2; }
    public ItemStack getOutput() { return output; }

    public static List<ChipPackagingMachineJeiRecipe> getAllRecipes() {
        return List.of(
            // Recipe 1: 4x silicon_die + gold_foil → basic_microchip (no third input)
            new ChipPackagingMachineJeiRecipe(
                new ItemStack(ModItems.SILICON_DIE.get(), 4),
                new ItemStack(ModItems.GOLD_FOIL.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.BASIC_MICROCHIP.get())),
            // Recipe 2: 8x silicon_die + gold_foil + etched_copper → advanced_microchip
            new ChipPackagingMachineJeiRecipe(
                new ItemStack(ModItems.SILICON_DIE.get(), 8),
                new ItemStack(ModItems.GOLD_FOIL.get()),
                new ItemStack(ModItems.ETCHED_COPPER.get()),
                new ItemStack(ModItems.ADVANCED_MICROCHIP.get()))
        );
    }
}
