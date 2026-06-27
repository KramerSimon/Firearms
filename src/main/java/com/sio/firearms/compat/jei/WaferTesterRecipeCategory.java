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

public class WaferTesterRecipeCategory implements IRecipeCategory<WaferTesterJeiRecipe> {

    public static final RecipeType<WaferTesterJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "wafer_tester", WaferTesterJeiRecipe.class);

    // GUI slots: input (56,35), output (116,35)
    // Crop origin (38,17) → JEI: input=(18,18), output_good=(78,18), output_defective=(98,18)
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public WaferTesterRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/wafer_tester.png");
        this.background = guiHelper.createDrawable(texture, 38, 17, 100, 34);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.WAFER_TESTER.get()));
        this.title = Component.translatable("block.firearms.wafer_tester");
    }

    @Override public RecipeType<WaferTesterJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()       { return 100; }
    @Override public int getHeight()      { return 34; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WaferTesterJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 18)
                .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 18)
                .addItemStack(recipe.getOutputGood());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 18)
                .addItemStack(recipe.getOutputDefective());
    }
}
