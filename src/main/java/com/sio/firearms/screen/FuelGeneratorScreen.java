package com.sio.firearms.screen;

import com.sio.firearms.menu.FuelGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FuelGeneratorScreen extends AbstractContainerScreen<FuelGeneratorMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/fuel_generator.png");

    public FuelGeneratorScreen(FuelGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.isBurning()) {
            gui.blit(TEXTURE, leftPos + 79, topPos + 34, 176, 0, 14, 14);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxFuel = menu.getMaxFuel();
        if (maxFuel > 0) {
            int fuelHeight = menu.getFuelAmount() * 52 / maxFuel;
            gui.fill(leftPos + 7, topPos + 66 - fuelHeight, leftPos + 19, topPos + 66, 0xFFFF8C00);
        }

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 150, topPos + 66 - barHeight, leftPos + 162, topPos + 66, 0xFFCC0000);
        }

        gui.drawString(font, menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE",
                leftPos + 7, topPos + 75, 0x404040, false);
        gui.drawString(font, menu.getFuelAmount() + " / " + menu.getMaxFuel() + " mB",
                leftPos + 100, topPos + 75, 0x404040, false);

        renderTooltip(gui, mouseX, mouseY);
    }
}
