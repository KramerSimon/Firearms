package com.sio.firearms.screen;

import com.sio.firearms.menu.EBFMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Read-only status panel for the Electric Blast Furnace. Drawn procedurally (no slot
 * texture) since all item handling happens through the import / output buses.
 */
public class EBFScreen extends AbstractContainerScreen<EBFMenu> {

    private static final int W = 176, H = 196;
    private static final int FE_PER_TICK = 200;

    private Button toggleButton;

    // palette
    private static final int PANEL    = 0xFF1E2024;
    private static final int PANEL_HI = 0xFF34383F;
    private static final int PANEL_LO = 0xFF0E0F12;
    private static final int FRAME    = 0xFF3A3E45;
    private static final int SLOT_BG  = 0xFF101216;
    private static final int LABEL    = 0xFF7E848C;
    private static final int VALUE    = 0xFFD7DBE0;
    private static final int GREEN    = 0xFF4FC85A;
    private static final int RED      = 0xFFD15440;
    private static final int ORANGE   = 0xFFFF8A2A;

    public EBFScreen(EBFMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = W;
        this.imageHeight = H;
    }

    @Override
    protected void init() {
        super.init();
        toggleButton = addRenderableWidget(Button.builder(
                buttonLabel(),
                b -> {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, EBFMenu.BUTTON_TOGGLE);
                    }
                })
                .bounds(leftPos + 12, topPos + 170, W - 24, 18)
                .build());
    }

    private Component buttonLabel() {
        return Component.literal(menu.isEnabled() ? "■ STOP" : "▶ START");
    }

    private String coilName(int temp) {
        return switch (temp) {
            case 800  -> "Kanthal";
            case 1200 -> "Nichrome";
            case 2000 -> "Tungsten";
            default   -> "None";
        };
    }

    private int tempColor(int temp) {
        return temp >= 2000 ? 0xFFEE5544 : temp >= 1200 ? 0xFFFF8A2A : temp > 0 ? 0xFFFFB347 : LABEL;
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

        boolean formed = menu.isStructureValid();
        int coilTemp = menu.getCoilTemperature();
        int reqTemp  = menu.getRequiredTemp();

        // ── status badge ────────────────────────────────────────────────────────
        section(g, x + 7, y + 22, W - 14, 22);
        g.drawString(font, "STRUCTURE", x + 12, y + 28, LABEL, false);
        String badge = formed ? "● FORMED" : "● INCOMPLETE";
        g.drawString(font, badge, x + W - 12 - font.width(badge), y + 28, formed ? GREEN : RED, false);

        // ── coil / temperature info ──────────────────────────────────────────────
        section(g, x + 7, y + 48, W - 14, 32);
        g.drawString(font, "COIL", x + 12, y + 53, LABEL, false);
        g.drawString(font, coilName(coilTemp), x + 40, y + 53, formed ? VALUE : LABEL, false);
        String maxT = "MAX " + coilTemp + "°C";
        g.drawString(font, maxT, x + W - 12 - font.width(maxT), y + 53, tempColor(coilTemp), false);
        g.drawString(font, "RECIPE", x + 12, y + 66, LABEL, false);
        String req = reqTemp > 0 ? "needs " + reqTemp + "°C" : "no load";
        g.drawString(font, req, x + W - 12 - font.width(req), y + 66, reqTemp > 0 ? tempColor(reqTemp) : LABEL, false);

        // ── bars ─────────────────────────────────────────────────────────────────
        int maxE = menu.getMaxEnergy();
        bar(g, x + 12, y + 92, W - 24, "ENERGY", maxE > 0 ? (float) menu.getEnergyStored() / maxE : 0f,
                0xFF2FBF4A, 0xFF103019,
                menu.getEnergyStored() + " / " + maxE + " FE");

        float tempFrac = Math.min(1f, coilTemp / 2000f);
        bar(g, x + 12, y + 118, W - 24, "TEMPERATURE", tempFrac, 0xFFF26A1B, 0xFF301505,
                coilTemp + " / 2000 °C");

        int maxP = menu.getMaxProgress();
        bar(g, x + 12, y + 144, W - 24, "PROGRESS", maxP > 0 ? (float) menu.getProgress() / maxP : 0f,
                0xFFE0B43A, 0xFF302608,
                statusText());
    }

    private String statusText() {
        if (!menu.isStructureValid()) return "OFFLINE";
        if (!menu.isEnabled()) return "STOPPED";
        if (menu.isActive()) return "SMELTING";
        if (menu.getRequiredTemp() > 0 && menu.getCoilTemperature() < menu.getRequiredTemp()) return "COIL TOO COLD";
        if (menu.getEnergyStored() < FE_PER_TICK) return "NO POWER";
        return "IDLE";
    }

    private void section(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, SLOT_BG);
        g.fill(x, y, x + w, y + 1, PANEL_LO);
        g.fill(x, y, x + 1, y + h, PANEL_LO);
        g.fill(x, y + h - 1, x + w, y + h, PANEL_HI);
        g.fill(x + w - 1, y, x + w, y + h, PANEL_HI);
    }

    /** Labelled progress bar with the value centred on the fill. */
    private void bar(GuiGraphics g, int x, int y, int w, String label, float frac, int fill, int bg, String value) {
        g.drawString(font, label, x, y, LABEL, false);
        int by = y + 10, h = 10;
        g.fill(x, by, x + w, by + h, bg);
        g.fill(x, by, x + w, by + 1, PANEL_LO);
        int fw = (int) (Math.max(0f, Math.min(1f, frac)) * (w - 2));
        if (fw > 0) {
            g.fill(x + 1, by + 1, x + 1 + fw, by + h - 1, fill);
            g.fill(x + 1, by + 1, x + 1 + fw, by + 2, shade(fill, 40));
        }
        g.fill(x, by, x + 1, by + h, FRAME);
        g.fill(x + w - 1, by, x + w, by + h, FRAME);
        g.fill(x, by + h - 1, x + w, by + h, FRAME);
        int tx = x + w / 2 - font.width(value) / 2;
        g.drawString(font, value, tx, by + 1, 0xFFFFFFFF, false);
    }

    private static int shade(int argb, int d) {
        int a = (argb >>> 24) & 0xFF;
        int r = Math.min(255, ((argb >> 16) & 0xFF) + d);
        int gg = Math.min(255, ((argb >> 8) & 0xFF) + d);
        int b = Math.min(255, (argb & 0xFF) + d);
        return (a << 24) | (r << 16) | (gg << 8) | b;
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, 8, 6, 0xFFE8D7A0, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (toggleButton != null) toggleButton.setMessage(buttonLabel());
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }
}
