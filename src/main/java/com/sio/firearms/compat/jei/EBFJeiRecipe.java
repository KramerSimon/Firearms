package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class EBFJeiRecipe {

    private final ItemStack input0;
    private final ItemStack input1;
    private final ItemStack output;
    private final int requiredTemp;

    public EBFJeiRecipe(ItemStack input0, ItemStack input1, ItemStack output, int requiredTemp) {
        this.input0      = input0;
        this.input1      = input1;
        this.output      = output;
        this.requiredTemp = requiredTemp;
    }

    public ItemStack getInput0()    { return input0; }
    public ItemStack getInput1()    { return input1; }
    public ItemStack getOutput()    { return output; }
    public int getRequiredTemp()    { return requiredTemp; }

    public static List<EBFJeiRecipe> getAllRecipes() {
        return List.of(
            // ── Coal-coke recipes @800°C ──────────────────────────────────────
            new EBFJeiRecipe(
                new ItemStack(Items.RAW_IRON),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.STEEL_INGOT.get(), 2),
                800),
            new EBFJeiRecipe(
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.STEEL_INGOT.get(), 3),
                800),
            new EBFJeiRecipe(
                new ItemStack(ModItems.STEEL_INGOT.get()),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                800),
            new EBFJeiRecipe(
                new ItemStack(Items.RAW_GOLD),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(Items.GOLD_INGOT, 2),
                800),
            new EBFJeiRecipe(
                new ItemStack(Items.COPPER_INGOT),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.CARBON_STEEL.get()),
                800),
            // ── Coal-coke recipe @1200°C ──────────────────────────────────────
            new EBFJeiRecipe(
                new ItemStack(ModItems.TUNGSTEN_ORE_RAW.get()),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.TUNGSTEN_INGOT.get()),
                1200),
            // ── Coal-coke recipe @2000°C ──────────────────────────────────────
            new EBFJeiRecipe(
                new ItemStack(ModItems.URANIUM_ORE_RAW.get()),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.URANIUM_INGOT.get()),
                2000),
            // ── Carbon-steel recipe @800°C ────────────────────────────────────
            new EBFJeiRecipe(
                new ItemStack(ModItems.TUNGSTEN_INGOT.get()),
                new ItemStack(ModItems.CARBON_STEEL.get()),
                new ItemStack(ModItems.TUNGSTEN_CARBIDE.get(), 2),
                800),
            // ── Silicon production @800°C ─────────────────────────────────────
            new EBFJeiRecipe(
                new ItemStack(ModItems.QUARTZ_SAND.get(), 4),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.METALLURGICAL_SILICON.get(), 2),
                800),
            // ── Nuclear Reactor Stage 1 @800°C ────────────────────────────────
            new EBFJeiRecipe(
                new ItemStack(net.minecraft.world.item.Items.COAL),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.GRAPHITE_BLOCK_ITEM.get(), 2),
                800),
            new EBFJeiRecipe(
                new ItemStack(ModItems.ZIRCONIUM_ORE_RAW.get()),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.ZIRCONIUM_INGOT.get()),
                800)
        );
    }
}
