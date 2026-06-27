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

public class ChipPackagingMachineRecipeCategory implements IRecipeCategory<ChipPackagingMachineJeiRecipe> {

    public static final RecipeType<ChipPackagingMachineJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "chip_packaging_machine", ChipPackagingMachineJeiRecipe.class);

    // GUI slots: (35,35), (62,35), (89,35), output (134,35)
    // Crop origin (17,17) → JEI: input0=(18,18), input1=(45,18), input2=(72,18), output=(117,18)
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public ChipPackagingMachineRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/chip_packaging_machine.png");
        this.background = guiHelper.createDrawable(texture, 17, 17, 140, 34);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.CHIP_PACKAGING_MACHINE.get()));
        this.title = Component.translatable("block.firearms.chip_packaging_machine");
    }

    @Override public RecipeType<ChipPackagingMachineJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()       { return 140; }
    @Override public int getHeight()      { return 34; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChipPackagingMachineJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 18).addItemStack(recipe.getInput0());
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 18).addItemStack(recipe.getInput1());

        if (!recipe.getInput2().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 72, 18).addItemStack(recipe.getInput2());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 18).addItemStack(recipe.getOutput());
    }
}
