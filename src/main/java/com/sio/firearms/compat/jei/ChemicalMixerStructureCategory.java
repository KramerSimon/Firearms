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

import java.util.List;

/**
 * JEI assembly guide for the Chemical Mixer 3×3×4 multiblock.
 * Shows 4 layer slices (floor → lower walls → upper walls → solid cap)
 * plus a bill-of-materials key with live item tooltips.
 */
public class ChemicalMixerStructureCategory implements IRecipeCategory<ChemicalMixerStructureJeiRecipe> {

    public static final RecipeType<ChemicalMixerStructureJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "chemical_mixer_structure", ChemicalMixerStructureJeiRecipe.class);

    private static final int W = 176;
    private static final int H = 120;

    // Layer slices, drawn bottom (step 1) to top (step 4).
    // Rows = depth (top = back, bottom = front). Cols = left→right.
    // Chars: B=base, C=controller, W=wall, .=hollow air
    private static final String[][] LAYERS = {
        {"BBB", "BBB", "BCB"},  // 1 — floor (controller at front-centre)
        {"WWW", "W.W", "WWW"},  // 2 — lower wall ring (hollow centre)
        {"WWW", "W.W", "WWW"},  // 3 — upper wall ring (hollow centre)
        {"WWW", "WWW", "WWW"},  // 4 — solid cap
    };
    private static final String[] STEP_NUMS  = {"1", "2", "3", "4"};
    private static final String[] LAYER_NAME = {"Floor", "Walls", "Walls", "Cap"};

    // Geometry — 4 panels of 3×3 cells (10px each) across a 176px canvas.
    private static final int CELL      = 10;
    private static final int GRID_W    = CELL * 3;   // 30
    private static final int GRID_TOP  = 22;
    private static final int PANEL_X0  = 4;
    private static final int PANEL_GAP = 46;         // gap between panel origins

    // Bill-of-materials slot row (4 slots: controller, base, wall, port substitute).
    private static final int BOM_Y   = 60;
    private static final int[] BOM_X = {6, 50, 94, 138};

    // Cell colours (ARGB)
    private static final int C_BASE     = 0xFF3A4438;  // dark green-gray floor tile
    private static final int C_WALL     = 0xFF3C5570;  // blue-gray glass/steel wall
    private static final int C_CTRL     = 0xFFCC7020;  // amber control panel
    private static final int C_HOLLOW   = 0xFF101216;  // hollow air
    private static final int C_PORT     = 0xFF8B8B1A;  // olive — optional port substitutes
    private static final int C_BORDER   = 0xFF05060A;
    private static final int C_PANEL    = 0xFF1B1D22;
    private static final int C_PANEL_HI = 0xFF34373E;

    private final IDrawable icon;
    private final Component title;

    public ChemicalMixerStructureCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.CHEMICAL_MIXER_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.chemical_mixer_structure");
    }

    @Override public RecipeType<ChemicalMixerStructureJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChemicalMixerStructureJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[0], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.CHEMICAL_MIXER_CONTROLLER.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[1], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.CHEMICAL_MIXER_BASE.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[2], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.CHEMICAL_MIXER_WALL.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[3], BOM_Y)
                .addItemStacks(List.of(
                        new ItemStack(ModItems.ENERGY_PORT.get()),
                        new ItemStack(ModItems.FLUID_PORT.get())));
    }

    @Override
    public void draw(ChemicalMixerStructureJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        // ── Step slices ───────────────────────────────────────────────────────
        for (int li = 0; li < LAYERS.length; li++) {
            int px = PANEL_X0 + li * PANEL_GAP;

            centered(g, font, STEP_NUMS[li],  px, 1,  0xFFFFE08A);
            centered(g, font, LAYER_NAME[li], px, 11, 0xFFB9BDC4);

            // panel background frame
            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP + GRID_W + 1, C_PANEL);
            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP,              C_PANEL_HI);
            g.fill(px - 1, GRID_TOP - 1, px,               GRID_TOP + GRID_W + 1, C_PANEL_HI);

            String[] layer = LAYERS[li];
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int x = px + col * CELL;
                    int y = GRID_TOP + row * CELL;
                    int color = colorOf(layer[row].charAt(col));
                    g.fill(x, y, x + CELL,     y + CELL,     C_BORDER);
                    g.fill(x, y, x + CELL - 1, y + CELL - 1, color);
                    g.fill(x, y, x + CELL - 1, y + 1,        shade(color, 28));
                }
            }

            if (li < LAYERS.length - 1) {
                g.drawString(font, ">", px + GRID_W + 1, GRID_TOP + 11, 0xFF6E7278, false);
            }
        }

        // ── Bill of materials key (under the live JEI slots) ─────────────────
        bom(g, font, 0, C_CTRL, "x" + ChemicalMixerStructureJeiRecipe.CONTROLLER_COUNT);
        bom(g, font, 1, C_BASE, "x" + ChemicalMixerStructureJeiRecipe.BASE_COUNT);
        bom(g, font, 2, C_WALL, "x" + ChemicalMixerStructureJeiRecipe.WALL_COUNT);
        bom(g, font, 3, C_PORT, "opt.");

        // ── Notes ──────────────────────────────────────────────────────────────
        int ny = BOM_Y + 32;
        g.drawString(font, "Build bottom→top. Controller on any outer border of the floor.", 2, ny,      0xFF9AA0A8, false);
        g.drawString(font, "Layers 2–3 have a hollow 1×1 centre column; layer 4 is a solid cap.",  2, ny + 10, 0xFF9AA0A8, false);
        g.drawString(font, "Any wall position can hold an Energy Port or Fluid Port instead.",   2, ny + 20, 0xFF9AA0A8, false);
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
            case 'C' -> C_CTRL;
            case 'W' -> C_WALL;
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
