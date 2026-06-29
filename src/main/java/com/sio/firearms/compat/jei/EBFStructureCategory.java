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

/**
 * A GregTech-style assembly guide for the Electric Blast Furnace. Shows the five
 * build steps (bottom layer to top) as colour-coded slices, plus a keyed bill of
 * materials with live item tooltips. The slice layout mirrors
 * {@link com.sio.firearms.block.EBFControllerBlockEntity}'s validation exactly.
 */
public class EBFStructureCategory implements IRecipeCategory<EBFStructureJeiRecipe> {

    public static final RecipeType<EBFStructureJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "ebf_structure", EBFStructureJeiRecipe.class);

    private static final int W = 178;
    private static final int H = 118;

    // Layer slices, drawn bottom (step 1) to top (step 5).
    // Rows = depth (top row = machine back, bottom row = machine front).
    // Cols = left→right. Chars: C casing, K coil, P controller, M muffler, '.' hollow.
    private static final String[][] LAYERS = {
            { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" }, // 1 floor
            { "CCCCC", "CKKKC", "CK.KC", "CKKKC", "CCCCC" }, // 2 coil ring
            { "CCCCC", "CKKKC", "CK.KC", "CKKKC", "CCPCC" }, // 3 coil ring + controller (front)
            { "CCCCC", "C...C", "C...C", "C...C", "CCCCC" }, // 4 chamber walls
            { "CCCCC", "CCCCC", "CCMCC", "CCCCC", "CCCCC" }, // 5 roof + muffler
    };
    private static final String[] STEP_NUMS  = { "1", "2", "3", "4", "5" };
    private static final String[] LAYER_NAME  = { "Floor", "Coils", "Coils", "Walls", "Roof" };

    // Geometry.
    private static final int CELL     = 6;
    private static final int GRID_W   = CELL * 5;     // 30
    private static final int GRID_TOP = 22;
    private static final int PANEL_X0 = 4;
    private static final int PANEL_GAP = 35;

    // Bill-of-materials slot row.
    private static final int BOM_Y    = 58;
    private static final int[] BOM_X  = { 6, 50, 94, 138 };

    // Cell colours (ARGB).
    private static final int C_CASING = 0xFF565B62;
    private static final int C_COIL   = 0xFFCC6A22;
    private static final int C_CTRL   = 0xFF46C24E;
    private static final int C_MUFFLR = 0xFFB5532A;
    private static final int C_HOLLOW = 0xFF101216;
    private static final int C_BORDER = 0xFF05060A;
    private static final int C_PANEL  = 0xFF1B1D22;
    private static final int C_PANEL_HI = 0xFF34373E;

    private final IDrawable icon;
    private final Component title;

    public EBFStructureCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.EBF_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.ebf_structure");
    }

    @Override public RecipeType<EBFStructureJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EBFStructureJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[0], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.EBF_CONTROLLER.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[1], BOM_Y)
                .addItemStack(new ItemStack(ModItems.BLAST_FURNACE_CASING.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[2], BOM_Y)
                .addItemStacks(recipe.getCoils());   // cycles Kanthal / Nichrome / Tungsten
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[3], BOM_Y)
                .addItemStack(new ItemStack(ModItems.MUFFLER_HATCH.get()));
    }

    @Override
    public void draw(EBFStructureJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        // ── Step slices ───────────────────────────────────────────────────────
        for (int li = 0; li < LAYERS.length; li++) {
            int px = PANEL_X0 + li * PANEL_GAP;

            centered(g, font, STEP_NUMS[li], px, 1, 0xFFFFE08A);
            centered(g, font, LAYER_NAME[li], px, 11, 0xFFB9BDC4);

            // panel frame
            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP + GRID_W + 1, C_PANEL);
            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP, C_PANEL_HI);
            g.fill(px - 1, GRID_TOP - 1, px, GRID_TOP + GRID_W + 1, C_PANEL_HI);

            String[] layer = LAYERS[li];
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    int x = px + col * CELL;
                    int y = GRID_TOP + row * CELL;
                    int color = colorOf(layer[row].charAt(col));
                    g.fill(x, y, x + CELL, y + CELL, C_BORDER);
                    g.fill(x, y, x + CELL - 1, y + CELL - 1, color);
                    g.fill(x, y, x + CELL - 1, y + 1, shade(color, 28));   // top bevel
                }
            }

            // build-order arrow between steps
            if (li < LAYERS.length - 1) {
                g.drawString(font, ">", px + GRID_W + 1, GRID_TOP + 11, 0xFF6E7278, false);
            }
        }

        // ── Bill of materials key (under the live slots) ────────────────────────
        bom(g, font, 0, C_CTRL,   "x" + EBFStructureJeiRecipe.CONTROLLER_COUNT);
        bom(g, font, 1, C_CASING, "x" + EBFStructureJeiRecipe.CASING_COUNT);
        bom(g, font, 2, C_COIL,   "x" + EBFStructureJeiRecipe.COIL_COUNT);
        bom(g, font, 3, C_MUFFLR, "x" + EBFStructureJeiRecipe.MUFFLER_COUNT);

        // ── Notes ───────────────────────────────────────────────────────────────
        int ny = BOM_Y + 32;
        g.drawString(font, "Build bottom→top. Front row = machine front; the controller faces out.", 2, ny, 0xFF9AA0A8, false);
        g.drawString(font, "All 16 coils must match — the tier sets the max temperature.", 2, ny + 10, 0xFF9AA0A8, false);
        g.drawString(font, "Dark cells are the hollow chamber. Any casing may be an Energy/Fluid Port.", 2, ny + 20, 0xFF9AA0A8, false);
    }

    private static void bom(GuiGraphics g, Font font, int i, int color, String count) {
        int sx = BOM_X[i];
        g.fill(sx, BOM_Y + 18, sx + 16, BOM_Y + 20, color);            // colour key bar under slot
        int tx = sx + 8 - font.width(count) / 2;
        g.drawString(font, count, tx, BOM_Y + 23, 0xFFFFFFFF, false);  // count
    }

    private static void centered(GuiGraphics g, Font font, String s, int panelX, int y, int color) {
        int tx = panelX + GRID_W / 2 - font.width(s) / 2;
        g.drawString(font, s, tx, y, color, false);
    }

    private static int colorOf(char c) {
        return switch (c) {
            case 'C' -> C_CASING;
            case 'K' -> C_COIL;
            case 'P' -> C_CTRL;
            case 'M' -> C_MUFFLR;
            default  -> C_HOLLOW;
        };
    }

    private static int shade(int argb, int d) {
        int a = (argb >>> 24) & 0xFF;
        int r = Math.min(255, ((argb >> 16) & 0xFF) + d);
        int gg = Math.min(255, ((argb >> 8) & 0xFF) + d);
        int b = Math.min(255, (argb & 0xFF) + d);
        return (a << 24) | (r << 16) | (gg << 8) | b;
    }
}
