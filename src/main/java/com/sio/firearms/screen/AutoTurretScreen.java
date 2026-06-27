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
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 150, topPos + 66 - barHeight, leftPos + 162, topPos + 66, 0xFFCC0000);
        }

        String status = menu.isActive() ? "Active" : "Inactive";
        int statusColor = menu.isActive() ? 0x00AA00 : 0xAA0000;
        gui.drawString(font, status, leftPos + 40, topPos + 40, statusColor, false);
        gui.drawString(font, "Ammo: " + menu.getAmmoCount(), leftPos + 40, topPos + 55, 0x404040, false);

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
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
