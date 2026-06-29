package com.sio.firearms.screen;

import com.sio.firearms.menu.EbfBusMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Read-only display panel for an EBF item hatch buffer. Drawn procedurally to match
 * the controller's status panel; slots show contents but cannot be interacted with.
 */
public class EbfBusScreen extends AbstractContainerScreen<EbfBusMenu> {

    private static final int W = 176, H = 96;

    private static final int PANEL    = 0xFF1E2024;
    private static final int PANEL_HI = 0xFF34383F;
    private static final int PANEL_LO = 0xFF0E0F12;
    private static final int FRAME    = 0xFF3A3E45;
    private static final int SLOT_BG  = 0xFF101216;
    private static final int LABEL    = 0xFF7E848C;

    public EbfBusScreen(EbfBusMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = W;
        this.imageHeight = H;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;

        // chassis
        g.fill(x, y, x + W, y + H, PANEL);
        g.fill(x, y, x + W, y + 1, PANEL_HI);
        g.fill(x, y, x + 1, y + H, PANEL_HI);
        g.fill(x, y + H - 1, x + W, y + H, PANEL_LO);
        g.fill(x + W - 1, y, x + W, y + H, PANEL_LO);

        // title underline
        g.fill(x + 8, y + 17, x + W - 8, y + 18, FRAME);

        // slot cells — must line up with EbfBusMenu slot positions (62+col*18, 22+row*18)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = x + 62 + col * 18, sy = y + 22 + row * 18;
                g.fill(sx - 1, sy - 1, sx + 17, sy + 17, SLOT_BG);
                g.fill(sx - 1, sy - 1, sx + 17, sy, PANEL_LO);
                g.fill(sx - 1, sy - 1, sx, sy + 17, PANEL_LO);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, 8, 6, 0xFFE8D7A0, false);
        Component hint = Component.translatable("gui.firearms.ebf_bus.hint");
        g.drawString(font, hint, 8, H - 12, LABEL, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }
}
