package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class HeatTreatmentRecipeCategory implements IRecipeCategory<HeatTreatmentJeiRecipe> {

    public static final RecipeType<HeatTreatmentJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "heat_treatment", HeatTreatmentJeiRecipe.class);

    // GUI slots: primary (56,35), secondary (56,53), output (116,44)
    // Crop origin (38,17) → JEI: primary=(18,18), secondary=(18,36), output=(78,27)
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public HeatTreatmentRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/heat_treatment_furnace.png");
        this.background = guiHelper.createDrawable(texture, 38, 17, 100, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.HEAT_TREATMENT_FURNACE.get()));
        this.title = Component.translatable("block.firearms.heat_treatment_furnace");
    }

    @Override public RecipeType<HeatTreatmentJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth()  { return 100; }
    @Override public int getHeight() { return 54; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HeatTreatmentJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 18)
                .addItemStack(recipe.getPrimary());

        if (!recipe.getSecondary().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 18, 36)
                    .addItemStack(recipe.getSecondary());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 27)
                .addItemStack(recipe.getOutput());
    }
}
