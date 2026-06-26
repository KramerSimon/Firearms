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

public class LatheRecipeCategory implements IRecipeCategory<LatheJeiRecipe> {

    public static final RecipeType<LatheJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "lathe", LatheJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public LatheRecipeCategory(IGuiHelper guiHelper) {
        // Crop the lathe GUI: primary slot (56,35), secondary (56,53), output (116,44)
        // Crop origin (38,17) captures all three slots plus the progress arrow at ~(79,40).
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/lathe.png");
        this.background = guiHelper.createDrawable(texture, 38, 17, 98, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.LATHE.get()));
        this.title = Component.translatable("block.firearms.lathe");
    }

    @Override public RecipeType<LatheJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 98; }
    @Override public int getHeight() { return 54; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LatheJeiRecipe recipe, IFocusGroup focuses) {
        // Primary slot: abs (56,35) - crop (38,17) = JEI (18,18)
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 18)
                .addItemStack(recipe.getPrimary());

        // Secondary slot: abs (56,53) - crop (38,17) = JEI (18,36). Only add if recipe uses it.
        if (!recipe.getSecondary().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 18, 36)
                    .addItemStack(recipe.getSecondary());
        }

        // Output slot: abs (116,44) - crop (38,17) = JEI (78,27)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 27)
                .addItemStack(recipe.getOutput());
    }
}
