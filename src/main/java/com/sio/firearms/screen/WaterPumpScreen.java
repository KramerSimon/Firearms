package com.sio.firearms.screen;

import com.sio.firearms.menu.WaterPumpMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WaterPumpScreen extends AbstractContainerScreen<WaterPumpMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/water_pump.png");

    public WaterPumpScreen(WaterPumpMenu menu, Inventory playerInventory, Component title) {
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

        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * 52 / maxE;
            gui.fill(leftPos + 8, topPos + 66 - h, leftPos + 20, topPos + 66, 0xFFCC0000);
        }

        int maxW = menu.getWaterMax();
        if (maxW > 0 && menu.getWaterAmount() > 0) {
            int h = menu.getWaterAmount() * 52 / maxW;
            gui.fill(leftPos + 27, topPos + 66 - h, leftPos + 39, topPos + 66, 0xFF3399FF);
        }

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Energy bar (8–20, top 14, bottom 66)
        if (mx >= leftPos + 8 && mx < leftPos + 20 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        // Water bar (27–39, top 14, bottom 66)
        if (mx >= leftPos + 27 && mx < leftPos + 39 && my >= topPos + 14 && my <= topPos + 66) {
            int amt = menu.getWaterAmount();
            String text = amt == 0 ? "Water: Empty"
                    : "Water: " + amt + "/" + menu.getWaterMax() + " mB";
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
