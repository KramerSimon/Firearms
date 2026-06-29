package com.sio.firearms.screen;

import com.sio.firearms.menu.CoolingTowerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CoolingTowerScreen extends AbstractContainerScreen<CoolingTowerMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/cooling_tower.png");

    private static final int BAR_H   = 52;
    private static final int BAR_W   = 12;
    private static final int BAR_Y   = 14;
    private static final int STEAM_X = 8;
    private static final int WATER_X = 24;
    private static final int TEXT_X  = 44;

    public CoolingTowerScreen(CoolingTowerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mx, int my) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);

        // Steam bar (light gray, bottom-up)
        int steamH = menu.getSteamMax() > 0
                ? Math.max(1, menu.getSteamAmount() * BAR_H / menu.getSteamMax()) : 0;
        if (steamH > 0) {
            gui.fill(leftPos + STEAM_X, topPos + BAR_Y + BAR_H - steamH,
                     leftPos + STEAM_X + BAR_W, topPos + BAR_Y + BAR_H, 0xFFCCCCCC);
        }

        // Water bar (blue, bottom-up)
        int waterH = menu.getWaterMax() > 0
                ? Math.max(1, menu.getWaterAmount() * BAR_H / menu.getWaterMax()) : 0;
        if (waterH > 0) {
            gui.fill(leftPos + WATER_X, topPos + BAR_Y + BAR_H - waterH,
                     leftPos + WATER_X + BAR_W, topPos + BAR_Y + BAR_H, 0xFF2244CC);
        }

        // Status text — always visible regardless of bar fill
        int tx = leftPos + TEXT_X;
        int ty = topPos + BAR_Y;
        gui.drawString(font, "FE/t: " + menu.getFeRate(),                         tx, ty,      0xFFFFFF, false);
        gui.drawString(font, "Turbines: " + menu.getTurbineCount(),                tx, ty + 10, 0xFFFFFF, false);
        gui.drawString(font, menu.isStructureValid() ? "§aONLINE" : "§cOFFLINE", tx, ty + 20, 0xFFFFFF, false);

        renderTooltip(gui, mx, my);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        if (inBar(mx, my, STEAM_X)) {
            gui.renderTooltip(font,
                    Component.literal("Steam: " + menu.getSteamAmount() + " / " + menu.getSteamMax() + " mB"),
                    mx, my);
            return;
        }
        if (inBar(mx, my, WATER_X)) {
            gui.renderTooltip(font,
                    Component.literal("Water: " + menu.getWaterAmount() + " / " + menu.getWaterMax() + " mB"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }

    private boolean inBar(int mx, int my, int barX) {
        return mx >= leftPos + barX && mx < leftPos + barX + BAR_W
                && my >= topPos + BAR_Y && my < topPos + BAR_Y + BAR_H;
    }
}
