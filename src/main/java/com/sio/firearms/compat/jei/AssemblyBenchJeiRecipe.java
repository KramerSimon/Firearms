package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AssemblyBenchJeiRecipe {

    private final NonNullList<ItemStack> inputs;
    private final ItemStack output;

    public AssemblyBenchJeiRecipe(NonNullList<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public NonNullList<ItemStack> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public static List<AssemblyBenchJeiRecipe> getAllRecipes() {
        return List.of(
                createGunBarrelRecipe(),
                createTriggerAssemblyRecipe(),
                createPistolRecipe(),
                createRifleRecipe(),
                createShotgunRecipe(),
                createSniperRifleRecipe(),
                createSMGRecipe()
        );
    }

    private static AssemblyBenchJeiRecipe of(ItemStack[] slots, ItemStack output) {
        NonNullList<ItemStack> inputs = NonNullList.withSize(6, ItemStack.EMPTY);
        for (int i = 0; i < slots.length && i < 6; i++) {
            inputs.set(i, slots[i]);
        }
        return new AssemblyBenchJeiRecipe(inputs, output);
    }

    private static AssemblyBenchJeiRecipe createGunBarrelRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.GUN_BARREL_BLANK.get()),
                new ItemStack(ModItems.STEEL_ROD.get(), 2)
        }, new ItemStack(ModItems.GUN_BARREL.get()));
    }

    private static AssemblyBenchJeiRecipe createTriggerAssemblyRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.FIRING_MECHANISM.get()),
                new ItemStack(ModItems.FIRING_PIN.get()),
                new ItemStack(ModItems.SPRING.get())
        }, new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()));
    }

    private static AssemblyBenchJeiRecipe createPistolRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.GUN_BARREL.get()),
                new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()),
                new ItemStack(ModItems.GUN_GRIP.get()),
                new ItemStack(ModItems.MAGAZINE.get()),
                new ItemStack(ModItems.SPRING.get())
        }, new ItemStack(ModItems.PISTOL.get()));
    }

    private static AssemblyBenchJeiRecipe createRifleRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.GUN_BARREL.get()),
                new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()),
                new ItemStack(ModItems.GUN_GRIP.get()),
                new ItemStack(ModItems.MAGAZINE.get()),
                new ItemStack(ModItems.BOLT.get()),
                new ItemStack(ModItems.BUFFER_TUBE.get())
        }, new ItemStack(ModItems.RIFLE.get()));
    }

    private static AssemblyBenchJeiRecipe createShotgunRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.GUN_BARREL.get(), 2),
                new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()),
                new ItemStack(ModItems.GUN_GRIP.get()),
                new ItemStack(ModItems.MAGAZINE.get())
        }, new ItemStack(ModItems.SHOTGUN.get()));
    }

    private static AssemblyBenchJeiRecipe createSniperRifleRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.GUN_BARREL.get()),
                new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()),
                new ItemStack(ModItems.GUN_GRIP.get()),
                new ItemStack(ModItems.MAGAZINE.get()),
                new ItemStack(ModItems.STEEL_ROD.get(), 2),
                new ItemStack(ModItems.FIRING_PIN.get())
        }, new ItemStack(ModItems.SNIPER_RIFLE.get()));
    }

    private static AssemblyBenchJeiRecipe createSMGRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.GUN_BARREL.get()),
                new ItemStack(ModItems.ELECTRONIC_TRIGGER.get()),
                new ItemStack(ModItems.GUN_GRIP.get()),
                new ItemStack(ModItems.MAGAZINE.get()),
                new ItemStack(ModItems.CIRCUIT_BOARD.get()),
                new ItemStack(ModItems.BUFFER_TUBE.get())
        }, new ItemStack(ModItems.SMG.get()));
    }
}
