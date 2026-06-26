package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class FirearmsJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new MetalPressRecipeCategory(gui),
                new AssemblyBenchRecipeCategory(gui),
                new LatheRecipeCategory(gui),
                new EBFRecipeCategory(gui),
                new ChemicalMixerRecipeCategory(gui),
                new ElectrolysisRecipeCategory(gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(MetalPressRecipeCategory.RECIPE_TYPE, MetalPressJeiRecipe.getAllRecipes());
        registration.addRecipes(AssemblyBenchRecipeCategory.RECIPE_TYPE, AssemblyBenchJeiRecipe.getAllRecipes());
        registration.addRecipes(LatheRecipeCategory.RECIPE_TYPE, LatheJeiRecipe.getAllRecipes());
        registration.addRecipes(EBFRecipeCategory.RECIPE_TYPE, EBFJeiRecipe.getAllRecipes());
        registration.addRecipes(ChemicalMixerRecipeCategory.RECIPE_TYPE, ChemicalMixerJeiRecipe.getAllRecipes());
        registration.addRecipes(ElectrolysisRecipeCategory.RECIPE_TYPE, ElectrolysisJeiRecipe.getAllRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.METAL_PRESS.get()), MetalPressRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ASSEMBLY_BENCH.get()), AssemblyBenchRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.LATHE.get()), LatheRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.EBF_CONTROLLER.get()), EBFRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHEMICAL_MIXER.get()), ChemicalMixerRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ELECTROLYSIS_MACHINE.get()), ElectrolysisRecipeCategory.RECIPE_TYPE);
    }
}
