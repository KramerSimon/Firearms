package com.sio.firearms.screen;

import com.sio.firearms.menu.RefuelStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class RefuelStationScreen extends AbstractContainerScreen<RefuelStationMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/refuel_station.png");

    private static final int BAR_X = 80;
    private static final int BAR_Y = 14;
    private static final int BAR_W = 16;
    private static final int BAR_H = 52;

    public RefuelStationScreen(RefuelStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    private static String fluidName(int fluidId) {
        if (fluidId <= 0) return null;
        var fluid = BuiltInRegistries.FLUID.byId(fluidId);
        if (fluid == null || fluid == Fluids.EMPTY) return null;
        return new FluidStack(fluid, 1).getHoverName().getString();
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int cap = menu.getCapacity();
        int amount = menu.getFluidAmount();
        if (cap > 0 && amount > 0) {
            int h = amount * BAR_H / cap;
            gui.fill(leftPos + BAR_X, topPos + BAR_Y + BAR_H - h,
                     leftPos + BAR_X + BAR_W, topPos + BAR_Y + BAR_H, 0xFFCCA030);
        }

        String status = menu.getStatusText();
        int statusColor = menu.getStatus() == 0 ? 0xFFAAAAAA : 0xFF55CC55;
        gui.drawString(font, status, 8, 18, statusColor, false);

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        int ax = leftPos + BAR_X;
        int ay = topPos + BAR_Y;
        if (mx >= ax && mx < ax + BAR_W && my >= ay && my < ay + BAR_H) {
            String text;
            int amount = menu.getFluidAmount();
            if (amount == 0) {
                text = "Empty";
            } else {
                String name = fluidName(menu.getFluidTypeId());
                text = (name != null ? name + ": " : "") + amount + "/" + menu.getCapacity() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
