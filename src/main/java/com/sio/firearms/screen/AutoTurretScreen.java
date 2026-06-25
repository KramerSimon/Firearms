package com.sio.firearms.screen;

import com.sio.firearms.menu.AutoTurretMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutoTurretScreen extends AbstractContainerScreen<AutoTurretMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/auto_turret.png");

    public AutoTurretScreen(AutoTurretMenu menu, Inventory playerInventory, Component title) {
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
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);

        String energyText = menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE";
        gui.drawString(font, energyText, leftPos + 8, topPos + 66, 0x404040, false);

        String ammoText = "Ammo: " + menu.getAmmoCount();
        gui.drawString(font, ammoText, leftPos + 8, topPos + 56, 0x404040, false);

        String status = menu.isActive() ? "Active" : "Inactive";
        int statusColor = menu.isActive() ? 0x00AA00 : 0xAA0000;
        gui.drawString(font, status, leftPos + 110, topPos + 56, statusColor, false);
    }
}
