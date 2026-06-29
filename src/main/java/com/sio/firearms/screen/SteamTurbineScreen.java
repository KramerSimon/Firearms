package com.sio.firearms.screen;

import com.sio.firearms.menu.SteamTurbineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SteamTurbineScreen extends AbstractContainerScreen<SteamTurbineMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/steam_turbine.png");

    private static final int BAR_H  = 52;
    private static final int STEAM_X = 80;
    private static final int STEAM_Y = 14;
    private static final int STEAM_W = 16;

    public SteamTurbineScreen(SteamTurbineMenu menu, Inventory playerInventory, Component title) {
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
                ? menu.getSteamAmount() * BAR_H / menu.getSteamMax() : 0;
        if (steamH > 0) {
            gui.fill(leftPos + STEAM_X, topPos + STEAM_Y + BAR_H - steamH,
                     leftPos + STEAM_X + STEAM_W, topPos + STEAM_Y + BAR_H, 0xFFCCCCCC);
        }

        // Text info
        int tx = leftPos + 112;
        gui.drawString(font, "FE/t: " + menu.getFeRate(), tx, topPos + 16, 0xFFFFFF, false);

        renderTooltip(gui, mx, my);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        int sx0 = leftPos + STEAM_X, sy0 = topPos + STEAM_Y;
        if (mx >= sx0 && mx < sx0 + STEAM_W && my >= sy0 && my < sy0 + BAR_H) {
            gui.renderTooltip(font,
                    Component.literal("Steam: " + menu.getSteamAmount() + " / " + menu.getSteamMax() + " mB"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
