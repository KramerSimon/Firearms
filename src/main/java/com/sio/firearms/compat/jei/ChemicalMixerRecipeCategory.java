package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ChemicalMixerRecipeCategory implements IRecipeCategory<ChemicalMixerJeiRecipe> {

    public static final RecipeType<ChemicalMixerJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "chemical_mixer", ChemicalMixerJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public ChemicalMixerRecipeCategory(IGuiHelper guiHelper) {
        // Crop origin (8,8) captures: fluid-in bar (27,14), item slots (47,17/35), arrow (75,35),
        // item output (113,26), fluid-out bar (141,14). Size 160x60.
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/chemical_mixer.png");
        this.background = guiHelper.createDrawable(texture, 8, 8, 160, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CHEMICAL_MIXER.get()));
        this.title = Component.translatable("block.firearms.chemical_mixer");
    }

    @Override public RecipeType<ChemicalMixerJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 160; }
    @Override public int getHeight() { return 60; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChemicalMixerJeiRecipe recipe, IFocusGroup focuses) {
        // Fluid input: abs fluid bar at (27,14) → JEI (19,6). Rendered as 16x52 fluid tank.
        if (!recipe.getInputFluid().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 19, 6)
                    .setFluidRenderer(recipe.getInputFluid().getAmount(), false, 16, 52)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getInputFluid());
        }

        // Item input slot A: abs slot (47,17) → JEI (39,9)
        if (!recipe.getInputItem().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 39, 9)
                    .addItemStack(recipe.getInputItem());
        }

        // Item input slot B (optional second ingredient): abs slot (47,35) → JEI (39,27)
        if (!recipe.getSecondInputItem().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 39, 27)
                    .addItemStack(recipe.getSecondInputItem());
        }

        // Item output: abs slot (113,26) → JEI (105,18)
        if (!recipe.getOutputItem().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 18)
                    .addItemStack(recipe.getOutputItem());
        }

        // Fluid output: abs fluid bar at (141,14) → JEI (133,6). Rendered as 16x52 fluid tank.
        if (!recipe.getOutputFluid().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 133, 6)
                    .setFluidRenderer(recipe.getOutputFluid().getAmount(), false, 16, 52)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutputFluid());
        }
    }
}
