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

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 152, topPos + 10 + 52 - barHeight, leftPos + 164, topPos + 10 + 52, 0xFFCC0000);
        }

        int maxFuel = menu.getMaxFuel();
        if (maxFuel > 0) {
            int fuelHeight = menu.getFuelAmount() * 52 / maxFuel;
            gui.fill(leftPos + 10, topPos + 10 + 52 - fuelHeight, leftPos + 22, topPos + 10 + 52, 0xFFFF8C00);
        }

        if (menu.isBurning()) {
            gui.blit(TEXTURE, leftPos + 81, topPos + 36, 176, 0, 14, 14);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);

        String energyText = menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE";
        gui.drawString(font, energyText, leftPos + 8, topPos + 66, 0x404040, false);

        String fuelText = menu.getFuelAmount() + " / " + menu.getMaxFuel() + " mB";
        gui.drawString(font, fuelText, leftPos + 8, topPos + 74, 0x404040, false);
    }
}
