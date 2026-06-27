package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class EuvLithographyJeiRecipe {

    public static final FluidStack PHOTORESIST_INPUT = new FluidStack(ModFluids.PHOTORESIST_STILL.get(), 500);

    private final ItemStack coatedWafer;
    private final ItemStack photomask;
    private final FluidStack photoresist;
    private final ItemStack pattermedWafer;

    public EuvLithographyJeiRecipe(ItemStack coatedWafer, ItemStack photomask,
                                   FluidStack photoresist, ItemStack pattermedWafer) {
        this.coatedWafer   = coatedWafer;
        this.photomask     = photomask;
        this.photoresist   = photoresist;
        this.pattermedWafer = pattermedWafer;
    }

    public ItemStack  getCoatedWafer()   { return coatedWafer; }
    public ItemStack  getPhotomask()     { return photomask; }
    public FluidStack getPhotoresist()   { return photoresist; }
    public ItemStack  getPattermedWafer() { return pattermedWafer; }

    public static List<EuvLithographyJeiRecipe> getAllRecipes() {
        return List.of(new EuvLithographyJeiRecipe(
                new ItemStack(ModItems.COATED_WAFER.get()),
                new ItemStack(ModItems.PHOTOMASK.get()),
                PHOTORESIST_INPUT,
                new ItemStack(ModItems.PATTERNED_WAFER.get())));
    }
}
