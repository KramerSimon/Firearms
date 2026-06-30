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

public class VehicleGarageRecipeCategory implements IRecipeCategory<VehicleGarageJeiRecipe> {

    public static final RecipeType<VehicleGarageJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "vehicle_garage", VehicleGarageJeiRecipe.class);

    private static final int W = 176;
    private static final int H = 90;

    // 4 input slots in row 1, 3 in row 2; output on the right
    private static final int ROW1_Y = 4;
    private static final int ROW2_Y = 24;
    private static final int[] ROW1_X = { 6, 26, 46, 66 };
    private static final int[] ROW2_X = { 6, 26, 46 };
    private static final int ARROW_X = 88;
    private static final int ARROW_Y = 15;
    private static final int OUT_X   = 104;
    private static final int OUT_Y   = 14;

    private final IDrawable icon;
    private final Component title;

    public VehicleGarageRecipeCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.GARAGE_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.vehicle_garage");
    }

    @Override public RecipeType<VehicleGarageJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, VehicleGarageJeiRecipe recipe, IFocusGroup focuses) {
        // Row 1: hull, tracks, turret, cannon
        builder.addSlot(RecipeIngredientRole.INPUT, ROW1_X[0], ROW1_Y)
                .addItemStack(new ItemStack(ModItems.TANK_HULL.get()));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW1_X[1], ROW1_Y)
                .addItemStack(new ItemStack(ModItems.TANK_TRACKS.get(), 2));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW1_X[2], ROW1_Y)
                .addItemStack(new ItemStack(ModItems.TANK_TURRET.get()));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW1_X[3], ROW1_Y)
                .addItemStack(new ItemStack(ModItems.TANK_CANNON.get()));
        // Row 2: diesel engine, microchip, diesel bucket
        builder.addSlot(RecipeIngredientRole.INPUT, ROW2_X[0], ROW2_Y)
                .addItemStack(new ItemStack(ModItems.DIESEL_ENGINE.get()));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW2_X[1], ROW2_Y)
                .addItemStack(new ItemStack(ModItems.ADVANCED_MICROCHIP.get(), 2));
        builder.addSlot(RecipeIngredientRole.INPUT, ROW2_X[2], ROW2_Y)
                .addItemStack(new ItemStack(ModItems.DIESEL_BUCKET.get()));
        // Output placeholder: no Tank item exists, use controller as stand-in
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y)
                .addItemStack(new ItemStack(ModBlocks.GARAGE_CONTROLLER.get()));
    }

    @Override
    public void draw(VehicleGarageJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        g.drawString(font, "=>", ARROW_X, ARROW_Y, 0xFFFFFFFF, false);
        int ty = 50;
        g.drawString(font, "Place all 7 items in the Garage Controller inventory.", 2, ty,      0xFF9AA0A8, false);
        g.drawString(font, "Supply 50,000 FE (250 FE/t over 200 ticks / 10 sec).",  2, ty + 10, 0xFF9AA0A8, false);
        g.drawString(font, "A Tank spawns in the structure interior on completion.", 2, ty + 20, 0xFF9AA0A8, false);
        g.drawString(font, "* Requires a valid 9x9x6 Vehicle Garage structure.",    2, ty + 30, 0xFF9AA0A8, false);
    }
}
