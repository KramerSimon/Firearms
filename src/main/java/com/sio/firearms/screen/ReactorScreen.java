package com.sio.firearms.screen;

import com.sio.firearms.menu.ReactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReactorScreen extends AbstractContainerScreen<ReactorMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/reactor.png");

    private static final int MAX_TEMP = 1000;

    // Bar dimensions (GUI-relative coords)
    private static final int BAR_Y = 20;
    private static final int BAR_W = 12;
    private static final int BAR_H = 80;

    // Bar X positions
    private static final int WATER_X = 130;
    private static final int STEAM_X = 150;
    private static final int TEMP_X  = 170;

    // Text area
    private static final int TEXT_X = 130;
    private static final int TEXT_Y = 110;

    public ReactorScreen(ReactorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 230;
        this.imageHeight = 200;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY     = 5;
        this.inventoryLabelY = 110;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mx, int my) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);

        // Water bar (blue, bottom-up)
        int waterH = menu.getWaterMax() > 0
                ? Math.max(1, menu.getWaterAmount() * BAR_H / menu.getWaterMax()) : 0;
        if (waterH > 0) {
            gui.fill(leftPos + WATER_X,
                     topPos  + BAR_Y + BAR_H - waterH,
                     leftPos + WATER_X + BAR_W,
                     topPos  + BAR_Y + BAR_H,
                     0xFF2244CC);
        }

        // Steam bar (light gray, bottom-up)
        int steamH = menu.getSteamMax() > 0
                ? Math.max(1, menu.getSteamAmount() * BAR_H / menu.getSteamMax()) : 0;
        if (steamH > 0) {
            gui.fill(leftPos + STEAM_X,
                     topPos  + BAR_Y + BAR_H - steamH,
                     leftPos + STEAM_X + BAR_W,
                     topPos  + BAR_Y + BAR_H,
                     0xFFCCCCCC);
        }

        // Temperature bar (red, bottom-up)
        int tempH = MAX_TEMP > 0
                ? Math.max(1, menu.getTemperature() * BAR_H / MAX_TEMP) : 0;
        if (tempH > 0) {
            gui.fill(leftPos + TEMP_X,
                     topPos  + BAR_Y + BAR_H - tempH,
                     leftPos + TEMP_X + BAR_W,
                     topPos  + BAR_Y + BAR_H,
                     0xFFCC2222);
        }

        // Status text block
        int tx = leftPos + TEXT_X;
        int ty = topPos  + TEXT_Y;
        gui.drawString(font, "FE/t:  " + menu.getFeRate(),                 tx, ty,      0xFFFFFF, false);
        gui.drawString(font, "Temp: "  + menu.getTemperature() + "°C", tx, ty + 10, 0xFFFFFF, false);
        gui.drawString(font, menu.isStructureValid() ? "§aONLINE" : "§cOFFLINE", tx, ty + 20, 0xFFFFFF, false);

        // Meltdown warning
        if (menu.getTemperature() > 500) {
            String warn = "§4⚠ MELTDOWN RISK";
            int wx = leftPos + (imageWidth - font.width(warn)) / 2;
            gui.drawString(font, warn, wx, topPos + 95, 0xFFFFFF, false);
        }

        renderTooltip(gui, mx, my);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Water bar
        if (inBar(mx, my, WATER_X)) {
            gui.renderTooltip(font,
                    Component.literal("Water: " + menu.getWaterAmount() + " / " + menu.getWaterMax() + " mB"),
                    mx, my);
            return;
        }
        // Steam bar
        if (inBar(mx, my, STEAM_X)) {
            gui.renderTooltip(font,
                    Component.literal("Steam: " + menu.getSteamAmount() + " / " + menu.getSteamMax() + " mB"),
                    mx, my);
            return;
        }
        // Temperature bar
        if (inBar(mx, my, TEMP_X)) {
            gui.renderTooltip(font,
                    Component.literal("Temperature: " + menu.getTemperature() + " / " + MAX_TEMP + " °C"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }

    private boolean inBar(int mx, int my, int barX) {
        int x0 = leftPos + barX;
        int y0 = topPos  + BAR_Y;
        return mx >= x0 && mx < x0 + BAR_W && my >= y0 && my < y0 + BAR_H;
    }
}
