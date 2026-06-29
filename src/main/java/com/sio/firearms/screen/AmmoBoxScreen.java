package com.sio.firearms.screen;

import com.sio.firearms.menu.AmmoBoxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AmmoBoxScreen extends AbstractContainerScreen<AmmoBoxMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/ammo_box.png");

    public AmmoBoxScreen(AmmoBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        // Count display
        int stored = menu.getStoredCount();
        int max    = menu.getMaxTotal();
        String countText = stored + " / " + max;
        gui.drawString(font, countText, leftPos + 100, topPos + 35, 0x404040, false);

        renderTooltip(gui, mouseX, mouseY);
    }
}
