package com.sio.firearms.screen;

import com.sio.firearms.menu.AcidBathMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AcidBathScreen extends AbstractContainerScreen<AcidBathMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/acid_bath.png");

    public AcidBathScreen(AcidBathMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int maxP = menu.getMaxProgress();
        if (maxP > 0 && menu.getProgress() > 0) {
            int w = menu.getProgress() * 24 / maxP;
            gui.blit(TEXTURE, leftPos + 80, topPos + 35, 176, 0, w, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        // energy bar (red)
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * 52 / maxE;
            gui.fill(leftPos + 8, topPos + 66 - h, leftPos + 20, topPos + 66, 0xFFCC0000);
        }

        // acid bar (yellow-green — sulfuric acid colour)
        int maxA = menu.getAcidMax();
        if (maxA > 0 && menu.getAcidAmount() > 0) {
            int h = menu.getAcidAmount() * 52 / maxA;
            gui.fill(leftPos + 27, topPos + 66 - h, leftPos + 39, topPos + 66, 0xFF9DB500);
        }

        renderTooltip(gui, mouseX, mouseY);
    }
}
