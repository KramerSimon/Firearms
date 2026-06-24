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

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 10, topPos + 10 + 52 - barHeight, leftPos + 22, topPos + 10 + 52, 0xFFCC0000);
        }

        int maxFluid = menu.getMaxFluid();
        if (maxFluid > 0) {
            int barHeight = menu.getFluidAmount() * 52 / maxFluid;
            gui.fill(leftPos + 152, topPos + 10 + 52 - barHeight, leftPos + 164, topPos + 10 + 52, 0xFF1A1A1A);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);

        String status = menu.isStructureValid() ? "Structure: Valid" : "Structure: Incomplete";
        int statusColor = menu.isStructureValid() ? 0x00AA00 : 0xAA0000;
        gui.drawString(font, status, leftPos + 30, topPos + 15, statusColor, false);

        String energyText = menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE";
        gui.drawString(font, energyText, leftPos + 30, topPos + 30, 0x404040, false);

        String fluidText = menu.getFluidAmount() + " / " + menu.getMaxFluid() + " mB";
        gui.drawString(font, fluidText, leftPos + 30, topPos + 45, 0x404040, false);
    }
}
