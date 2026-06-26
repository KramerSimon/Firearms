package com.sio.firearms.screen;

import com.sio.firearms.menu.OilDerrickMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OilDerrickScreen extends AbstractContainerScreen<OilDerrickMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/oil_derrick.png");

    public OilDerrickScreen(OilDerrickMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 7, topPos + 66 - barHeight, leftPos + 19, topPos + 66, 0xFFCC0000);
        }

        int maxFluid = menu.getMaxFluid();
        if (maxFluid > 0) {
            int barHeight = menu.getFluidAmount() * 52 / maxFluid;
            gui.fill(leftPos + 150, topPos + 66 - barHeight, leftPos + 162, topPos + 66, 0xFF1A1A1A);
        }

        String status = menu.isStructureValid() ? "Structure: Valid" : "Structure: Incomplete";
        int statusColor = menu.isStructureValid() ? 0x00AA00 : 0xAA0000;
        gui.drawString(font, status, leftPos + 40, topPos + 80, statusColor, false);
        gui.drawString(font, menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE",
                leftPos + 7, topPos + 75, 0x404040, false);
        gui.drawString(font, menu.getFluidAmount() + " / " + menu.getMaxFluid() + " mB",
                leftPos + 120, topPos + 75, 0x404040, false);

        renderTooltip(gui, mouseX, mouseY);
    }
}
