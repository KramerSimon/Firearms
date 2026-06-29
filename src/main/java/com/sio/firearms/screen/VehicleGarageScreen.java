package com.sio.firearms.screen;

import com.sio.firearms.menu.VehicleGarageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VehicleGarageScreen extends AbstractContainerScreen<VehicleGarageMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/vehicle_garage.png");

    // Energy bar (right side)
    private static final int ENERGY_X = 152;
    private static final int ENERGY_Y = 14;
    private static final int ENERGY_W = 12;
    private static final int ENERGY_H = 52;

    // Progress bar
    private static final int PROG_X = 8;
    private static final int PROG_Y = 60;
    private static final int PROG_W = 120;
    private static final int PROG_H = 8;

    public VehicleGarageScreen(VehicleGarageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 200;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mx, int my) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Energy bar (orange, bottom-up)
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = Math.max(0, menu.getEnergyStored() * ENERGY_H / maxE);
            if (h > 0) {
                gui.fill(leftPos + ENERGY_X,
                         topPos + ENERGY_Y + ENERGY_H - h,
                         leftPos + ENERGY_X + ENERGY_W,
                         topPos + ENERGY_Y + ENERGY_H,
                         0xFFFF8800);
            }
        }

        // Progress bar (green, left-right)
        if (menu.getBuildProgress() > 0) {
            int w = menu.getBuildProgress() * PROG_W / 200;
            if (w > 0) {
                gui.fill(leftPos + PROG_X,
                         topPos + PROG_Y,
                         leftPos + PROG_X + w,
                         topPos + PROG_Y + PROG_H,
                         0xFF44CC44);
            }
        }
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);

        int tx = leftPos + 8;
        int ty = topPos + 70;
        boolean valid = menu.isStructureValid();
        gui.drawString(font, valid ? "§aStructure: Valid" : "§cStructure: Invalid", tx, ty, 0xFFFFFF, false);

        int prog = menu.getBuildProgress();
        if (prog > 0) {
            gui.drawString(font, "Building: " + prog + " / 200", tx, ty + 10, 0xFFFFFF, false);
        } else if (valid) {
            gui.drawString(font, "Awaiting items + power", tx, ty + 10, 0xAAAAAA, false);
        }

        renderTooltip(gui, mx, my);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        if (mx >= leftPos + ENERGY_X && mx < leftPos + ENERGY_X + ENERGY_W
                && my >= topPos + ENERGY_Y && my < topPos + ENERGY_Y + ENERGY_H) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        if (mx >= leftPos + PROG_X && mx < leftPos + PROG_X + PROG_W
                && my >= topPos + PROG_Y && my < topPos + PROG_Y + PROG_H) {
            gui.renderTooltip(font,
                    Component.literal("Build Progress: " + menu.getBuildProgress() + " / 200 ticks"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
