package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
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

public class SpentFuelStorageStructureCategory implements IRecipeCategory<SpentFuelStorageStructureJeiRecipe> {

    public static final RecipeType<SpentFuelStorageStructureJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "spent_fuel_storage_structure", SpentFuelStorageStructureJeiRecipe.class);

    private static final int W = 178;
    private static final int H = 118;

    // 3 representative layers for the 5×5×4 structure.
    // B=spent_fuel_storage_base, W=spent_fuel_storage_wall, C=controller, .=hollow
    private static final String[][] LAYERS = {
        { "BBBBB", "BBBBB", "BBBBB", "BBBBB", "BBBBB" }, // 1 — base floor (layer 0)
        { "BBBBB", "BBBBB", "BBBBB", "BBBBB", "BBCBB" }, // 2 — controller layer (layer 1)
        { "WWWWW", "W...W", "W...W", "W...W", "WWWWW" }, // 3 — hollow wall ring ×2 (layers 2–3)
    };
    private static final String[] STEP_NUMS  = { "1", "2", "3" };
    private static final String[] LAYER_NAME = { "Base", "Ctrl", "×2 Walls" };

    private static final int CELL     = 7;
    private static final int GRID_W   = CELL * 5; // 35
    private static final int GRID_TOP = 22;
    private static final int PANEL_X0 = 4;
    private static final int PANEL_GAP = 60;

    private static final int BOM_Y   = 62;
    private static final int[] BOM_X = { 18, 80, 142 };

    private static final int C_BASE    = 0xFF807020;
    private static final int C_WALL    = 0xFF9A8A30;
    private static final int C_CTRL    = 0xFF46C24E;
    private static final int C_HOLLOW  = 0xFF101216;
    private static final int C_BORDER  = 0xFF05060A;
    private static final int C_PANEL   = 0xFF1B1D22;
    private static final int C_PANEL_HI = 0xFF34373E;

    private final IDrawable icon;
    private final Component title;

    public SpentFuelStorageStructureCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.SPENT_FUEL_STORAGE_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.spent_fuel_storage_structure");
    }

    @Override public RecipeType<SpentFuelStorageStructureJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpentFuelStorageStructureJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[0], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.SPENT_FUEL_STORAGE_CONTROLLER.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[1], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.SPENT_FUEL_STORAGE_BASE.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[2], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.SPENT_FUEL_STORAGE_WALL.get()));
    }

    @Override
    public void draw(SpentFuelStorageStructureJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        for (int li = 0; li < LAYERS.length; li++) {
            int px = PANEL_X0 + li * PANEL_GAP;

            centered(g, font, STEP_NUMS[li],  px, 1,  0xFFFFE08A);
            centered(g, font, LAYER_NAME[li], px, 11, 0xFFB9BDC4);

            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP + GRID_W + 1, C_PANEL);
            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP,               C_PANEL_HI);
            g.fill(px - 1, GRID_TOP - 1, px,               GRID_TOP + GRID_W + 1, C_PANEL_HI);

            String[] layer = LAYERS[li];
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    int x = px + col * CELL;
                    int y = GRID_TOP + row * CELL;
                    int color = colorOf(layer[row].charAt(col));
                    g.fill(x, y, x + CELL,     y + CELL,     C_BORDER);
                    g.fill(x, y, x + CELL - 1, y + CELL - 1, color);
                    g.fill(x, y, x + CELL - 1, y + 1,        shade(color, 28));
                }
            }

            if (li < LAYERS.length - 1) {
                g.drawString(font, ">", px + GRID_W + 1, GRID_TOP + 14, 0xFF6E7278, false);
            }
        }

        bom(g, font, 0, C_CTRL, "x" + SpentFuelStorageStructureJeiRecipe.CONTROLLER_COUNT);
        bom(g, font, 1, C_BASE, "x" + SpentFuelStorageStructureJeiRecipe.BASE_COUNT);
        bom(g, font, 2, C_WALL, "x" + SpentFuelStorageStructureJeiRecipe.WALL_COUNT);

        int ny = BOM_Y + 32;
        g.drawString(font, "Build bottom→top. 5×5×4. Two solid base layers, then two hollow wall rings.", 2, ny,      0xFF9AA0A8, false);
        g.drawString(font, "Controller sits on any border of layer 1. No Energy/Fluid Ports required.", 2, ny + 10, 0xFF9AA0A8, false);
        g.drawString(font, "Stores spent fuel rods (9 slots). Emits radiation in a 5-block radius.", 2, ny + 20, 0xFF9AA0A8, false);
    }

    private static void bom(GuiGraphics g, Font font, int i, int color, String label) {
        int sx = BOM_X[i];
        g.fill(sx, BOM_Y + 18, sx + 16, BOM_Y + 20, color);
        int tx = sx + 8 - font.width(label) / 2;
        g.drawString(font, label, tx, BOM_Y + 23, 0xFFFFFFFF, false);
    }

    private static void centered(GuiGraphics g, Font font, String s, int panelX, int y, int color) {
        int tx = panelX + GRID_W / 2 - font.width(s) / 2;
        g.drawString(font, s, tx, y, color, false);
    }

    private static int colorOf(char c) {
        return switch (c) {
            case 'B' -> C_BASE;
            case 'W' -> C_WALL;
            case 'C' -> C_CTRL;
            default  -> C_HOLLOW;
        };
    }

    private static int shade(int argb, int d) {
        int a  = (argb >>> 24) & 0xFF;
        int r  = Math.min(255, ((argb >> 16) & 0xFF) + d);
        int gg = Math.min(255, ((argb >>  8) & 0xFF) + d);
        int b  = Math.min(255,  (argb        & 0xFF) + d);
        return (a << 24) | (r << 16) | (gg << 8) | b;
    }
}
