package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class HangarRecipeCategory implements IRecipeCategory<HangarJeiRecipe> {

    public static final RecipeType<HangarJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "aircraft_hangar", HangarJeiRecipe.class);

    private static final int W = 176;
    private static final int H = 90;

    // 3 input slots in row 1, 2 in row 2; output on the right
    private static final int ROW1_Y = 4;
    private static final int ROW2_Y = 24;
    private static final int[] ROW1_X = { 6, 26, 46 };
    private static final int[] ROW2_X = { 6, 26 };
    private static final int ARROW_X = 68;
    private static final int ARROW_Y = 15;
    private static final int OUT_X   = 84;
    private static final int OUT_Y   = 14;

    private final IDrawable icon;
    private final Component title;

    public HangarRecipeCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.HANGAR_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.aircraft_hangar");
    }

    @Override public RecipeType<HangarJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HangarJeiRecipe recipe, IFocusGroup focuses) {
        // Row 1: fuselage, wings, jet engine
        builder.addSlot(RecipeIngredientRole.INPUT, ROW1_X[0], ROW1_Y)
                .addItemStack(new ItemStack(ModItems.AIRCRAFT_FUSELAGE.get()));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW1_X[1], ROW1_Y)
                .addItemStack(new ItemStack(ModItems.AIRCRAFT_WINGS.get(), 2));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW1_X[2], ROW1_Y)
                .addItemStack(new ItemStack(ModItems.JET_ENGINE.get(), 2));
        // Row 2: avionics, kerosene bucket
        builder.addSlot(RecipeIngredientRole.INPUT, ROW2_X[0], ROW2_Y)
                .addItemStack(new ItemStack(ModItems.COCKPIT_AVIONICS.get()));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW2_X[1], ROW2_Y)
                .addItemStack(new ItemStack(ModItems.KEROSENE_BUCKET.get()));
        // Output placeholder: no Aircraft item exists, use controller as stand-in
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y)
                .addItemStack(new ItemStack(ModBlocks.HANGAR_CONTROLLER.get()));
    }

    @Override
    public void draw(HangarJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        g.drawString(font, "=>", ARROW_X, ARROW_Y, 0xFFFFFFFF, false);
        int ty = 50;
        g.drawString(font, "Place all 5 items in the Hangar Controller inventory.",   2, ty,      0xFF9AA0A8, false);
        g.drawString(font, "Supply 80,000 FE (200 FE/t over 400 ticks / 20 sec).",   2, ty + 10, 0xFF9AA0A8, false);
        g.drawString(font, "An aircraft spawns in the structure interior.",           2, ty + 20, 0xFF9AA0A8, false);
        g.drawString(font, "* Requires a valid 11x11x6 Aircraft Hangar structure.",   2, ty + 30, 0xFF9AA0A8, false);
    }
}
