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

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Burn indicator (14×14 at 79,34)
        if (mx >= leftPos + 79 && mx < leftPos + 93 && my >= topPos + 34 && my < topPos + 48) {
            gui.renderTooltip(font,
                    Component.literal(menu.isBurning() ? "Burning" : "Not Burning"),
                    mx, my);
            return;
        }
        // Fuel bar (7–19, top 14, bottom 66)
        if (mx >= leftPos + 7 && mx < leftPos + 19 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Fuel: " + menu.getFuelAmount() + " / " + menu.getMaxFuel() + " mB"),
                    mx, my);
            return;
        }
        // Energy bar (150–162, top 14, bottom 66)
        if (mx >= leftPos + 150 && mx < leftPos + 162 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
