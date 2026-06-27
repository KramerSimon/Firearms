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

public class WaferCuttingMachineRecipeCategory implements IRecipeCategory<WaferCuttingMachineJeiRecipe> {

    public static final RecipeType<WaferCuttingMachineJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "wafer_cutting_machine", WaferCuttingMachineJeiRecipe.class);

    // GUI slots: input (44,26), tool (44,52), output (116,35)
    // Crop origin (26,8) → JEI: input=(18,18), tool=(18,44), output=(90,27)
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public WaferCuttingMachineRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/wafer_cutting_machine.png");
        this.background = guiHelper.createDrawable(texture, 26, 8, 104, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.WAFER_CUTTING_MACHINE.get()));
        this.title = Component.translatable("block.firearms.wafer_cutting_machine");
    }

    @Override public RecipeType<WaferCuttingMachineJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()       { return 104; }
    @Override public int getHeight()      { return 54; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WaferCuttingMachineJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 18).addItemStack(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 44).addItemStack(recipe.getTool());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 27).addItemStack(recipe.getOutput());
    }
}
