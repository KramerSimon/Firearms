package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GunsmithTableJeiRecipe {

    private final NonNullList<ItemStack> inputs;
    private final ItemStack output;

    public GunsmithTableJeiRecipe(NonNullList<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public NonNullList<ItemStack> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public static List<GunsmithTableJeiRecipe> getAllRecipes() {
        return List.of(createPistolRecipe(), createRifleRecipe());
    }

    private static GunsmithTableJeiRecipe createPistolRecipe() {
        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        inputs.set(0, new ItemStack(ModItems.GUN_BARREL.get()));
        inputs.set(3, new ItemStack(ModItems.GUN_GRIP.get()));
        inputs.set(4, new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()));
        inputs.set(6, new ItemStack(ModItems.MAGAZINE.get()));
        return new GunsmithTableJeiRecipe(inputs, new ItemStack(ModItems.PISTOL.get()));
    }

    private static GunsmithTableJeiRecipe createRifleRecipe() {
        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        inputs.set(1, new ItemStack(ModItems.GUN_BARREL.get()));
        inputs.set(3, new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()));
        inputs.set(4, new ItemStack(ModItems.GUN_GRIP.get()));
        inputs.set(6, new ItemStack(ModItems.MAGAZINE.get()));
        inputs.set(7, new ItemStack(ModItems.STEEL_ROD.get()));
        return new GunsmithTableJeiRecipe(inputs, new ItemStack(ModItems.RIFLE.get()));
    }
}
