package com.sio.firearms.screen;

import com.sio.firearms.menu.RefineryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RefineryScreen extends AbstractContainerScreen<RefineryMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/refinery.png");

    private static final int TANK_Y      = 6;
    private static final int TANK_H      = 80;
    private static final int TANK_W      = 10;
    private static final int ENERGY_X    = 7;
    private static final int OIL_X       = 22;
    private static final int[] OUT_TANK_X = { 57, 70, 83, 96, 109, 122, 135 };
    private static final int OUT_TANK_CAP = 5_000;
    private static final int OIL_CAP      = 10_000;

    private static final int[] OUT_COLORS = {
        0xFFB8D4E8,  // butane
        0xFFFFE680,  // gasoline
        0xFFFFCC44,  // naphtha
        0xFFFF9900,  // kerosene
        0xFFCC7700,  // diesel
        0xFF8B4513,  // heavy gas oil
        0xFF3D1C00,  // residual fuel oil
    };
    private static final String[] OUT_NAMES = {
        "Butane", "Gasoline", "Naphtha", "Kerosene", "Diesel", "Heavy Gas Oil", "Residual Fuel Oil"
    };

    public RefineryScreen(RefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 200;
        this.inventoryLabelY = 115;
        this.titleLabelY = 200; // suppress title (off-screen)
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int bottom = topPos + TANK_Y + TANK_H;

        // Energy bar (red, fills bottom-up)
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * TANK_H / maxE;
            if (h > 0) gui.fill(leftPos + ENERGY_X, bottom - h, leftPos + ENERGY_X + TANK_W, bottom, 0xFFCC0000);
        }

        // Oil input bar (dark brown, fills bottom-up)
        int oilH = menu.getOilAmount() * TANK_H / OIL_CAP;
        if (oilH > 0) {
            gui.fill(leftPos + OIL_X, bottom - oilH, leftPos + OIL_X + TANK_W, bottom, 0xFF1A1A1A);
        }

        // Progress arrow (fills left-to-right, 16px wide)
        int maxProg = menu.getMaxProgress();
        if (maxProg > 0 && menu.getProgress() > 0) {
            int arrowW = menu.getProgress() * 16 / maxProg;
            gui.fill(leftPos + 35, topPos + 42, leftPos + 35 + arrowW, topPos + 50, 0xFFFFDC64);
        }

        // 7 output tank bars (fills bottom-up)
        for (int i = 0; i < 7; i++) {
            int amt = menu.getOutputAmount(i);
            if (amt <= 0) continue;
            int h = amt * TANK_H / OUT_TANK_CAP;
            int tx = leftPos + OUT_TANK_X[i];
            gui.fill(tx, bottom - h, tx + TANK_W, bottom, OUT_COLORS[i]);
        }

        // Structure status
        String status = menu.isStructureValid() ? "Structure: Valid" : "Structure: Incomplete";
        int statusColor = menu.isStructureValid() ? 0x55FF55 : 0xFF5555;
        gui.drawString(font, status, leftPos + 7, topPos + 92, statusColor, false);

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xAAAAAA, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        super.renderTooltip(gui, mx, my);

        int bottom = topPos + TANK_Y + TANK_H + 1;
        int top    = topPos + TANK_Y - 1;

        // Energy tooltip
        if (mx >= leftPos + ENERGY_X - 1 && mx <= leftPos + ENERGY_X + TANK_W + 1
                && my >= top && my <= bottom) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }

        // Oil tooltip
        if (mx >= leftPos + OIL_X - 1 && mx <= leftPos + OIL_X + TANK_W + 1
                && my >= top && my <= bottom) {
            int oilAmt = menu.getOilAmount();
            String oilText = oilAmt == 0 ? "Oil: Empty" : "Oil: " + oilAmt + "/" + OIL_CAP + " mB";
            gui.renderTooltip(font, Component.literal(oilText), mx, my);
            return;
        }

        // Output tank tooltips
        for (int i = 0; i < 7; i++) {
            int tx = leftPos + OUT_TANK_X[i];
            if (mx >= tx - 1 && mx <= tx + TANK_W + 1 && my >= top && my <= bottom) {
                int outAmt = menu.getOutputAmount(i);
                String outText = outAmt == 0 ? OUT_NAMES[i] + ": Empty"
                        : OUT_NAMES[i] + ": " + outAmt + "/" + OUT_TANK_CAP + " mB";
                gui.renderTooltip(font, Component.literal(outText), mx, my);
                return;
            }
        }
    }
}
