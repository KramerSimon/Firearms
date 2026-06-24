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

    public RefineryScreen(RefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 10, topPos + 20 + 52 - barHeight, leftPos + 20, topPos + 20 + 52, 0xFFCC0000);
        }

        int oilHeight = menu.getOilAmount() * 52 / 10_000;
        gui.fill(leftPos + 50, topPos + 20 + 52 - oilHeight, leftPos + 60, topPos + 20 + 52, 0xFF1A1A1A);

        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && menu.getProgress() > 0) {
            int arrowWidth = menu.getProgress() * 24 / maxProgress;
            gui.blit(TEXTURE, leftPos + 80, topPos + 35, 176, 0, arrowWidth, 16);
        }

        int fuelHeight = menu.getFuelAmount() * 52 / 10_000;
        gui.fill(leftPos + 110, topPos + 20 + 52 - fuelHeight, leftPos + 120, topPos + 20 + 52, 0xFFFF8C00);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);

        gui.drawString(font, "FE", leftPos + 10, topPos + 10, 0x404040, false);
        gui.drawString(font, "Oil", leftPos + 48, topPos + 10, 0x404040, false);
        gui.drawString(font, "Fuel", leftPos + 106, topPos + 10, 0x404040, false);

        String status = menu.isStructureValid() ? "Structure: Valid" : "Structure: Incomplete";
        int statusColor = menu.isStructureValid() ? 0x00AA00 : 0xAA0000;
        gui.drawString(font, status, leftPos + 46, topPos + 74, statusColor, false);
    }
}
