package com.sio.firearms.screen;

import com.sio.firearms.block.FluidPortBlockEntity;
import com.sio.firearms.menu.FluidPortConfigMenu;
import com.sio.firearms.network.SetFluidPortModePayload;
import com.sio.firearms.network.SetFluidPortTargetPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class FluidPortConfigScreen extends AbstractContainerScreen<FluidPortConfigMenu> {

    /** Ghost ingredient drop zone, relative to leftPos/topPos — read by JEI ghost handler. */
    public static final int GHOST_X    = 70;
    public static final int GHOST_Y    = 48;
    public static final int GHOST_SIZE = 36;

    private Button inputBtn;
    private Button outputBtn;

    public FluidPortConfigScreen(FluidPortConfigMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 130;
    }

    @Override
    protected void init() {
        super.init();

        inputBtn = addRenderableWidget(Button.builder(
                Component.literal("INPUT"),
                btn -> applyMode(false)
        ).bounds(leftPos + 8, topPos + 26, 72, 18).build());

        outputBtn = addRenderableWidget(Button.builder(
                Component.literal("OUTPUT"),
                btn -> applyMode(true)
        ).bounds(leftPos + 88, topPos + 26, 72, 18).build());

        addRenderableWidget(Button.builder(
                Component.literal("Clear (Any)"),
                btn -> {
                    PacketDistributor.sendToServer(
                            new SetFluidPortTargetPayload(menu.pos, Optional.empty()));
                    menu.targetFluid = "any";
                }
        ).bounds(leftPos + 8, topPos + 104, 100, 18).build());

        refreshModeButtons();
    }

    private void applyMode(boolean outputMode) {
        PacketDistributor.sendToServer(new SetFluidPortModePayload(menu.pos, outputMode));
        menu.mode = outputMode ? FluidPortBlockEntity.Mode.OUTPUT : FluidPortBlockEntity.Mode.INPUT;
        refreshModeButtons();
    }

    private void refreshModeButtons() {
        if (inputBtn  != null) inputBtn.active  = (menu.mode != FluidPortBlockEntity.Mode.INPUT);
        if (outputBtn != null) outputBtn.active = (menu.mode != FluidPortBlockEntity.Mode.OUTPUT);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        g.fill(leftPos,     topPos,     leftPos + imageWidth,     topPos + imageHeight,     0xD0101018);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF1A1A2E);

        // Drop zone
        int zx = leftPos + GHOST_X;
        int zy = topPos  + GHOST_Y;
        g.fill(zx - 2, zy - 2, zx + GHOST_SIZE + 2, zy + GHOST_SIZE + 2, 0xFF4A4A6A);
        g.fill(zx,     zy,     zx + GHOST_SIZE,     zy + GHOST_SIZE,     0xFF0D0D1F);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(font, "Fluid Port Config", 8, 8, 0xFFCCCCDD, false);
        g.drawString(font, "Mode:", 8, 18, 0xFF999999, false);
        g.drawString(font, "Target Fluid (drag from JEI):", 8, 42, 0xFF999999, false);

        String label = menu.targetFluid.equals("any") ? "Any" : menu.targetFluid;
        g.drawString(font, label, GHOST_X + GHOST_SIZE + 6, GHOST_Y + 14, 0xFFEEEEFF, false);
        g.drawString(font, "Drop here", GHOST_X + 4, GHOST_Y + GHOST_SIZE + 4, 0xFF666677, false);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        super.render(g, mx, my, partial);
        renderTooltip(g, mx, my);
    }
}
